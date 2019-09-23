package com.azimo.quokka.aggregator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AmountUtils {

    public static String convertToDecimal(String value) {
        return value != null ? new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).toString() : "";
    }
}
