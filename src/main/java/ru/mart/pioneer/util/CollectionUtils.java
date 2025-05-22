package ru.mart.pioneer.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {
    @SafeVarargs
    public static <T> Set<T> mutableSetOf(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}

