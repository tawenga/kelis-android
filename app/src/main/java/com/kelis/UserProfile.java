package com.kelis;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfile {

    public int id;
    public int user_id;
    public String username;
    public String course_name_and_year;
    public String photo;
    public int thumbs_up;
    public int thumbs_down;

    public UserProfile() {
    }

    public UserProfile(int id, int user_id, String username, String course_name_and_year,
                       String photo, int thumbs_up, int thumbs_down) {
        this.id = id;
        this.user_id = user_id;
        this.username = username;
        this.course_name_and_year = course_name_and_year;
        this.photo = photo;
        this.thumbs_up = thumbs_up;
        this.thumbs_down = thumbs_down;
    }

    public UserProfile(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.user_id = object.getInt("user_id");
            this.username = object.getString("username");
            this.course_name_and_year = object.getString("course_name_and_year");
            this.photo = object.getString("photo");
            this.thumbs_up = object.getInt("thumbs_up");
            this.thumbs_down = object.getInt("thumbs_down");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId(){
        return id;
    }

    public int getUserId(){
        return user_id;
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