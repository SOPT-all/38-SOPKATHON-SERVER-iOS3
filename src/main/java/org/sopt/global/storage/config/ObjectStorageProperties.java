package org.sopt.global.storage.config;

import java.time.Duration;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "object-storage")
public record ObjectStorageProperties(
        @NotBlank String endpoint,
        @NotBlank String region,
        @NotBlank String bucket,
        @NotBlank String accessKey,
        @NotBlank String secretKey,
        Duration uploadUrlExpiration,
        Duration downloadUrlExpiration,
        @Positive long maxFileSize,
        @NotBlank String keyPrefix,
        @NotEmpty List<String> allowedContentTypes
) {
}
