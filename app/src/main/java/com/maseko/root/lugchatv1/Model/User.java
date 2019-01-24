package com.maseko.root.lugchatv1.Model;
public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;
    private String status_msg;
    private String lastSee;
    private String location;

    public User(String id, String username, String imageURL, String status, String search, String status_msg, String lastSee, String location) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.status_msg = status_msg;
        this.lastSee = lastSee;
        this.location = location;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }

    public String getLastSee() {
        return lastSee;
    }

    public void setLastSee(String lastSee) {
        this.lastSee = lastSee;
    }

    public String getLocation() {
        return status_msg;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}