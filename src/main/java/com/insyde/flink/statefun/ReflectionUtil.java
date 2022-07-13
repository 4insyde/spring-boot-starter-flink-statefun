package com.insyde.flink.statefun;

import com.insyde.flink.statefun.api.SerDeType;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public abstract class ReflectionUtil {

    /**
     * Retrieve generic type using class
     * @param type input class that will be used to get generic type
     * @return generic type
     */
    @SneakyThrows
    public static Class<?> retrieveGeneric(SerDeType<?> type) {
        Class<? extends SerDeType> aClass = type.getClass();
        Method method = aClass.getMethod("type");
        ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
        Type actualTypeArgument = genericReturnType.getActualTypeArguments()[0];
        return Class.forName(actualTypeArgument.getTypeName());
    }

    /**
     * Retrieve generic type using field object
     * @param field object that will be used to get generic of this field
     * @return generic type
     */
    @SneakyThrows
    public static Class<?> retrieveGeneric(Field field) {
        ParameterizedType genericReturnType = (ParameterizedType) field.getGenericType();
        Type actualTypeArgument = genericReturnType.getActualTypeArguments()[0];
        return Class.forName(actualTypeArgument.getTypeName());
    }
}
