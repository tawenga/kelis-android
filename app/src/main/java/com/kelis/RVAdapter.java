package com.kelis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.github.abdularis.civ.AvatarImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.kelis.App.mDatabaseRef;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.UserProfileViewHolder> {

    public static class UserProfileViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        AvatarImageView mProfilePhoto;
        TextView mUserNameTextView;
        TextView mCourseYearTextView;
        ImageView mThumbsUpImageView;
        ImageView mThumbsDownImageView;
        TextView mNumThumbsUpTextView;
        TextView mNumThumbsDownTextView;
        LinearLayout mThumbsUpLayout;
        LinearLayout mThumbsDownLayout;

        UserProfileViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            mProfilePhoto = (AvatarImageView) itemView.findViewById(R.id.profile_photo_image_view);
            mUserNameTextView = (TextView)itemView.findViewById(R.id.user_name_text_view);
            mCourseYearTextView = (TextView)itemView.findViewById(R.id.course_name_year_text_view);
            mThumbsUpImageView = (ImageView) itemView.findViewById(R.id.thumbs_up_image_view);
            mThumbsDownImageView = (ImageView) itemView.findViewById(R.id.thumbs_down_image_view);
            mNumThumbsUpTextView = (TextView) itemView.findViewById(R.id.num_thumbs_up_text_view);
            mNumThumbsDownTextView =(TextView) itemView.findViewById(R.id.num_thumbs_down_text_view);
            mThumbsUpLayout = (LinearLayout) itemView.findViewById(R.id.thumbs_up_layout);
            mThumbsDownLayout = (LinearLayout) itemView.findViewById(R.id.thumbs_down_layout);

        }
    }

    List<UserProfile> userProfiles;
    Context context;

    RVAdapter(List<UserProfile> userProfiles, Context context){
        this.userProfiles = userProfiles;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public UserProfileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_item, viewGroup, false);
        UserProfileViewHolder pvh = new UserProfileViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final UserProfileViewHolder userProfileViewHolder, final int i) {
        final int userProfileId = userProfiles.get(i).getUserId();
        userProfileViewHolder.mUserNameTextView.setText(userProfiles.get(i).getUsername());
        userProfileViewHolder.mCourseYearTextView.setText(userProfiles.get(i).getCourseNameAndYear());

        if(!userProfiles.get(i).getPhoto().isEmpty()) {
            Picasso.get().load(userProfiles.get(i).getPhoto())
                    .placeholder(R.drawable.user_100)
                    .into(userProfileViewHolder.mProfilePhoto);
        }

        final ImagePopup imagePopup = new ImagePopup(context);
        imagePopup.setBackgroundColor(Color.TRANSPARENT);  // Optional
        imagePopup.setFullScreen(true); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

        if(!userProfiles.get(i).getPhoto().isEmpty()) {
            imagePopup.initiatePopupWithGlide(userProfiles.get(i).getPhoto());
        }

        userProfileViewHolder.mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                imagePopup.viewPopup();

            }
        });

        userProfileViewHolder.mThumbsUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbsUpLogic(userProfiles.get(i));
            }
        });

        userProfileViewHolder.mThumbsDownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbsDownLogic(userProfiles.get(i));
            }
        });

    }

    public void thumbsUpLogic(final UserProfile userProfile){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liker_id", Integer.parseInt(myUserId()));
            jsonObject.put("liked_id", userProfile.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(App.APP_DOMAIN + "like")
                .addJSONObjectBody(jsonObject)
                .setTag("like")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addThumbsUp(userProfile);
                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                    }
                });
    }

    public void thumbsDownLogic(final UserProfile userProfile){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("unliker_id", Integer.parseInt(myUserId()));
            jsonObject.put("unliked_id", userProfile.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(App.APP_DOMAIN + "unlike")
                .addJSONObjectBody(jsonObject)
                .setTag("unlike")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addThumbsDown(userProfile);
                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                    }
                });
    }

    public void addThumbsUp(UserProfile userProfile){
        //see if you can do this in background
        JSONObject jsonObject = new JSONObject();
        try {
            int numThumbsUp = userProfile.getThumbsUp() + 1;
            jsonObject.put("user_id", userProfile.getUserId());
            jsonObject.put("username", userProfile.getUsername());
            jsonObject.put("course_name_and_year", userProfile.getCourseNameAndYear());
            jsonObject.put("photo", userProfile.getPhoto());
            jsonObject.put("thumbs_up", numThumbsUp);
            jsonObject.put("thumbs_down", userProfile.getThumbsDown());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.put(App.APP_DOMAIN + "profiles/" + userProfile.getUserId())
                .addJSONObjectBody(jsonObject)
                .setTag("edit_profile_add_thumbs_up")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("successaddthumbsup", response.toString());
                        //save to local
                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                    }
                });
    }

    public void addThumbsDown(UserProfile userProfile){
        JSONObject jsonObject = new JSONObject();
        try {
            int numThumbsDown = userProfile.getThumbsDown() + 1;
            jsonObject.put("user_id", userProfile.getUserId());
            jsonObject.put("username", userProfile.getUsername());
            jsonObject.put("course_name_and_year", userProfile.getCourseNameAndYear());
            jsonObject.put("photo", userProfile.getPhoto());
            jsonObject.put("thumbs_up", userProfile.getThumbsUp());
            jsonObject.put("thumbs_down", numThumbsDown);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.put(App.APP_DOMAIN + "profiles/" + userProfile.getUserId())
                .addJSONObjectBody(jsonObject)
                .setTag("edit_profile_add_thumbs_down")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("successaddthumbsdown", response.toString());
                        //save to local
                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    public String myUserId(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //return preferences.getString("user_id", "");
        return "1";
    }
}
