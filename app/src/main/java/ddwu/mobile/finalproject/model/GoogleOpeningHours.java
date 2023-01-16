package ddwu.mobile.finalproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleOpeningHours {
    @SerializedName("open_now")
    public boolean openNow;

    @SerializedName("weekday_text")
    public List<String> weekdayText;
}
