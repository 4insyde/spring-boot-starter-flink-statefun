package com.insyde.flink.statefun.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Declare configuration properties for base package scan validation mechanism
 */
@Getter
@Setter
@ConfigurationProperties("flink-statefun.scan.types")
public class ConfigProperties {
    private boolean validationEnabled = true;
}
