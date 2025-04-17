package nova.backend.domain.user.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nova.backend.domain.user.dto.response.UserTokenResponseDTO;
import nova.backend.global.common.SuccessResponse;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "엑세스/리프레시 토큰 발급 성공 응답")
public class UserTokenSuccessResponse extends SuccessResponse<UserTokenResponseDTO> {
}
