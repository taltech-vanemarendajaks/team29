package com.borsibaar.form.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TransactionType {
    SALE("SALE"),
    INITIAL("INITIAL"),
    ADJUSTMENT("ADJUSTMENT");

    @Getter
    private final String type;
}