package ddwu.mobile.finalproject.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ddwu.mobile.finalproject.R;
import ddwu.mobile.finalproject.ui.book.SearchBookActivity;
import ddwu.mobile.finalproject.ui.diary.DiaryListActivity;
import ddwu.mobile.finalproject.ui.store.SearchBookStoreActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.write) {
            Intent write_intent = new Intent(this, DiaryListActivity.class);
            startActivity(write_intent);

        } else if (id == R.id.search) {
            Intent search_intent = new Intent(this, SearchBookActivity.class);
            startActivity(search_intent);

        } else if (id == R.id.Recommend) {
            Intent recommendIntent = new Intent(this, SearchBookStoreActivity.class);
            startActivity(recommendIntent);
        }
    }
}