package dev.pns.tntrun.utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtil {
    public static List<String> wrap(String string, int maxCharsPerLine) {
        List<String> lines = new ArrayList<>();
        StringBuilder lineText = new StringBuilder();
        for (String word : StringUtils.split(string, " ")) {
            if (lineText.toString().equals("") || lineText.length() + 1 + word.length() <= maxCharsPerLine) {
                lineText.append(word).append(" ");
                continue;
            }
            String previousColor = ChatColor.getLastColors(lineText.toString());
            lines.add(lineText.toString().trim());
            lineText = new StringBuilder(previousColor).append(word).append(" ");
        }
        if (lineText.length() > 0) lines.add(lineText.toString().trim());
        return lines;
    }

    public static String titleCase(String str) {
        return Arrays
                .stream(str.split(" "))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
