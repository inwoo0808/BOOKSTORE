package ddwu.mobile.finalproject.ui.book;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import ddwu.mobile.finalproject.R;
import ddwu.mobile.finalproject.databinding.ActivitySearchBookBinding;
import ddwu.mobile.finalproject.model.BookRoot;
import ddwu.mobile.finalproject.model.NaverBook;
import ddwu.mobile.finalproject.source.INaverBookSearchService;
import ddwu.mobile.finalproject.ui.diary.DiaryEditorActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchBookActivity extends AppCompatActivity {
    public static final String TAG = "SearchBookActivity";

    private ActivitySearchBookBinding binding;
    private final BookAdapter adapter = new BookAdapter();

    private INaverBookSearchService naverApiService;
    private Call<BookRoot> apiCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.naver_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        naverApiService = retrofit.create(INaverBookSearchService.class);

        binding.searchTextField.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
            }

            return false;
        });

        binding.searchTextField.setStartIconOnClickListener(v -> onBackPressed());
        binding.searchTextField.setEndIconOnClickListener(v -> search());

        adapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(NaverBook book) {
                Intent intent = new Intent(SearchBookActivity.this, DiaryEditorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("book", book);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(NaverBook book) {
                saveImage(book);
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void search() {
        String query = binding.searchTextField.getEditText().getText().toString().trim();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchTextField.getEditText().getWindowToken(), 0);
        binding.searchTextField.getEditText().clearFocus();

        if (query.isEmpty()) {
            adapter.submitList(Collections.emptyList());
            binding.emptyView.setVisibility(View.GONE);
            binding.progressIndicator.setVisibility(View.GONE);
            return;
        }

        if (apiCall != null) {
            apiCall.cancel();
            apiCall = null;
        }

        binding.progressIndicator.setVisibility(View.VISIBLE);

        apiCall = naverApiService.getBooks("4bYKxTqeChhjkLiBks6P", "F5xQIqm6b1", 50, 1, query);
        apiCall.enqueue(new Callback<BookRoot>() {
            @Override
            public void onResponse(Call<BookRoot> call, Response<BookRoot> response) {
                BookRoot body = response.body();
                adapter.submitList(body.getItems());

                binding.emptyView.setVisibility(body.getItems().isEmpty() ? View.VISIBLE : View.GONE);
                binding.progressIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BookRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void saveImage(NaverBook book) {
        Glide.with(SearchBookActivity.this)
                .asBitmap()
                .load(book.getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        // 파일 처리 클래스로 분리 필요
                        if (isExternalStorageWritable()) {
                            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                    "myalbum");
                            if (!file.mkdirs()) {
                                Log.d(TAG, "directory not created");
                            }
                            File saveFile = new File(file.getPath(), "test.jpg");
                            try {
                                FileOutputStream fos = new FileOutputStream((saveFile));
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                                Toast.makeText(SearchBookActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}
