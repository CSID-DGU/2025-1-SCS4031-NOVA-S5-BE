package nova.backend.domain.challenge.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.challenge.dto.request.ChallengeAccumulateRequestDTO;
import nova.backend.domain.challenge.schema.ChallengeAccumulateSuccessResponse;
import nova.backend.global.auth.CustomUserDetails;
import nova.backend.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "2. 챌린지(STAFF/OWNER) API", description = "직원/사장용 챌린지 적립 API")
@RequestMapping("/api/challenges/staff")
@SecurityRequirement(name = "token")
public interface StaffChallengeApi {

    @Operation(summary = "챌린지 적립", description = "직원 또는 사장이 유저의 QR코드로 챌린지를 적립합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "챌린지 적립 성공",
                    content = @Content(schema = @Schema(implementation = ChallengeAccumulateSuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "오늘 이미 적립했거나 챌린지 참여 상태가 올바르지 않음"),
            @ApiResponse(responseCode = "404", description = "유저 또는 챌린지를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/accumulate")
    ResponseEntity<SuccessResponse<?>> accumulateChallenge(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChallengeAccumulateRequestDTO request
    );
}
