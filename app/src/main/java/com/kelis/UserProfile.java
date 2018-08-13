package com.kelis;

public class UserProfile {

    public int user_id;
    public String username;
    public String course_name_and_year;
    public String photo;
    public int thumbs_up;
    public int thumbs_down;

    public UserProfile() {
    }

    public UserProfile(int user_id, String username, String course_name_and_year,
                       String photo, int thumbs_up, int thumbs_down) {
        this.user_id = user_id;
        this.username = username;
        this.course_name_and_year = course_name_and_year;
        this.photo = photo;
        this.thumbs_up =thumbs_up;
        this.thumbs_down = thumbs_down;
    }

    public String getUsername() {
        return username;
    }

    public String getCourseNameAndYear() {
        return course_name_and_year;
    }

    public String getPhoto() {
        return photo;
    }

    public int getThumbsUp(){
        return thumbs_up;
    }

    public int getThumbsDown(){
        return  thumbs_down;
    }
}