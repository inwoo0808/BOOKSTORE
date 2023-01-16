package ddwu.mobile.finalproject.source;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ddwu.mobile.finalproject.model.Diary;

@Dao
public interface DiaryDao {
    @Query("SELECT * FROM diary_table")
    LiveData<List<Diary>> getAllDiary();

    @Insert
    void insertDiary(Diary diary);

    @Update
    void updateDiary(Diary Diary);

    @Delete
    void deleteDiary(Diary diary);

    @Query("SELECT * FROM diary_table WHERE title LIKE '%' || :title || '%' OR book_title LIKE '%' || :title || '%'")
    LiveData<List<Diary>> getDiaryByTitle(String title);

    @Query("SELECT * FROM diary_table WHERE id = :id")
    LiveData<List<Diary>> getDiaryById(long id);
}
