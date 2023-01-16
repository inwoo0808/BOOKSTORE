package ddwu.mobile.finalproject.ui.diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import ddwu.mobile.finalproject.databinding.ActivityDiaryListBinding;
import ddwu.mobile.finalproject.model.Diary;
import ddwu.mobile.finalproject.source.DiaryDB;
import ddwu.mobile.finalproject.source.DiaryDao;

public class DiaryListActivity extends AppCompatActivity {
    private ActivityDiaryListBinding binding;
    private DiaryDao diaryDao;
    private final DiaryListAdapter adapter = new DiaryListAdapter();

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private LiveData<List<Diary>> diaries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDiaryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DiaryDB diaryDB = DiaryDB.getDatabase(getApplicationContext());
        diaryDao = diaryDB.diaryDao();

        diaries = Transformations.switchMap(query, query -> {
            if (query.isEmpty()) {
                return diaryDao.getAllDiary();
            } else {
                return diaryDao.getDiaryByTitle(query);
            }
        });

        adapter.setOnItemClickListener(new DiaryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Diary diary) {
                Intent intent = new Intent(DiaryListActivity.this, DiaryEditorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("diary", diary);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(Diary diary) {
                new MaterialAlertDialogBuilder(DiaryListActivity.this).setTitle("리뷰 삭제")
                        .setMessage("선택하신 리뷰를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (DialogInterface.OnClickListener) (dialog, which) -> {
                            new Thread() {
                                @Override
                                public void run() {
                                    diaryDao.deleteDiary(diary);
                                }
                            }.start();
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        diaries.observe(this, diaries -> {
            adapter.submitList(diaries);
            binding.emptyView.setVisibility(diaries.isEmpty() ? View.VISIBLE : View.GONE);
        });

        binding.searchTextField.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setQuery();
            }

            return false;
        });

        binding.searchTextField.setStartIconOnClickListener(v -> onBackPressed());
        binding.searchTextField.setEndIconOnClickListener(v -> setQuery());
    }

    private void setQuery() {
        query.setValue(binding.searchTextField.getEditText().toString().trim());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchTextField.getEditText().getWindowToken(), 0);
        binding.searchTextField.getEditText().clearFocus();
    }
}