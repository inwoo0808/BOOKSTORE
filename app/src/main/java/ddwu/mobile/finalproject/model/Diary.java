package ddwu.mobile.finalproject.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary_table")
public class Diary implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "book_image_url")
    public String bookImageUrl;

    @ColumnInfo(name = "book_title")
    public String bookTitle;
    public String writer;
    public String publish;

    public String title;
    public String review;

    public Diary() {
    }

    public Diary(String bookImageUrl, String bookTitle, String writer, String publish, String title, String review) {
        this.bookImageUrl = bookImageUrl;
        this.bookTitle = bookTitle;
        this.writer = writer;
        this.publish = publish;
        this.title = title;
        this.review = review;
    }

    protected Diary(Parcel in) {
        id = in.readInt();
        bookImageUrl = in.readString();
        bookTitle = in.readString();
        writer = in.readString();
        publish = in.readString();
        title = in.readString();
        review = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(bookImageUrl);
        dest.writeString(bookTitle);
        dest.writeString(writer);
        dest.writeString(publish);
        dest.writeString(title);
        dest.writeString(review);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getWriter() {
        return writer;
    }

    public String getPublish() {
        return publish;
    }

    public String getTitle() {
        return title;
    }

    public String getReview() {
        return review;
    }

    @Override
    public String toString() {
        return "책 제목 : " + bookTitle + '\n' +
                "저자 : " + writer + '\n' +
                "출판사 : " + publish + '\n' + '\n' +
                title + '\n' + review;
    }
}
