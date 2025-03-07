package com.group.libraryapp.dto.book.request;

public class SaveBookRequest {
    private String name;

    public SaveBookRequest(){}

    public SaveBookRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
