package ddwu.mobile.finalproject.ui.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ddwu.mobile.finalproject.R;
import ddwu.mobile.finalproject.databinding.ActivityDiaryEditorBinding;
import ddwu.mobile.finalproject.model.Diary;
import ddwu.mobile.finalproject.model.NaverBook;
import ddwu.mobile.finalproject.source.DiaryDB;

public class DiaryEditorActivity extends AppCompatActivity {
    private NaverBook book;
    private Diary diary;
    private ActivityDiaryEditorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            book = getIntent().getParcelableExtra("book");
            diary = getIntent().getParcelableExtra("diary");
        }

        if (savedInstanceState != null) {
            book = savedInstanceState.getParcelable("book");
            diary = savedInstanceState.getParcelable("diary");
        }

        if (book == null && diary == null) {
            finish();
            return;
        }

        binding = ActivityDiaryEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        List<TextInputLayout> textFields = Arrays.asList(binding.bookTitleTextField,
                binding.authorTextField,
                binding.publisherTextField,
                binding.reviewTitleTextField,
                binding.reviewContentTextField);
        for (TextInputLayout textField : textFields) {
            textField.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    boolean validation = true;
                    for (TextInputLayout field : textFields) {
                        if (field.getEditText().getText().toString().trim().isEmpty()) {
                            validation = false;
                            break;
                        }
                    }

                    binding.saveButton.setEnabled(validation);
                }
            });
        }

        if (this.diary == null) {
            binding.toolbar.getMenu().findItem(R.id.action_share).setVisible(false);

            Glide.with(this)
                    .load(book.getImage())
                    .centerCrop()
                    .into(binding.imageView);

            binding.bookTitleTextField.getEditText().setText(book.getTitle());
            binding.authorTextField.getEditText().setText(TextUtils.isEmpty(book.getAuthor()) ? book.getPublisher() : book.getAuthor());
            binding.publisherTextField.getEditText().setText(book.getPublisher());

        } else {
            binding.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_share) {
                    share();
                }

                return false;
            });
            Glide.with(this)
                    .load(diary.getBookImageUrl())
                    .centerCrop()
                    .into(binding.imageView);

            binding.bookTitleTextField.getEditText().setText(diary.bookTitle);
            binding.authorTextField.getEditText().setText(diary.writer);
            binding.publisherTextField.getEditText().setText(diary.publish);
            binding.reviewTitleTextField.getEditText().setText(diary.title);
            binding.reviewContentTextField.getEditText().setText(diary.review);
        }

        binding.saveButton.setOnClickListener(v -> save());
        binding.cancelButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("diary", diary);
        super.onSaveInstanceState(outState);
    }

    private void save() {
        String imageUrl = book != null ? book.getImage() : diary.bookImageUrl;
        String bookTitle = binding.bookTitleTextField.getEditText().getText().toString().trim();
        String writer = binding.authorTextField.getEditText().getText().toString().trim();
        String publish = binding.publisherTextField.getEditText().getText().toString().trim();
        String title = binding.reviewTitleTextField.getEditText().getText().toString().trim();
        String review = binding.reviewContentTextField.getEditText().getText().toString().trim();

        final Diary diary = new Diary(imageUrl, bookTitle, writer, publish, title, review);

        if (this.diary == null) {
            DiaryDB diaryDB = DiaryDB.getDatabase(getApplicationContext());
            new Thread() {
                @Override
                public void run() {
                    diaryDB.diaryDao().insertDiary(diary);
                }
            }.start();

        } else {
            diary.id = this.diary.id;

            DiaryDB diaryDB = DiaryDB.getDatabase(getApplicationContext());
            new Thread() {
                @Override
                public void run() {
                    diaryDB.diaryDao().updateDiary(diary);
                }
            }.start();
        }

        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void share() {
        String imageUrl = book != null ? book.getImage() : diary.bookImageUrl;

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        File directory = new File(getExternalCacheDir(), "myalbum");
                        directory.mkdirs();

                        File file = new File(directory.getPath(), "" + new Date().getTime() + ".jpg");
                        try {
                            FileOutputStream fos = new FileOutputStream((file));
                            resource.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Uri bmpUri = FileProvider.getUriForFile(DiaryEditorActivity.this, "ddwu.mobile.finalproject", file);

                        Log.d("DiaryEditorActivity", diary.toString());

                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, diary.toString());
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "리뷰 공유"));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}