package com.spring.flink.statefun.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("flink-statefun.scan.types")
public class ConfigProperties {
    private boolean validationEnabled = true;
}
