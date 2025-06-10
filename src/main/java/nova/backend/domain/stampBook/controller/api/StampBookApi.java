package nova.backend.domain.stampBook.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.cafe.dto.request.StampBookCreateRequestDTO;
import nova.backend.domain.stampBook.schema.StampBookDetailSuccessResponse;
import nova.backend.domain.stampBook.schema.StampBookListSuccessResponse;
import nova.backend.domain.stampBook.schema.StampBookSuccessResponse;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import nova.backend.global.error.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. 스탬프북(USER)", description = "스탬프북 관련 API, 스탬프는 단일 스탬프지만 스탬프북은 한 장을 관리합니다.")
public interface StampBookApi {

    @Operation(
            summary = "나의 스탬프북 전체 조회",
            description = "현재 유저의 전체 스탬프북 목록을 조회합니다.\n* inHome 값이 true인 경우, 메인페이지에 노출 중인 항목입니다.\n* 완료 여부, 리워드 전환 여부에 관계없이 모두 포함됩니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스탬프북 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = StampBookListSuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/my")
    ResponseEntity<SuccessResponse<?>> getMyStampBooks(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스탬프북 생성 (다운로드)",
            description = "해당 카페에 대한 스탬프북을 생성합니다. 이미 미완료 상태의 스탬프북이 존재하면 409 Conflict 예외가 발생합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "스탬프북 생성 성공",
                    content = @Content(schema = @Schema(implementation = StampBookSuccessResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 미완료 스탬프북 존재",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<SuccessResponse<?>> createStampBook(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StampBookCreateRequestDTO request
    );

    @Operation(
            summary = "스탬프북 리워드 전환",
            description = "스탬프북을 리워드로 전환합니다. 전환 조건이 충족되지 않으면 예외 발생.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리워드 전환 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 미완료 스탬프북)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "스탬프북을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{stampBookId}/reward")
    ResponseEntity<SuccessResponse<?>> convertToReward(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long stampBookId
    );

    @Operation(
            summary = "스탬프북을 메인페이지에 추가",
            description = "선택한 스탬프북을 메인페이지에 표시되도록 추가합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메인페이지 추가 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "스탬프북을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{stampBookId}/home")
    ResponseEntity<SuccessResponse<?>> addStampBookToHome(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long stampBookId
    );

    @Operation(
            summary = "스탬프북을 메인페이지에서 제거",
            description = "선택한 스탬프북을 메인페이지에서 제거합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메인페이지 제거 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "스탬프북을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{stampBookId}/home")
    ResponseEntity<SuccessResponse<?>> removeStampBookFromHome(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long stampBookId
    );

    @Operation(
            summary = "메인페이지용 스탬프북 목록 조회 (inHome=true)",
            description = "메인페이지에 표시되는 스탬프북 목록만 조회합니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메인페이지 스탬프북 조회 성공",
                    content = @Content(schema = @Schema(implementation = StampBookListSuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/my/home")
    ResponseEntity<SuccessResponse<?>> getMyHomeStampBooks(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );
    @Operation(
            summary = "스탬프북 단일 상세 조회",
            description = "지정된 스탬프북 ID를 기반으로 상세 정보를 조회합니다.\n" +
                    "- 카페 기본 정보 및 노출 중인 디자인 정보 포함\n" +
                    "- 현재 스탬프 개수, 최대 개수, 남은 개수 포함\n" +
                    "- 자신의 스탬프북이 아닐 경우 403 오류 발생",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스탬프북 단일 조회 성공",
                    content = @Content(schema = @Schema(implementation = StampBookDetailSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "스탬프북 또는 디자인 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{stampBookId}")
    ResponseEntity<SuccessResponse<?>> getStampBookDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long stampBookId
    );
    @Operation(summary = "스탬프북 삭제", description = "내 스탬프북 중 하나를 삭제합니다.")
    @DeleteMapping("/{stampBookId}")
    ResponseEntity<SuccessResponse<?>> deleteStampBook(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                       @PathVariable Long stampBookId);


}