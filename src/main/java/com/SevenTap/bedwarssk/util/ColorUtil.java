package com.SevenTap.bedwarssk.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColorUtil {
    public static String translateColors(String input) {
        if (input == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> translateColorsList(List<String> input) {
        if (input == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String line : input) {
            result.add(translateColors(line));
        }
        return result;
    }
}
