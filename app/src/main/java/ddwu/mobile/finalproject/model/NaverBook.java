package ddwu.mobile.finalproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NaverBook implements Parcelable {
    private int id;
    private String title;
    private String author;
    private String publisher;
    private String link;
    private String image;

    public NaverBook() {
    }

    protected NaverBook(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        publisher = in.readString();
        link = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(publisher);
        dest.writeString(link);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NaverBook> CREATOR = new Creator<NaverBook>() {
        @Override
        public NaverBook createFromParcel(Parcel in) {
            return new NaverBook(in);
        }

        @Override
        public NaverBook[] newArray(int size) {
            return new NaverBook[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }
}
