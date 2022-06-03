package ark.noah.wtviewerfinalpls.ui.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class ToonsContainer implements Parcelable {
    private static final int SUNDAY_FLAG    = 0b1000000;
    private static final int MONDAY_FLAG    = 0b0100000;
    private static final int TUESDAY_FLAG   = 0b0010000;
    private static final int WEDNESDAY_FLAG = 0b0001000;
    private static final int THURSDAY_FLAG  = 0b0000100;
    private static final int FRIDAY_FLAG    = 0b0000010;
    private static final int SATURDAY_FLAG  = 0b0000001;

    public String toonName, toonType;
    public int toonID, episodeID, releaseWeekdays;

    protected ToonsContainer(Parcel in) {
        toonName = in.readString();
        toonType = in.readString();
        toonID = in.readInt();
        episodeID = in.readInt();
        releaseWeekdays = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toonName);
        dest.writeString(toonType);
        dest.writeInt(toonID);
        dest.writeInt(episodeID);
        dest.writeInt(releaseWeekdays);
    }

    public static final Creator<ToonsContainer> CREATOR = new Creator<ToonsContainer>() {
        @Override
        public ToonsContainer createFromParcel(Parcel in) {
            return new ToonsContainer(in);
        }

        @Override
        public ToonsContainer[] newArray(int size) {
            return new ToonsContainer[size];
        }
    };

    private boolean releaseMatchesFlag(int release, int flag) { return (release & flag) == flag; }
    public Integer[] getAllReleaseDaysInArray() {
        ArrayList<Integer> releaseDays = new ArrayList<>();
        if     (releaseMatchesFlag(releaseWeekdays, SUNDAY_FLAG   )) releaseDays.add(Calendar.SUNDAY   );
        else if(releaseMatchesFlag(releaseWeekdays, MONDAY_FLAG   )) releaseDays.add(Calendar.MONDAY   );
        else if(releaseMatchesFlag(releaseWeekdays, TUESDAY_FLAG  )) releaseDays.add(Calendar.TUESDAY  );
        else if(releaseMatchesFlag(releaseWeekdays, WEDNESDAY_FLAG)) releaseDays.add(Calendar.WEDNESDAY);
        else if(releaseMatchesFlag(releaseWeekdays, THURSDAY_FLAG )) releaseDays.add(Calendar.THURSDAY );
        else if(releaseMatchesFlag(releaseWeekdays, FRIDAY_FLAG   )) releaseDays.add(Calendar.FRIDAY   );
        else if(releaseMatchesFlag(releaseWeekdays, SATURDAY_FLAG )) releaseDays.add(Calendar.SATURDAY );

        return (Integer[]) releaseDays.toArray();
    }

    public String getAllReleaseDaysInString() {
        String releaseDays = "";
        if(releaseMatchesFlag(releaseWeekdays, SUNDAY_FLAG   )) releaseDays += "SUN";
        if(releaseMatchesFlag(releaseWeekdays, MONDAY_FLAG   )) releaseDays += releaseDays.length() > 0 ? ", MON" :  "MON";
        if(releaseMatchesFlag(releaseWeekdays, TUESDAY_FLAG  )) releaseDays += releaseDays.length() > 0 ? ", TUE" :  "TUE";
        if(releaseMatchesFlag(releaseWeekdays, WEDNESDAY_FLAG)) releaseDays += releaseDays.length() > 0 ? ", WED" :  "WED";
        if(releaseMatchesFlag(releaseWeekdays, THURSDAY_FLAG )) releaseDays += releaseDays.length() > 0 ? ", THU" :  "THU";
        if(releaseMatchesFlag(releaseWeekdays, FRIDAY_FLAG   )) releaseDays += releaseDays.length() > 0 ? ", FRI" :  "FRI";
        if(releaseMatchesFlag(releaseWeekdays, SATURDAY_FLAG )) releaseDays += releaseDays.length() > 0 ? ", SAT" :  "SAT";
        return releaseDays;
    }
}
