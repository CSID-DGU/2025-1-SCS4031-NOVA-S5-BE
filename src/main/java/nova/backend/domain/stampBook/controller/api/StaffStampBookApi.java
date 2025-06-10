package nova.backend.domain.stampBook.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.stampBook.dto.request.UseRewardsRequestDTO;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import nova.backend.global.error.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;

@Tag(name = "2. 스탬프북(STAFF)", description = "사장/직원용 리워드 사용 API (카페 선택 후 사용 가능)")
public interface StaffStampBookApi {

    @Operation(
            summary = "QR 코드로 리워드 사용 처리",
            description = """
                    카페 사장/직원이 대상 유저의 QR 코드(UUID)를 입력받아 리워드 스탬프 사용 처리를 합니다.
                    
                    ✅ 요청 수량보다 사용 가능한 리워드 수량이 부족하면 `NOT_ENOUGH_REWARDS` 예외 (400) 발생
                    ✅ cafeId는 SecurityContextHolder 내 userDetails.selectedCafeId로 처리되며, cafeSelection을 선행해야 합니다.
                    ✅ user가 해당 카페의 staff/owner가 아닐 경우 403 예외 발생
                    """,
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리워드 사용 처리 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용 가능한 리워드 부족 (NOT_ENOUGH_REWARDS)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료 또는 미인증)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 부족 (해당 카페 staff/owner 아님)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "대상 사용자 또는 카페를 찾을 수 없음 (ENTITY_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/rewards")
    ResponseEntity<SuccessResponse<?>> useRewardsByQrCodeForCafe(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리워드 사용 요청 DTO (qrCodeValue, count 포함)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UseRewardsRequestDTO.class))
            )
            @org.springframework.web.bind.annotation.RequestBody UseRewardsRequestDTO request
    );
}
