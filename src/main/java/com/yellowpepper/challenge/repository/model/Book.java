package com.yellowpepper.challenge.repository.model;

import org.springframework.data.annotation.Id;

public class Book {

    @Id
    private Long id;

    private String title;

    private String author;

    public Book() {
    }

    public Book(String title, String author) {

        this.title = title;
        this.author = author;
    }

    public Book(Long id, String title, String author) {

        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Long getId() {

        return this.id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getTitle() {

        return this.title;
    }

    public String getAuthor() {

        return this.author;
    }
}