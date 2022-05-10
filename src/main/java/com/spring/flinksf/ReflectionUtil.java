package com.spring.flinksf;

import com.spring.flinksf.api.SerDeType;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {
    @SneakyThrows
    public static Class<?> retrieveGeneric(SerDeType<?> type) {
        Class<? extends SerDeType> aClass = type.getClass();
        Method method = aClass.getMethod("type");
        ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
        Type actualTypeArgument = genericReturnType.getActualTypeArguments()[0];
        return Class.forName(actualTypeArgument.getTypeName());
    }
}
