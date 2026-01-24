package com.borsibaar.service;

import com.borsibaar.dto.*;
import com.borsibaar.entity.Category;
import com.borsibaar.entity.Inventory;
import com.borsibaar.entity.InventoryTransaction;
import com.borsibaar.entity.Product;
import com.borsibaar.form.enums.TransactionType;
import com.borsibaar.repository.InventoryRepository;
import com.borsibaar.repository.InventoryTransactionRepository;
import com.borsibaar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesService {
        public static final String SALE = "SALE-";
        private final InventoryRepository inventoryRepository;
        private final InventoryTransactionRepository inventoryTransactionRepository;
        private final ProductRepository productRepository;

        @Transactional
        public SaleResponseDto processSale(SaleRequestDto request, UUID userId, Long organizationId) {
                // Generate unique sale reference ID
                String saleId = SALE + System.currentTimeMillis();

                // Process each item in the sale
                List<SaleItemResponseDto> saleItems = request.items().stream()
                        .map(item ->
                                processSaleItem(item, userId, organizationId, saleId, request.barStationId()))
                        .toList();

                BigDecimal totalAmount = saleItems.stream()
                        .map(SaleItemResponseDto::totalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                return SaleResponseDto.create(
                        saleId,
                        saleItems,
                        totalAmount,
                        request.notes());
        }

        private SaleItemResponseDto processSaleItem(SaleItemRequestDto item, UUID userId, Long organizationId,
                        String saleId, Long barStationId) {
                // Verify product exists and belongs to organization
                Product product = productRepository.findById(item.productId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Product not found: " + item.productId()));

                if (!product.getOrganizationId().equals(organizationId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN, "Product does not belong to your organization");
                }

                if (!product.isActive()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST, "Product is not active: " + product.getName());
                }

                // Get inventory for this product
                /*
                 * Inventory inventory = inventoryRepository
                 * .findByOrganizationIdAndProductId(organizationId, item.productId())
                 * .orElseThrow(() -> new ResponseStatusException(
                 * HttpStatus.NOT_FOUND, "No inventory found for product: " +
                 * product.getName()));
                 */
                Inventory inventory = Optional.ofNullable(product.getInventory())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "No inventory found for product: " + product.getName()));

                // Check stock availability
                BigDecimal oldQuantity = inventory.getQuantity();
                BigDecimal newQuantity = oldQuantity.subtract(item.quantity());

                if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Insufficient stock for " + product.getName() +
                                                        ". Available: " + oldQuantity + ", Requested: "
                                                        + item.quantity());
                }

                // Calculate pricing
                BigDecimal priceBeforeSale = Optional.ofNullable(inventory.getAdjustedPrice())
                                .orElse(product.getBasePrice());
                BigDecimal totalPrice = priceBeforeSale.multiply(item.quantity());

                BigDecimal priceAfterSale = priceBeforeSale;
                Category category = product.getCategory();
                if (category != null && category.isDynamicPricing()) {
                        priceAfterSale = priceBeforeSale.add(product.getOrganization().getPriceIncreaseStep());
                        if (product.getMaxPrice() != null && priceAfterSale.compareTo(product.getMaxPrice()) > 0) {
                                priceAfterSale = product.getMaxPrice();
                        }
                }


                // Update inventory
                inventory.setQuantity(newQuantity);
                inventory.setUpdatedAt(OffsetDateTime.now());
                inventory.setAdjustedPrice(priceAfterSale);

                inventory = inventoryRepository.save(inventory);

                // Create sale transaction
                createSaleTransaction(inventory, item.quantity(),
                                oldQuantity, newQuantity, priceBeforeSale, priceAfterSale,
                                saleId, userId, barStationId);

                return new SaleItemResponseDto(
                                item.productId(),
                                product.getName(),
                                item.quantity(),
                                priceBeforeSale,
                                totalPrice);
        }

        private void createSaleTransaction(Inventory inventory, BigDecimal quantity,
                        BigDecimal quantityBefore, BigDecimal quantityAfter,
                        BigDecimal priceBefore, BigDecimal priceAfter,
                        String saleId, UUID userId, Long barStationId) {
                InventoryTransaction transaction = InventoryTransaction.builder()
                        .inventory(inventory)
                        .transactionType(TransactionType.SALE.name())
                        .quantityChange(quantity.negate())
                        .quantityBefore(quantityBefore)
                        .quantityAfter(quantityAfter)
                        .priceBefore(priceBefore)
                        .priceAfter(priceAfter)
                        .referenceId(saleId)
                        .notes("POS Sale")
                        .createdBy(userId)
                        .barStationId(barStationId)
                        .createdAt(OffsetDateTime.now())
                        .build();

                inventoryTransactionRepository.save(transaction);
        }
}