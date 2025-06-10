package nova.backend.domain.challenge.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.challenge.dto.request.ChallengeCreateRequestDTO;
import nova.backend.domain.challenge.schema.*;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "3. 챌린지(OWNER) API", description = "사장용 챌린지 생성 및 조회")
@RequestMapping("/api/owner/challenges")
@SecurityRequirement(name = "token")
public interface OwnerChallengeApi {

    @Operation(summary = "챌린지 생성", description = "사장이 새로운 챌린지를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "챌린지 생성 성공",
                    content = @Content(schema = @Schema(implementation = ChallengeCreateSuccessResponse.class))),
            @ApiResponse(responseCode = "403", description = "카페 소유주 아님"),
            @ApiResponse(responseCode = "404", description = "카페를 찾을 수 없음")
    })
    @PostMapping
    ResponseEntity<SuccessResponse<?>> createChallenge(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChallengeCreateRequestDTO request
    );

    @Operation(summary = "챌린지 상세 조회", description = "사장이 챌린지 단일 조회를 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChallengeDetailSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "챌린지를 찾을 수 없음")
    })
    @GetMapping("/{challengeId}")
    ResponseEntity<SuccessResponse<?>> getChallengeDetail(
            @PathVariable Long challengeId
    );

    @Operation(summary = "진행 예정 챌린지 조회", description = "사장이 진행 예정인 챌린지를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChallengeUpcomingListSuccessResponse.class)))
    })
    @GetMapping("/upcoming")
    ResponseEntity<SuccessResponse<?>> getUpcomingChallenges(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "진행 중 챌린지 조회", description = "사장이 진행 중인 챌린지를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChallengeOngoingListSuccessResponse.class)))
    })
    @GetMapping("/ongoing")
    ResponseEntity<SuccessResponse<?>> getOngoingChallenges(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "완료된 챌린지 조회", description = "사장이 완료된 챌린지를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ChallengeCompletedListSuccessResponse.class)))
    })
    @GetMapping("/completed")
    ResponseEntity<SuccessResponse<?>> getCompletedChallenges(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );
}

