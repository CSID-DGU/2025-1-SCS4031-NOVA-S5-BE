package nova.backend.domain.cafe.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.cafe.schema.CafeSelectedSuccessResponse;
import nova.backend.domain.cafe.schema.MyCafeListSuccessResponse;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import nova.backend.global.error.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "2. 카페(STAFF) 카페 선택 API", description = "로그인 후 현재 선택할 카페를 설정하는 API")
public interface StaffCafeApi {

    @Operation(
            summary = "카페 선택 (스태프/사장)",
            description = "스태프 또는 사장이 자신이 소속된 카페 중 하나를 선택하여 '현재 선택된 카페'로 설정합니다.\n" +
                    "선택된 카페는 Redis에 저장되며, 추후 JWT 또는 인증객체에서 자동으로 사용됩니다.\n\n" +
                    "✅ 선택할 수 있는 카페는 자신이 staff 또는 owner로 소속된 카페만 가능합니다.\n" +
                    "✅ 잘못된 카페 ID 또는 권한이 없는 카페를 선택 시 ACCESS_DENIED 에러가 발생합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카페 선택 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (해당 카페에 staff/owner로 등록 안됨)",
                    content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "카페 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = nova.backend.global.error.dto.ErrorResponse.class)))
    })
    @PutMapping("/{cafeId}/selected")
    ResponseEntity<SuccessResponse<?>> selectCafe(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "선택할 카페 ID", example = "1") @PathVariable Long cafeId
    );

    @Operation(
            summary = "내가 소속된 카페 목록 조회",
            description = """
                현재 로그인한 사용자가 staff 또는 owner로 등록된 카페 목록을 조회합니다.

                ✅ 각 카페에는 선택 여부(`isSelected`)도 함께 포함되어 있으므로,
                이 정보를 활용하여 선택 API(`/selected`)를 호출할 수 있습니다.
                """,
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카페 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyCafeListSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/my")
    ResponseEntity<SuccessResponse<?>> getMyCafes(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "현재 선택된 카페 상세 정보 조회",
            description = """
                현재 JWT 또는 인증 객체에 설정된 `selectedCafeId`를 기반으로
                해당 카페의 상세 정보를 반환합니다.

                ✅ 등록 상태, 영업 시간, 특별 운영일 등을 함께 포함합니다.
                """,
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "선택된 카페 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = CafeSelectedSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "선택된 카페 ID에 해당하는 카페가 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/selected")
    ResponseEntity<SuccessResponse<?>> getSelectedCafe(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );



}



