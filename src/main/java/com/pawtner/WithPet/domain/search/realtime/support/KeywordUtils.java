package com.pawtner.WithPet.domain.search.realtime.support;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeywordUtils {
    private KeywordUtils(){}

    public static List<String> parse(String keywords) {
        if (keywords == null) return List.of();
        return Arrays.stream(keywords.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
    }

    public static List<String> matched(String haystack, List<String> keywords) {
        if (haystack == null || haystack.isBlank() || keywords == null || keywords.isEmpty())
            return List.of();

        String lower = haystack.toLowerCase();
        return keywords.stream()
                .filter(k -> k != null && !k.isBlank())
                .filter(k -> lower.contains(k.toLowerCase()))
                .collect(Collectors.toList());
    }
}
