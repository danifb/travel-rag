package com.danifb.travel_rag.repo.util;

import java.util.List;
import java.util.stream.Collectors;

public class VectorUtils {

    private VectorUtils() {}

    public static String toVectorLiteral(List<Double> embedding) {
        return "[" +
                embedding.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")) +
                "]";
    }
}