package nova.backend.global.error.exception;

import nova.backend.global.error.ErrorCode;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}