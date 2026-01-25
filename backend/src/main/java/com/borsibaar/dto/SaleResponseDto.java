package com.borsibaar.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record SaleResponseDto(
        String saleId,
        List<SaleItemResponseDto> items,
        BigDecimal totalAmount,
        String notes,
        OffsetDateTime timestamp) {

    public static SaleResponseDto create(
            String saleId,
            List<SaleItemResponseDto> items,
            BigDecimal totalAmount,
            String notes) {
        return new SaleResponseDto(saleId, items, totalAmount, notes, OffsetDateTime.now());
    }

}