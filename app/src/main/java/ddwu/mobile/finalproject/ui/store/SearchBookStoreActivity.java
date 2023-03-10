package ddwu.mobile.finalproject.ui.store;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.material.appbar.MaterialToolbar;

import ddwu.mobile.finalproject.R;
import ddwu.mobile.finalproject.source.GooglePlacesService;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchBookStoreActivity extends AppCompatActivity implements OnMapReadyCallback {
    final static String TAG = "SearchBookStoreActivity";
    final static int PERMISSION_REQ_CODE = 100;

    // Map & Place
    private GoogleMap googleMap;
    private PlaceBasicManager placeBasicManager;

    private FusedLocationProviderClient flpClient;
    private Double latitude = null;
    private Double longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book_store);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        placeBasicManager = new PlaceBasicManager(getString(R.string.api_key));
        placeBasicManager.setOnPlaceBasicResult(list -> {
            Log.d(TAG, "Result size: " + list.size());

            MarkerOptions options = new MarkerOptions();
            for (PlaceBasic place : list) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                options.title(place.getName())
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                Marker marker = googleMap.addMarker(options);
                marker.setTag(place);//??? ????????? ?????? placeId??? ?????? ?????? ???????????? ??????
            }

            if (list.isEmpty()) {
                Toast.makeText(getApplicationContext(), "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
            }

            findViewById(R.id.progress_view).setVisibility(View.GONE);
        });

        ((MaterialToolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> onBackPressed());

        mapLoad();

        if (checkPermission()) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        flpClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                new CancellationTokenSource().getToken()).addOnCompleteListener(task -> {
            Location location = task.getResult();
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();

            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

                searchStart(latitude, longitude, 1000, PlaceTypes.BOOK_STORE);
            }
        });
    }

    /*????????? ????????? ?????? ????????? ??????
     * PlaceBasicManager ??? ????????? type ??? ????????? PlaceBasic ??? ???????????? ???????????? ????????? ???????????? ?????? */
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic(lat, lng, radius, type);
    }

    //?????? ?????? ???, ???????????? ?????????
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        /*????????? InfoWindow ?????? ??? marker??? Tag ??? ????????? placeID ???
         * Google PlacesAPI ??? ???????????? ????????? ????????????*/
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                if (!(marker.getTag() instanceof PlaceBasic)) {
                    return;
                }

                PlaceBasic place = (PlaceBasic) marker.getTag();

                Intent intent = new Intent(SearchBookStoreActivity.this, DetailBookStoreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("placeId", place.getPlaceId());
                intent.putExtra("placeName", place.getName());
                startActivity(intent);
            }
        });

        if (latitude != null && longitude != null) {
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            searchStart(latitude, longitude, 1000, PlaceTypes.BOOK_STORE);
        }
    }

    /*???????????? ??????????????? ??????*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // ???????????? this: MainActivity ??? OnMapReadyCallback ??? ???????????????
    }

    /* ?????? permission ?????? */
    private boolean checkPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (!checkPermission()) {
                // ????????? ????????? ??? ???????????? ??????
                Toast.makeText(this, "??? ????????? ?????? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
