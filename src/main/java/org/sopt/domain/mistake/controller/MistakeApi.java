package org.sopt.domain.mistake.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.domain.mistake.dto.request.MistakeCreateRequest;
import org.sopt.domain.mistake.dto.response.MistakeDetailResponse;
import org.sopt.domain.mistake.exception.MistakeAccessDeniedException;
import org.sopt.domain.mistake.exception.MistakeDuplicateException;
import org.sopt.domain.mistake.exception.MistakeNotFoundException;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.global.response.CommonApiResponse;
import org.sopt.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "실수 카드 상세 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiExceptions({UserNotFoundException.class, MistakeNotFoundException.class, MistakeAccessDeniedException.class})
    ResponseEntity<CommonApiResponse<MistakeDetailResponse>> getDetail(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long mistakeId
    );
}
