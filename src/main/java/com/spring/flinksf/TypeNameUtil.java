package com.spring.flinksf;

import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.api.StatefulFunction;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

public class TypeNameUtil {

    public static TypeName typeName(Class<? extends DispatchableFunction> clazz){
        if (clazz.isAnnotationPresent(StatefulFunction.class)) {
            StatefulFunction annotation = clazz.getDeclaredAnnotation(StatefulFunction.class);
            String namespace = getNamespace(annotation, clazz.getPackage());
            String name = getName(annotation, clazz);
            return TypeName.typeNameFromString(String.join("/", namespace, name));
        }
        throw new IllegalArgumentException(clazz.getSimpleName() + " class is not stateful function");

    }

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
