package ddwu.mobile.finalproject.ui.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.finalproject.R;
import ddwu.mobile.finalproject.databinding.ActivityDetailBookStoreBinding;
import ddwu.mobile.finalproject.databinding.ItemReviewBinding;
import ddwu.mobile.finalproject.model.GooglePlaceResponse;
import ddwu.mobile.finalproject.model.GooglePlaceResult;
import ddwu.mobile.finalproject.model.GooglePlaceReview;
import ddwu.mobile.finalproject.source.GooglePlacesService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailBookStoreActivity extends AppCompatActivity {
    private String placeId;
    private String placeName;

    private ActivityDetailBookStoreBinding binding;
    private GooglePlacesService googlePlacesService;

    private final List<String> fields = Arrays.asList("formatted_address", "name", "place_id",
            "formatted_phone_number", "reviews", "opening_hours");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            placeId = getIntent().getStringExtra("placeId");
            placeName = getIntent().getStringExtra("placeName");
        }

        if (savedInstanceState != null) {
            placeId = savedInstanceState.getString("placeId");
            placeName = savedInstanceState.getString("placeName");
        }

        if (placeId == null || placeName == null) {
            finish();
            return;
        }

        binding = ActivityDetailBookStoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.nameTextView.setText(placeName);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.google_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        googlePlacesService = retrofit.create(GooglePlacesService.class);

        googlePlacesService.getPlaceDetails(
                placeId,
                getString(R.string.api_key),
                "ko",
                String.join(",", fields)).enqueue(new Callback<GooglePlaceResponse>() {
            @Override
            public void onResponse(Call<GooglePlaceResponse> call, Response<GooglePlaceResponse> response) {
                GooglePlaceResult result = response.body().result;

                binding.addressTextView.setText(result.formattedAddress);
                binding.phoneNumberTextView.setText(result.formattedPhoneNumber);
                binding.openingHoursTextView.setText(String.join("\n", result.openingHours.weekdayText));

                binding.recyclerView.setAdapter(new ReviewAdapter(result.reviews));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(DetailBookStoreActivity.this, LinearLayoutManager.VERTICAL));

                binding.emptyView.setVisibility(result.reviews.isEmpty() ? View.VISIBLE : View.GONE);
                binding.progressView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GooglePlaceResponse> call, Throwable t) {
                t.printStackTrace();
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("placeId", placeId);
        outState.putString("placeName", placeName);
        super.onSaveInstanceState(outState);
    }


    private static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewItemViewHolder> {
        private final List<GooglePlaceReview> items;

        private ReviewAdapter(List<GooglePlaceReview> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ReviewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemReviewBinding binding = ItemReviewBinding.inflate(inflater, parent, false);
            return new ReviewItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewItemViewHolder holder, int position) {
            GooglePlaceReview item = items.get(position);
            ItemReviewBinding binding = holder.binding;

            Glide.with(binding.imageView)
                    .load(item.profilePhotoUrl)
                    .into(binding.imageView);

            binding.ratingBar.setRating((float) item.rating);
            binding.ratingTextView.setText(String.format(Locale.US, "%.1f", item.rating));
            binding.authorTextView.setText(item.authorName);
            binding.dateTextView.setText(item.relativeTimeDescription);
            binding.contentTextView.setText(item.text);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ReviewItemViewHolder extends RecyclerView.ViewHolder {
            final ItemReviewBinding binding;

            public ReviewItemViewHolder(ItemReviewBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
