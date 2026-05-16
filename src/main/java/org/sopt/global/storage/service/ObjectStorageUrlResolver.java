package org.sopt.global.storage.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.sopt.global.storage.config.ObjectStorageProperties;
import org.sopt.global.storage.exception.InvalidObjectKeyException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectStorageUrlResolver {

    private final ObjectStorageProperties properties;

    public String generatePublicUrl(String objectKey) {
        validateObjectKey(objectKey);
        return "%s/%s/%s".formatted(
                properties.endpoint().replaceAll("/+$", ""),
                encodePathSegment(properties.bucket()),
                encodeObjectKey(objectKey)
        );
    }

    public void validateObjectKey(String objectKey) {
        String keyPrefix = properties.keyPrefix() + "/";
        if (objectKey == null || !objectKey.startsWith(keyPrefix) || objectKey.contains("..")) {
            throw new InvalidObjectKeyException();
        }
    }

    private String encodeObjectKey(String objectKey) {
        return objectKey.lines()
                .findFirst()
                .orElseThrow(InvalidObjectKeyException::new)
                .replace("\\", "/")
                .transform(key -> {
                    String[] segments = key.split("/");
                    StringBuilder encodedPath = new StringBuilder();
                    for (String segment : segments) {
                        if (!encodedPath.isEmpty()) {
                            encodedPath.append('/');
                        }
                        encodedPath.append(encodePathSegment(segment));
                    }
                    return encodedPath.toString();
                });
    }

    private String encodePathSegment(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
