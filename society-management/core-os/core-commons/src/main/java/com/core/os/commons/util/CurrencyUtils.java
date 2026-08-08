package com.core.os.commons.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtils {

    private CurrencyUtils() {}

    public static String formatInr(Double amount) {
        if (amount == null) return "₹ 0.00";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(amount).replace("INR", "₹").trim();
    }
}
