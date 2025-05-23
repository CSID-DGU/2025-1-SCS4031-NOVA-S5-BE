package nova.backend.domain.user.dto.response;

public record UserTokenResponseDTO(
        String accessToken,
        String refreshToken
) {
    public static UserTokenResponseDTO of(String accessToken, String refreshToken) {
        return new UserTokenResponseDTO(accessToken, refreshToken);
    }
}
