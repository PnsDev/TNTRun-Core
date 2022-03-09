package dev.pns.tntrun.utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtil {
    /**
     * Splits a string into multiple lines.
     * This system supports color codes and making new lines with the '\n' character.
     * @param string The string to split
     * @param maxCharsPerLine The maximum number of characters per line
     * @return The split string
     */
    public static List<String> wrap(String string, int maxCharsPerLine) {
        List<String> finalLines = new ArrayList<>();
        List<String> adaptedLines = new ArrayList<>(List.of(StringUtils.split(string, "\n")));

        for (String line : adaptedLines) {
            StringBuilder lineText = new StringBuilder();
            for (String word : StringUtils.split(line, " ")) {
                if (lineText.toString().equals("") || lineText.length() + 1 + word.length() <= maxCharsPerLine) {
                    lineText.append(word).append(" ");
                    continue;
                }
                finalLines.add(lineText.toString().trim());
                lineText = new StringBuilder(ChatColor.getLastColors(finalLines.get(finalLines.size()-1))).append(word).append(" ");
            }
            if (lineText.length() > 0) finalLines.add(lineText.toString().trim());
        }
        return finalLines;
    }

    public static String titleCase(String str) {
        return Arrays
                .stream(str.split(" "))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
