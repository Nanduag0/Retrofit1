package com.example.retrofit1;

public class Post
{
    private  int userId;
    private String title;
    private Integer id;
    private String text;

    public Post(int userId, String title, Integer id, String text) {
        this.userId = userId;
        this.title = title;
        this.id = id;
        this.text = text;
    }

    public int getUserId()
    {

        return userId;
    }
    public Integer getId()
    {
        return id;

    }
    public String getTitle()
    {
        return title;
    }

    public String getText() {
        return text;
    }
}
