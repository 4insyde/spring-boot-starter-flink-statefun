package com.spring.flinksf;

import com.spring.flinksf.api.EnableMessageTypeScan;
import com.spring.flinksf.api.SerDeType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
                    .filter(c -> !c.isInterface())
                    .map(this::loadObjectByCLass)
                    .forEach(typeResolver::put);
        }
        return bean;
    }

    @SneakyThrows
    private SerDeType<?> loadObjectByCLass(Class<? extends SerDeType<?>> type) {
        Constructor<?> constructor = type.getConstructor();
        return (SerDeType<?>) constructor.newInstance();
    }

    @SneakyThrows
    private List<Class<? extends SerDeType<?>>> findTypes(String basePackage) {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<? extends SerDeType<?>>> candidates = new ArrayList<>();
        String packageSearchPath = resolveClasspath(basePackage);
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        Stream.of(resources)
                .parallel()
                .filter(Resource::isReadable)
                .map(r -> metadataReader(r, metadataReaderFactory))
                .filter(this::isCandidate)
                .map(this::loadClass)
                .forEach(c -> candidates.add((Class<? extends SerDeType<?>>) c.get()));
        return candidates;
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
        return loadClass(metadataReader)
                .filter(SerDeType.class::isAssignableFrom)
                .isPresent();
    }

    private Optional<Class<?>> loadClass(MetadataReader metadataReader) {
        try {
            return Optional.of(Class.forName(metadataReader.getClassMetadata().getClassName()));
        } catch (Throwable ignore) {
        }
        return Optional.empty();
    }
}
