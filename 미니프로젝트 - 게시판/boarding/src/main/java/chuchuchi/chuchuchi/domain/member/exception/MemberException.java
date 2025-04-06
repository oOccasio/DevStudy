package chuchuchi.chuchuchi.domain.member.exception;

import chuchuchi.chuchuchi.global.exception.BaseException;
import chuchuchi.chuchuchi.global.exception.BaseExceptionType;

public class MemberException extends BaseException {
    private BaseExceptionType exceptionType;

    public MemberException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getBaseExceptionType() {
        return exceptionType;
    }
}
