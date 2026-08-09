package com.society.common.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    private static final NumberFormat INDIA_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public static String formatInr(Double amount) {
        if (amount == null) {
            return "₹ 0.00";
        }
        return INDIA_FORMAT.format(amount).replace("INR", "₹").trim();
    }
}
