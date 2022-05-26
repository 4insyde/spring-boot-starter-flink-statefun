package com.spring.flinksf.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("flink-sf.scan.types")
public class ConfigProperties {
    private boolean validationEnabled = true;
}
