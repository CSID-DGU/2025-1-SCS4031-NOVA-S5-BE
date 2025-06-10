package nova.backend.domain.stamp.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.stamp.dto.request.StampAccumulateRequestDTO;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "2. 스탬프(STAFF) ", description = "스탬프 적립 및 조회 API (카페 선택 이후 사용 가능)")
public interface StaffStampApi {

    @Operation(
            summary = "스탬프 적립",
            description = """
                    카페 사장/직원이 Profile QR 코드(UUID)를 사용하여 해당 유저에게 스탬프를 적립합니다.
                    ✅ cafeId는 SecurityContextHolder에 저장된 selectedCafeId로 처리됩니다.
                    ✅ staff/owner 권한이 없거나 카페 선택 안했으면 403 또는 404 예외 발생
                    """,
            security = @SecurityRequirement(name = "token")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "적립 대상 회원의 QR 코드(UUID)와 적립 개수를 포함한 요청 DTO",
            required = true,
            content = @Content(schema = @Schema(implementation = StampAccumulateRequestDTO.class))
    )
    @ApiResponse(
            responseCode = "200",
            description = "스탬프 적립 성공",
            content = @Content(schema = @Schema(implementation = SuccessResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "유효하지 않은 요청 (예: 카페 미선택)",
            content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (해당 카페 staff/owner 아님)",
            content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "대상 유저 또는 카페를 찾을 수 없음 (ENTITY_NOT_FOUND)",
            content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class))
    )
    @PostMapping
    ResponseEntity<SuccessResponse<?>> accumulateStamp(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @org.springframework.web.bind.annotation.RequestBody StampAccumulateRequestDTO request
    );


    @Operation(
            summary = "적립 직후 유저 스탬프 현황 조회",
            description = """
                    카페 사장/직원이 유저의 QR 코드(UUID)를 사용하여 해당 유저의 스탬프 적립 현황을 조회합니다.
                    
                    ✅ 카페 staff/owner 권한이 필요합니다.
                    ✅ cafeId는 SecurityContextHolder에 저장된 selectedCafeId로 처리됩니다.
                    """,
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponse(
            responseCode = "200",
            description = "스탬프 현황 조회 성공",
            content = @Content(schema = @Schema(implementation = nova.backend.domain.stamp.schema.StampHistoryListSuccessResponse.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (해당 카페 staff/owner 아님)",
            content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "대상 유저를 찾을 수 없음 (ENTITY_NOT_FOUND)",
            content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class))
    )
    @GetMapping
    ResponseEntity<SuccessResponse<?>> getStampHistoryForStaffView(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "조회할 유저의 QR 코드(UUID)", required = true)
            @RequestParam String qrCodeValue
    );
}
