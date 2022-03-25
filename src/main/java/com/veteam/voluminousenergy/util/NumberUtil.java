package com.veteam.voluminousenergy.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.text.DecimalFormat;

// Graciously provided by MikeTheShadow from his other programming endeavours... Refactored for Voluminous Energy
public class NumberUtil {
    public static String formatNumber(String number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat.format(Double.parseDouble(number));
    }

    public static String formatNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat.format(number);
    }

    private static String removeZeros(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(d);
    }

    public static String numberToString(double num) {
        if (num < 1_000_000d) {
            return formatNumber(removeZeros(num));
        } else if (num < 1_000_000_000d) {
            return removeZeros(num / 1_000_000d) + " Million"; // TODO: Make translatable
        } else if (num < 1_000_000_000_000d) {
            return removeZeros(num / 1_000_000_000d) + " Billion";
        } else {
            return removeZeros(num / 1_000_000_000_000d) + " Trillion";
        }
    }

    public static String numberToString4Fluids(double num) {
        if (num < 1_000) {
            String toReturn = String.valueOf(num);
            toReturn += " mB";
            return toReturn;
        } else if (num >= 1_000 && num < 1_000_000d) {
            return removeZeros(num / 1_000) + " B";
        } else if (num < 1_000_000_000d) {
            return removeZeros(num / 1_000_000d) + " kB";
        } else if (num < 1_000_000_000_000d) {
            return removeZeros(num / 1_000_000_000d) + " MB";
        } else {
            return removeZeros(num / 1_000_000_000_000d) + " GB";
        }
    }

    public static String numberToString4FE(double num) {
        if (num < 1_000) {
            String toReturn = String.valueOf(num);
            toReturn += " FE";
            return toReturn;
        } else if (num >= 1_000 && num < 1_000_000d) {
            return removeZeros(num / 1_000) + " kFE";
        } else if (num < 1_000_000_000d) {
            return removeZeros(num / 1_000_000d) + " MFE";
        } else if (num < 1_000_000_000_000d) {
            return removeZeros(num / 1_000_000_000d) + " GFE";
        } else {
            return removeZeros(num / 1_000_000_000_000d) + " TFE";
        }
    }

    public static Component numberToTextComponent4FE(double num){
        return new TextComponent(numberToString4FE(num));
    }

    public static Component numberToTextComponent4Fluids(double num){
        return new TextComponent(numberToString4Fluids(num));
    }

    public static Component numberToTextComponent(double num){
        return new TextComponent(numberToString(num));
    }

    public static String numberToString(String numString) {
        double num = Double.parseDouble(numString);
        return numberToString(num);
    }
}