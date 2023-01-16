package ddwu.mobile.finalproject;

import android.app.Application;

import com.google.android.libraries.places.api.Places;

public class FinalProgressApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Places.initialize(this, getString(R.string.api_key));
    }
}
