package chuchuchi.chuchuchi.domain.comment.exception;

import chuchuchi.chuchuchi.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum CommentExceptionType implements BaseExceptionType {

    NOT_FOUND_COMMENT(800, HttpStatus.NOT_FOUND, "찾으시는 댓글이 없습니다."),
    NOT_AUTHORITY_UPDATE_COMMENT(801, HttpStatus.FORBIDDEN, "댓글을 업데이트할 권한이 없습니다."),
    NOT_AUTHORITY_DELETE_COMMENT(802, HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다.");


    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    CommentExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
