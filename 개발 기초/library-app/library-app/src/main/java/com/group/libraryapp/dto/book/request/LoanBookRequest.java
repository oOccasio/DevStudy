package com.group.libraryapp.dto.book.request;

public class LoanBookRequest {

    private String userName;
    private String bookName;

    public LoanBookRequest(String userName, String bookName) {
        this.userName = userName;
        this.bookName = bookName;
    }

    public String getUserName() {
        return userName;
    }

    public String getBookName() {
        return bookName;
    }
}
