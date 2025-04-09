package chuchuchi.chuchuchi.domain.comment.exception;

import chuchuchi.chuchuchi.global.exception.BaseException;
import chuchuchi.chuchuchi.global.exception.BaseExceptionType;

public class CommentException extends BaseException {

    private final BaseExceptionType baseExceptionType;

    public CommentException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getBaseExceptionType() {
        return baseExceptionType;
    }
}
