package com.dacia1704.truyenonline.module.administration.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class AuditMapper<T> {

    private final T source;
    private final Map<String, Object> values = new LinkedHashMap<>();

    private AuditMapper(T source) {
        this.source = source;
    }

    public static <T> AuditMapper<T> of(T source) {
        return new AuditMapper<>(source);
    }

    public <R> AuditMapper<T> add(String key, Function<T, R> getter) {
        values.put(key, getter.apply(source));
        return this;
    }

    public Map<String, Object> build() {
        return values;
    }
}
