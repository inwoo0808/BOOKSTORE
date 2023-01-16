package ddwu.mobile.finalproject.source;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ddwu.mobile.finalproject.model.Diary;

@Database(entities = {Diary.class}, version = 2)
public abstract class DiaryDB extends RoomDatabase {
    public abstract DiaryDao diaryDao();

    private static volatile DiaryDB INSTANCE;

    static public DiaryDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DiaryDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DiaryDB.class, "diary_db.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
