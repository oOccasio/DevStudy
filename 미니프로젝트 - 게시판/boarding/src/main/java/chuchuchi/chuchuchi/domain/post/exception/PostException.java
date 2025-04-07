package chuchuchi.chuchuchi.domain.post.exception;

import chuchuchi.chuchuchi.global.exception.BaseException;
import chuchuchi.chuchuchi.global.exception.BaseExceptionType;

public class PostException extends BaseException {

    private final BaseExceptionType baseExceptionType;

    public PostException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getBaseExceptionType() {
        return baseExceptionType;
    }
}
