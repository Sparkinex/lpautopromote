package com.sparkinex.lpautopromote.utils;

import net.minecraft.network.chat.TextColor;
import net.minecraft.world.phys.Vec3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Toolkit {
    public static String translateColorCodes(String text) {
        Pattern hexPattern = Pattern.compile("&#[a-fA-F0-9]{6}");

        Matcher hexMatcher = hexPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (hexMatcher.find()) {
            String hexCode = hexMatcher.group();
            int rgb = Integer.parseInt(hexCode.substring(2), 16);
            String forgeCode = TextColor.fromRgb(rgb).toString();
            hexMatcher.appendReplacement(sb, forgeCode);
        }
        hexMatcher.appendTail(sb);

        return sb.toString().replace("&", "\u00A7");
    }

    public static boolean almostEqual(Vec3 a, Vec3 b, double eps) {
        return almostEqual(a.x, b.x, eps) && almostEqual(a.y, b.y, eps) && almostEqual(a.z, b.z, eps);
    }

    public static boolean almostEqual(double a, double b, double eps) {
        return Math.abs(a - b) < eps;
    }
}
