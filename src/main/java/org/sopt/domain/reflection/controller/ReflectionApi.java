package org.sopt.domain.reflection.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.domain.mistake.exception.MistakeAccessDeniedException;
import org.sopt.domain.mistake.exception.MistakeNotFoundException;
import org.sopt.domain.reflection.dto.request.ReflectionCreateRequest;
import org.sopt.domain.reflection.dto.response.ReflectionCreateResponse;
import org.sopt.domain.reflection.exception.ReflectionDuplicateException;
import org.sopt.domain.reflection.exception.ReflectionInvalidEmojiIndexException;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.global.response.CommonApiResponse;
import org.sopt.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Reflection", description = "회고 API")
public interface ReflectionApi {

    @Operation(summary = "회고 작성")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @ApiExceptions({
            UserNotFoundException.class,
            MistakeNotFoundException.class,
            MistakeAccessDeniedException.class,
            ReflectionInvalidEmojiIndexException.class,
            ReflectionDuplicateException.class
    })
    ResponseEntity<CommonApiResponse<ReflectionCreateResponse>> create(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long mistakeId,
            @RequestBody ReflectionCreateRequest request
    );
}
