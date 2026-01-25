package com.borsibaar.form.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TransactionType {
    SALE("SALE");

    @Getter
    private final String type;
}
