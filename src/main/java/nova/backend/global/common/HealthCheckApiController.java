package nova.backend.global.common;

import lombok.RequiredArgsConstructor;
import nova.backend.global.auth.jwt.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class HealthCheckApiController {
    private final JwtProvider jwtProvider;

    @RequestMapping("/")
    public String NovaServer() {
        return "Hello! Nova Server!";
    }

    // 임시 토큰 발급 API. 추후 로그인 기능이 완성되면 삭제할 예정
    @PostMapping("/token/{userId}")
    public ResponseEntity<SuccessResponse<?>> getToken(@PathVariable(name = "userId") Long userId) {
        String accessToken = jwtProvider.getIssueToken(userId, true);
        String refreshToken = jwtProvider.getIssueToken(userId, false);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return SuccessResponse.created(tokens);
    }
}
