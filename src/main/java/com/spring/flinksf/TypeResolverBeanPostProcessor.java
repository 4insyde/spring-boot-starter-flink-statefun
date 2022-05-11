package com.spring.flinksf;

import com.spring.flinksf.api.EnableMessageTypeScan;
import com.spring.flinksf.api.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.types.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.spring.flinksf.ReflectionUtil.retrieveGeneric;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class TypeResolverBeanPostProcessor implements BeanPostProcessor {

    private final TypeResolver typeResolver;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(EnableMessageTypeScan.class)) {
            EnableMessageTypeScan scan = bean.getClass().getDeclaredAnnotation(EnableMessageTypeScan.class);
            String[] basePackageScan = scan.basePackageScan();
            if (basePackageScan.length == 0) {
                throw new IllegalArgumentException("Assign at least one base package path into EnableMessageTypeScan");
            }
            Stream.of(basePackageScan)
                    .map(this::findTypes)
                    .flatMap(Collection::stream)
                    .map(this::getTypes)
                    .flatMap(Collection::stream)
                    .forEach(kv -> typeResolver.put(kv.key, kv.value));
        }
        return bean;
    }

    @SneakyThrows
    private List<Class<?>> findTypes(String basePackage) {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<?>> candidates = new ArrayList<>();
        String packageSearchPath = resolveClasspath(basePackage);
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        Stream.of(resources)
                .parallel()
                .filter(Resource::isReadable)
                .map(r -> metadataReader(r, metadataReaderFactory))
                .filter(this::isCandidate)
                .map(this::loadClass)
                .forEach(c -> candidates.add(c.orElse(null)));
        return candidates;
    }

    private List<KeyValue<Class<?>, Type<?>>> getTypes(Class<?> classCandidate) {
        return Stream.of(classCandidate.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(MessageType.class))
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.getType().isAssignableFrom(Type.class))
                .map(this::extractType)
                .collect(toList());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private KeyValue<Class<?>, Type<?>> extractType(Field field) {
        field.setAccessible(true);
        Type<?> type = (Type<?>) field.get(null);
        Class<?> genericType = retrieveGeneric(field);
        return new KeyValue<>(genericType, type);
    }

    private String resolveClasspath(String basePackage) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/**/*.class";
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    private MetadataReader metadataReader(Resource resource, MetadataReaderFactory metadataReaderFactory) {
        try {
            return metadataReaderFactory.getMetadataReader(resource);
        } catch (IOException e) {
            return null;
        }
    }

    private boolean isCandidate(MetadataReader metadataReader) {
        Optional<Class<?>> aClass = loadClass(metadataReader);
        if (aClass.isEmpty()) {
            return false;
        }
        return Stream.of(aClass.get().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(MessageType.class))
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .anyMatch(f -> f.getType().isAssignableFrom(Type.class));
    }

    private Optional<Class<?>> loadClass(MetadataReader metadataReader) {
        try {
            return Optional.of(Class.forName(metadataReader.getClassMetadata().getClassName()));
        } catch (Throwable ignore) {
        }
        return Optional.empty();
    }

    @RequiredArgsConstructor
    private static class KeyValue<K, V> {
        private final K key;
        private final V value;
    }
}
