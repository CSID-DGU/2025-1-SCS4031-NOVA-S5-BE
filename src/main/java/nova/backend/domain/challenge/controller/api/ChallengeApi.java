package nova.backend.domain.challenge.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.challenge.entity.status.ChallengeStatus;
import nova.backend.domain.challenge.schema.ChallengeDetailSuccessResponse;
import nova.backend.domain.challenge.schema.ChallengeListSuccessResponse;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "1. 챌린지(USER)", description = "유저 챌린지 관련 API")
public interface ChallengeApi {

    @PatchMapping("/{challengeId}/withdraw")
    @Operation(summary = "챌린지 참여 중단", description = "유저가 진행 중인 챌린지 참여를 중단합니다.")
    ResponseEntity<SuccessResponse<?>> withdrawChallenge(
            @Parameter(description = "중단할 챌린지 ID", example = "1")
            @PathVariable Long challengeId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );


    @Operation(
            summary = "진행 가능한 챌린지 목록 조회",
            description = "현재 참여 가능한 챌린지를 모두 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ChallengeListSuccessResponse.class)))
            }
    )
    @GetMapping("/browse")
    ResponseEntity<SuccessResponse<?>> getAvailableChallenges();

    @Operation(
            summary = "내 챌린지 목록 조회",
            description = "사용자가 참여 중인 챌린지를 상태(IN_PROGRESS, COMPLETED, REWARDED)로 필터링하여 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ChallengeListSuccessResponse.class)))
            }
    )
    @GetMapping("/my")
    ResponseEntity<SuccessResponse<?>> getMyChallenges(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "필터링할 챌린지 상태" +
                    "파라미터를 꼭! 넣어야 합니다. `my?status=IN_PROGRESS`", example = "IN_PROGRESS")
            @RequestParam ChallengeStatus status
    );

    @Operation(
            summary = "챌린지 상세 조회",
            description = "특정 챌린지의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ChallengeDetailSuccessResponse.class)))
            }
    )
    @GetMapping("/{challengeId}")
    ResponseEntity<SuccessResponse<?>> getChallengeDetail(
            @Parameter(description = "조회할 챌린지 ID", example = "1")
            @PathVariable Long challengeId
    );
}
