package com.spring.flink.statefun.api;

import org.apache.flink.statefun.sdk.java.types.Type;

/**
 * Interface that may describe message type object.The implementation should be annotated with @MessageType to indicate
 * that this class is responsible for Serialization and Deserialization. Implementation could be considered as bean
 * therefore you can use all Spring component features to create your own SerDeType
 * @param <T>
 */
public interface SerDeType<T> {

    /**
     * Return value that describe Type for the object in generic
     * @return Type object for T class
     */
    Type<T> type();
}
