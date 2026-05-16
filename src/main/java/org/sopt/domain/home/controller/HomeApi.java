package org.sopt.domain.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.domain.home.dto.response.HomeResponse;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.global.response.CommonApiResponse;
import org.sopt.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Home", description = "홈 화면 API")
public interface HomeApi {

    @Operation(summary = "홈 화면 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiExceptions({UserNotFoundException.class})
    ResponseEntity<CommonApiResponse<HomeResponse>> getHome(
            @RequestHeader("User-Id") Long userId
    );
}
