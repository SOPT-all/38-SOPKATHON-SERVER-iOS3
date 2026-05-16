package org.sopt.domain.home.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.home.dto.response.HomeResponse;
import org.sopt.domain.home.service.HomeService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/home")
@RequiredArgsConstructor
public class HomeController implements HomeApi {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<CommonApiResponse<HomeResponse>> getHome(
            @RequestHeader("User-Id") Long userId
    ) {
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, homeService.getHome(userId));
    }
}
