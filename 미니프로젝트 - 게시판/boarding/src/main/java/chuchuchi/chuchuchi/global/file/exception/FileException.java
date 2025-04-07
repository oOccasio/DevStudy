package chuchuchi.chuchuchi.global.file.exception;

import chuchuchi.chuchuchi.global.exception.BaseException;
import chuchuchi.chuchuchi.global.exception.BaseExceptionType;

public class FileException extends BaseException {
    private BaseExceptionType exceptionType;

    public FileException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getBaseExceptionType() {
        return exceptionType;
    }
}
