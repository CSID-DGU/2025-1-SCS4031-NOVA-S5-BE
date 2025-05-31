package nova.backend.domain.challenge.dto.request;

import nova.backend.global.error.ErrorCode;
import nova.backend.global.error.exception.BusinessException;

public record ChallengeCancelRequestDTO(String qrCodeValue, int count) {
    public void validate() {
        if (count <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}

