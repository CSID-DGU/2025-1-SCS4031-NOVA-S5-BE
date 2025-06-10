package nova.backend.domain.cafe.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.cafe.dto.request.CafeRegistrationRequestDTO;
import nova.backend.domain.cafe.dto.request.StampBookDesignCreateRequestDTO;
import nova.backend.domain.cafe.dto.response.CafeDesignOverviewDTO;
import nova.backend.domain.cafe.dto.response.StampBookDesignDetailDTO;
import nova.backend.domain.cafe.schema.CafeRegistrationMultipartSchema;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import nova.backend.global.error.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "3. 카페(OWNER) API", description = "카페 등록/관리 API")
public interface OwnerCafeApi {

    @Operation(
            summary = "카페 등록",
            description = "사장이 자신의 카페를 등록합니다. 요청은 multipart/form-data 형식이며, PDF 파일을 포함합니다.",
            security = @SecurityRequirement(name = "token"),
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = CafeRegistrationMultipartSchema.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카페 등록 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 카페",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 에러",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    ResponseEntity<SuccessResponse<?>> registerCafe(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute CafeRegistrationRequestDTO request,
            @RequestPart MultipartFile businessRegistrationPdf
    );

    @Operation(
            summary = "카페 상세 정보 조회",
            description = "선택된 카페의 전체 정보를 JWT에서 추출된 selectedCafeId를 통해 조회합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CafeDesignOverviewDTO.class))),
            @ApiResponse(responseCode = "404", description = "카페를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/detail")
    ResponseEntity<SuccessResponse<?>> getCafeDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스탬프북 디자인 추가",
            description = "선택된 카페에 새로운 스탬프북 디자인을 추가합니다.",
            security = @SecurityRequirement(name = "token"),
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StampBookDesignCreateRequestDTO.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "디자인 추가 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "카페를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/stampbook-design")
    ResponseEntity<SuccessResponse<?>> addStampBookDesign(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @org.springframework.web.bind.annotation.RequestBody StampBookDesignCreateRequestDTO request
    );

    @Operation(
            summary = "스탬프북 디자인 노출 설정",
            description = "특정 스탬프북 디자인을 노출 상태로 변경합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "노출 설정 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "디자인 또는 카페를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/stampbook-design/{designId}/expose")
    ResponseEntity<SuccessResponse<?>> exposeStampBookDesign(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long designId
    );

    @Operation(
            summary = "카페의 모든 스탬프북 디자인 목록 조회",
            description = "선택된 카페에 등록된 모든 스탬프북 디자인 목록을 반환합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = StampBookDesignDetailDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/stampbook-designs")
    ResponseEntity<SuccessResponse<?>> getAllStampBookDesigns(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "노출된 스탬프북 디자인 조회",
            description = "선택된 카페에서 현재 노출된 하나의 스탬프북 디자인을 반환합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "노출된 디자인 조회 성공",
                    content = @Content(schema = @Schema(implementation = StampBookDesignDetailDTO.class))),
            @ApiResponse(responseCode = "404", description = "카페 또는 노출 디자인을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/stampbook-design")
    ResponseEntity<SuccessResponse<?>> getExposedStampBookDesign(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스탬프북 디자인 단건 조회",
            description = "디자인 ID를 통해 특정 스탬프북 디자인 정보를 조회합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "디자인 조회 성공",
                    content = @Content(schema = @Schema(implementation = StampBookDesignDetailDTO.class))),
            @ApiResponse(responseCode = "404", description = "디자인 또는 카페를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/stampbook-design/{designId}")
    ResponseEntity<SuccessResponse<?>> getStampBookDesignById(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long designId
    );
}
