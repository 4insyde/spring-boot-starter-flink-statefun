package com.spring.flink.statefun;

import com.spring.flink.statefun.api.SerDeType;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.types.Type;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Store {@link Type} into hashmap using generic class as key
 */
@RequiredArgsConstructor
public class TypeResolverImpl implements TypeResolver {
    private final Map<Class<?>, Type<?>> types = new HashMap<>();

    private final List<SerDeType<?>> serDeTypes;

    public void put(Class<?> genericClass, Type<?> type) {
        types.put(genericClass, type);
    }

    @Override
    public <T> Type<T> findByClass(Class<T> clazz) {
        return (Type<T>) types.get(clazz);
    }

    @PostConstruct
    private void init() {
        if (serDeTypes != null) {
            Map<? extends Class<?>, ? extends Type<?>> typeMap = serDeTypes.stream().collect(toMap(ReflectionUtil::retrieveGeneric, SerDeType::type));
            types.putAll(typeMap);
        }
    }
}
