package ddwu.mobile.finalproject.model;

import com.google.gson.annotations.SerializedName;

public class GooglePlaceReview {
    @SerializedName("author_name")
    public String authorName;

    @SerializedName("profile_photo_url")
    public String profilePhotoUrl;

    public double rating;

    @SerializedName("relative_time_description")
    public String relativeTimeDescription;

    public String text;

    public long time;
}
