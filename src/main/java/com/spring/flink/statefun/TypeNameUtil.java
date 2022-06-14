package com.spring.flink.statefun;

import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.api.StatefulFunction;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

public class TypeNameUtil {

    /**
     * Build TypeName using dispatchable function and {@link StatefulFunction} annotation parameters
     * @param clazz target class
     * @return constructed TypeName
     */
    public static TypeName typeName(Class<? extends DispatchableFunction> clazz){
        if (clazz.isAnnotationPresent(StatefulFunction.class)) {
            StatefulFunction annotation = clazz.getDeclaredAnnotation(StatefulFunction.class);
            String namespace = getNamespace(annotation, clazz.getPackage());
            String name = getName(annotation, clazz);
            return TypeName.typeNameFromString(String.join("/", namespace, name));
        }
        throw new IllegalArgumentException(clazz.getSimpleName() + " class is not stateful function");

    }

    /**
     * Get annotation namespace param or default
     * @param annotation object where namespace will be looking for
     * @param pckg object where default namespace will be extracted
     * @return namespace
     */
    private static String getNamespace(StatefulFunction annotation, Package pckg){
        return Optional.ofNullable(annotation.namespace())
                .filter(Strings::isNotBlank)
                .orElse(pckg.getName());
    }

    private static String getName(StatefulFunction annotation, Class<? extends DispatchableFunction> clazz){
        return Optional.ofNullable(annotation.name())
                .filter(Strings::isNotBlank)
                .orElse(clazz.getSimpleName());
    }
}
