package ddwu.mobile.finalproject.source;

import ddwu.mobile.finalproject.model.GooglePlaceResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesService {

    // Service Interface 메소드 생성
    @GET("/maps/api/place/details/json")
    Call<GooglePlaceResponse> getPlaceDetails(@Query("place_id") String placeId,
                                              @Query("key") String key,
                                              @Query("language") String language,
                                              @Query("fields") String fields
    );
}
