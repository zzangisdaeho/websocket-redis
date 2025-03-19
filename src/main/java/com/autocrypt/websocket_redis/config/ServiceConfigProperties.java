package com.autocrypt.websocket_redis.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Getter
@ConfigurationProperties(prefix = "service")
@Validated
public class ServiceConfigProperties {

    @NotNull(message = "service.prefix는 필수값 입니다")
    @NotEmpty(message = "service.prefix는 비어있어선 안됩니다")
    private final String prefix;

    public ServiceConfigProperties(String prefix) {
        this.prefix = prefix;
    }

    @PostConstruct
    public void validate() {
        log.info("✅ SERVICE_PREFIX is properly set: {}", prefix);
    }
}