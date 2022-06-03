package ark.noah.wtviewerfinalpls.ui.episodes;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;

public class EpisodesContainer implements Parcelable {
    String title, link;
    int number;
    LocalDate date;

    EpisodesContainer() {
        title = "";
        link = "";
        number = 0;
        date = LocalDate.MIN;
    }
    protected EpisodesContainer(Parcel in) {
        title = in.readString();
        link = in.readString();
        number = in.readInt();
    }

    public static final Creator<EpisodesContainer> CREATOR = new Creator<EpisodesContainer>() {
        @Override
        public EpisodesContainer createFromParcel(Parcel in) {
            return new EpisodesContainer(in);
        }

        @Override
        public EpisodesContainer[] newArray(int size) {
            return new EpisodesContainer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeInt(number);
    }
}
