package chuchuchi.chuchuchi.global.exception;

public abstract class BaseException extends RuntimeException {
    public abstract BaseExceptionType getBaseExceptionType();
}
