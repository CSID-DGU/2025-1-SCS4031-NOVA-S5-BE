package nova.backend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "1. 유저(USER)", description = "일반 회원 관련 API")
public interface UserApi {

    @Operation(
            summary = "[메인페이지 전용] QR 코드 조회",
            description = "현재 로그인된 사용자의 프로필 QR 코드를 조회합니다. UUID로 되어있으며, 프론트에서 이를 받아서 QR 코드로 보여줘야 합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponse(
            responseCode = "200",
            description = "QR 코드 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = nova.backend.domain.user.schema.QrCodeSuccessResponse.class)
            )
    )
    @GetMapping("/my-qr")
    ResponseEntity<SuccessResponse<?>> getMyQrCode(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "[메인페이지 전용] 사용자 상태 정보 조회",
            description = "현재 로그인된 사용자의 이름, 오늘 적립한 스탬프 수, 아직 사용하지 않은 리워드 수를 조회합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponse(
            responseCode = "200",
            description = "사용자 상태 정보 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = nova.backend.domain.user.schema.UserStatusSuccessResponse.class)
            )
    )
    @GetMapping("/status")
    ResponseEntity<SuccessResponse<?>> getUserStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
