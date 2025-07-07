package io.polyaxis.api.utils.exceptions;

import java.io.Serial;

/// Application runtime exception.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3513491993982293262L;

    private static final String ERROR_MESSAGE_FORMAT = "code: %d, msg: %s";

    private final int errCode;

    public ApplicationException(int errCode) {
        super();
        this.errCode = errCode;
    }

    public ApplicationException(int errCode, String errMsg) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg));
        this.errCode = errCode;
    }

    public ApplicationException(int errCode, Throwable throwable) {
        super(throwable);
        this.errCode = errCode;
    }

    public ApplicationException(int errCode, String errMsg, Throwable throwable) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg), throwable);
        this.errCode = errCode;
    }
    
    public int getErrCode() {
        return errCode;
    }
}
