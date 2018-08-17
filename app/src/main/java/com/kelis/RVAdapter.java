package com.kelis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.github.abdularis.civ.AvatarImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

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
    public void onBindViewHolder(UserProfileViewHolder userProfileViewHolder, int i) {
        userProfileViewHolder.mUserNameTextView.setText(userProfiles.get(i).getUsername());
        userProfileViewHolder.mCourseYearTextView.setText(userProfiles.get(i).getCourseNameAndYear());
        userProfileViewHolder.mNumThumbsUpTextView.setText(String.valueOf(userProfiles.get(i).getThumbsUp()));
        userProfileViewHolder.mNumThumbsDownTextView.setText(String.valueOf(userProfiles.get(i).getThumbsDown()));

        if(!userProfiles.get(i).getPhoto().isEmpty()) {
            Picasso.get().load(userProfiles.get(i).getPhoto())
                    .placeholder(R.drawable.user_100)
                    .into(userProfileViewHolder.mProfilePhoto);
        }

        final ImagePopup imagePopup = new ImagePopup(context);
        imagePopup.setWindowHeight(200); // Optional
        imagePopup.setWindowWidth(200); // Optional
        imagePopup.setBackgroundColor(Color.TRANSPARENT);  // Optional
        imagePopup.setFullScreen(true); // Optional
        //imagePopup.setHideCloseIcon(true);  // Optional
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
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }
}
