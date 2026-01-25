package com.borsibaar.jobs;

import com.borsibaar.entity.Inventory;
import com.borsibaar.entity.InventoryTransaction;
import com.borsibaar.entity.Product;
import com.borsibaar.form.enums.TransactionType;
import com.borsibaar.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PriceCorrectionJob {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;

    public PriceCorrectionJob(InventoryRepository inventoryRepository,
            InventoryTransactionRepository inventoryTransactionRepository,
            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.productRepository = productRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void adjustPrices() {
        System.out.println("Running price reduction job");
        List<Product> inactiveProducts = productRepository.findByActiveOrgAndInactiveSalesLastMinute();

        if (inactiveProducts.isEmpty()) {
            System.out.println("No product prices to update automatically");
            return;
        }

        int updatedCount = 0;
        for (Product product : inactiveProducts) {
            Inventory inventory = Optional.ofNullable(product.getInventory())
                    .orElseGet(() -> {
                        Inventory newInv = new Inventory();
                        newInv.setOrganizationId(product.getOrganizationId());
                        newInv.setProduct(product);
                        newInv.setQuantity(BigDecimal.ZERO);
                        newInv.setAdjustedPrice(product.getBasePrice());
                        newInv.setCreatedAt(OffsetDateTime.now());
                        newInv.setUpdatedAt(OffsetDateTime.now());
                        return newInv;
                    });

            BigDecimal decreaseAmount = product.getOrganization().getPriceDecreaseStep();
            BigDecimal minPrice = Optional.ofNullable(product.getMinPrice()).orElse(decreaseAmount);
            BigDecimal currentPrice = Optional.ofNullable(inventory.getAdjustedPrice()).orElse(product.getBasePrice());
            BigDecimal newPrice = currentPrice.subtract(decreaseAmount);
            if (newPrice.compareTo(minPrice) < 0) {
                newPrice = minPrice;
            }

            if (newPrice.compareTo(currentPrice) == 0) {
                // already at lowest price
                continue;
            }

            inventory.setAdjustedPrice(newPrice);
            inventory.setUpdatedAt(OffsetDateTime.now());
            inventory = inventoryRepository.save(inventory);

            // Create price reduction transaction
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setInventory(inventory);
            transaction.setTransactionType(TransactionType.ADJUSTMENT.name());
            transaction.setQuantityChange(BigDecimal.ZERO);
            transaction.setQuantityBefore(inventory.getQuantity());
            transaction.setQuantityAfter(inventory.getQuantity());
            transaction.setPriceBefore(currentPrice);
            transaction.setPriceAfter(newPrice);
            transaction.setReferenceId("REDUCE-" + System.currentTimeMillis());
            transaction.setNotes("PriceCorrectionJob");
            transaction.setCreatedBy(null);
            transaction.setCreatedAt(OffsetDateTime.now());
            inventoryTransactionRepository.save(transaction);

            updatedCount++;
        }
        System.out.println("Updated prices of " + updatedCount + " products.");
    }
}
