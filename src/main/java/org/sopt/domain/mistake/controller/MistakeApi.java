package org.sopt.domain.mistake.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.domain.mistake.dto.request.MistakeCreateRequest;
import org.sopt.domain.mistake.exception.MistakeDuplicateException;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.global.response.CommonApiResponse;
import org.sopt.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Mistake", description = "실수 카드 API")
public interface MistakeApi {

    @Operation(summary = "실수 카드 작성")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @ApiExceptions({UserNotFoundException.class, MistakeDuplicateException.class})
    ResponseEntity<CommonApiResponse<Void>> create(
            @RequestHeader("User-Id") Long userId,
            @RequestBody MistakeCreateRequest request
    );
}
