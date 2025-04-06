package chuchuchi.chuchuchi.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {


    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseEx(BaseException exception) {
        log.error("BaseException errorMessage(): {}",exception.getBaseExceptionType().getErrorMessage());
        log.error("BaseException errorCode(): {}", exception.getBaseExceptionType().getErrorCode());

        return new ResponseEntity(new ExceptionDto(exception.getBaseExceptionType().getErrorCode()),
                                                    exception.getBaseExceptionType().getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity handleMemberEx(Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity(HttpStatus.OK);
    }


    @Data
    @AllArgsConstructor
    static class ExceptionDto {
        private Integer errorCode;
    }
}
