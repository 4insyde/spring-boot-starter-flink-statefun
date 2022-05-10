package com.spring.flinksf;

import com.spring.flinksf.api.SerDeType;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;
import java.util.Map;

import static com.spring.flinksf.ReflectionUtil.retrieveGeneric;

@RequiredArgsConstructor
public class TypeResolverImpl implements TypeResolver {
    private final Map<Class<?>, Type<?>> types = new HashMap<>();


    public void put(SerDeType<?> type) {
        types.put(retrieveGeneric(type), type.type());
    }

    @Override
    public <T> Type<T> findByClass(Class<T> clazz) {
        return (Type<T>) types.get(clazz);
    }

}
