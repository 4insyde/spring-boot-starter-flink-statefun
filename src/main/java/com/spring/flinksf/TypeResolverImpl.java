package com.spring.flinksf;

import com.spring.flinksf.api.SerdeType;
import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.types.Type;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Stream;

public class TypeResolverImpl implements TypeResolver {
    private final Map<Class<?>, Type<?>> types = new HashMap<>();

    @Override
    public void put(Class<?> clazz, Type<?> type) {
        types.put(clazz, type);
    }

    @Override
    public <T> Type<T> findByClass(Class<T> clazz) {
        return (Type<T>) types.get(clazz);
    }

//    @PostConstruct
    public void init() {
        List<Class<? extends SerdeType<?>>> types = findTypes();
        types.stream()
                .filter(c -> !c.isInterface())
                .map(this::loadObjectByCLass)
                .forEach(t -> put(t.getTypeClass(), t.getType()));
    }

    @SneakyThrows
    private SerdeType<?> loadObjectByCLass(Class<? extends SerdeType<?>> type) {
        Constructor<?> constructor = type.getConstructor();
        return (SerdeType<?>) constructor.newInstance();
    }

    @SneakyThrows
    private List<Class<? extends SerdeType<?>>> findTypes() {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<? extends SerdeType<?>>> candidates = new ArrayList<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "**/*.class";
        Stream.of(resourcePatternResolver.getResources(packageSearchPath))
                .parallel()
                .filter(Resource::isReadable)
                .map(r -> metadataReader(r, metadataReaderFactory))
                .filter(this::isCandidate)
                .map(this::loadClass)
                .forEach(c -> candidates.add((Class<? extends SerdeType<?>>) c.get()));
        return candidates;
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
                .filter(cls -> cls.isAssignableFrom(SerdeType.class))
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
