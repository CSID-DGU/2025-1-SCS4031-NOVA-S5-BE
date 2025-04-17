package nova.backend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nova.backend.domain.user.dto.request.UserLoginRequestDTO;
import nova.backend.domain.user.dto.request.UserTokenRequestDTO;
import nova.backend.domain.user.schema.UserTokenSuccessResponse;
import nova.backend.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "인증 API", description = "인증 관련 API, 프론트에서 테스트할 때 redirect uri를 바꿔야 하니 먼저 알려주세요.")
public interface AuthApi {

    @Operation(summary = "임시 토큰 발급",
            description = "사용자에게 임시 토큰을 발급하는 API입니다.(소셜 로그인 없이 테스트 토큰 발급용)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 토큰 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserTokenSuccessResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<SuccessResponse<?>> getTempToken(
            @Parameter(description = "토큰을 발급 받을 사용자의 ID") @PathVariable Long userId
    );

    @Operation(summary = "소셜 로그인(회원 가입 포함)",
            description = "소셜 로그인 정보를 받아 엑세스, 리프레시 토큰을 발급하는 API입니다.\n\n" +
                    "- 이미 다른 계정으로 가입된 경우에는 회원 가입이 불가능합니다. (1인 1계정)\n" +
                    "- code(인가코드)는 일회성으로, 한 번만 호출 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserTokenSuccessResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자 회원 인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 사용자"),
            @ApiResponse(responseCode = "500", description = "Oauth 토큰 발급 실패"),
            @ApiResponse(responseCode = "500", description = "Oauth 사용자 정보 조회 실패"),
    })
    ResponseEntity<SuccessResponse<?>> socialLogin(
            @RequestBody(description = "소셜 로그인 요청 DTO", required = true)
            UserLoginRequestDTO userLoginRequest
    );

    @Operation(summary = "로그아웃",
            description = "현재 사용자의 리프레시 토큰을 무효화하여 로그아웃하는 API입니다.",
            security = @SecurityRequirement(name = "token")
    )
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    ResponseEntity<SuccessResponse<?>> logout(
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "엑세스 토큰 재발급",
            description = "리프레시 토큰을 사용하여 엑세스 토큰을 재발급합니다.\n\n" +
                    "- 리프레시 토큰이 만료된 경우에는 다시 로그인해야 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserTokenSuccessResponse.class)
                    )),
            @ApiResponse(responseCode = "401",  description = "리프레시 토큰 만료"),
            @ApiResponse(responseCode = "401",  description = "유효하지 않은 리프레시 토큰"),
            @ApiResponse(responseCode = "401",  description = "저장된 리프레시 토큰 불일치")
    })
    ResponseEntity<SuccessResponse<?>> reissueToken(
            @RequestBody(description = "리프레시 토큰 요청 DTO", required = true)
            UserTokenRequestDTO uerTokenRequest
    );
}
