package chuchuchi.chuchuchi.global.exception;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {

    int getErrorCode();

    HttpStatus getHttpStatus();

    String getErrorMessage();
}
