package ddwu.mobile.finalproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class GooglePlaceResult {
    @SerializedName("formatted_address")
    public String formattedAddress;

    @SerializedName("formatted_phone_number")
    public String formattedPhoneNumber;

    public String name;

    @SerializedName("opening_hours")
    public GoogleOpeningHours openingHours;

    @SerializedName("place_id")
    public String placeId;

    public List<GooglePlaceReview> reviews = Collections.emptyList();
}
