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

    public ToonsContainer() {}

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

        if(releasesOnSunday())    releaseDays.add(Calendar.SUNDAY   );
        if(releasesOnMonday())    releaseDays.add(Calendar.MONDAY   );
        if(releasesOnTuesday())   releaseDays.add(Calendar.TUESDAY  );
        if(releasesOnWednesday()) releaseDays.add(Calendar.WEDNESDAY);
        if(releasesOnThursday())  releaseDays.add(Calendar.THURSDAY );
        if(releasesOnFriday())    releaseDays.add(Calendar.FRIDAY   );
        if(releasesOnSaturday())  releaseDays.add(Calendar.SATURDAY );

        return releaseDays.toArray(new Integer[0]);
    }

    public String getAllReleaseDaysInString() {
        String releaseDays = "";

        if(releasesOnSunday())    releaseDays += "SUN";
        if(releasesOnMonday())    releaseDays += releaseDays.length() > 0 ? ", MON" :  "MON";
        if(releasesOnTuesday())   releaseDays += releaseDays.length() > 0 ? ", TUE" :  "TUE";
        if(releasesOnWednesday()) releaseDays += releaseDays.length() > 0 ? ", WED" :  "WED";
        if(releasesOnThursday())  releaseDays += releaseDays.length() > 0 ? ", THU" :  "THU";
        if(releasesOnFriday())    releaseDays += releaseDays.length() > 0 ? ", FRI" :  "FRI";
        if(releasesOnSaturday())  releaseDays += releaseDays.length() > 0 ? ", SAT" :  "SAT";
        return releaseDays;
    }

    public boolean releasesOnSunday()    { return releaseMatchesFlag(releaseWeekdays, SUNDAY_FLAG); }
    public boolean releasesOnMonday()    { return releaseMatchesFlag(releaseWeekdays, MONDAY_FLAG); }
    public boolean releasesOnTuesday()   { return releaseMatchesFlag(releaseWeekdays, TUESDAY_FLAG); }
    public boolean releasesOnWednesday() { return releaseMatchesFlag(releaseWeekdays, WEDNESDAY_FLAG); }
    public boolean releasesOnThursday()  { return releaseMatchesFlag(releaseWeekdays, THURSDAY_FLAG); }
    public boolean releasesOnFriday()    { return releaseMatchesFlag(releaseWeekdays, FRIDAY_FLAG); }
    public boolean releasesOnSaturday()  { return releaseMatchesFlag(releaseWeekdays, SATURDAY_FLAG); }

    //region flag modifying methods
    public void changeFlagSunday()    { releaseWeekdays = releaseWeekdays ^ SUNDAY_FLAG;    }
    public void changeFlagMonday()    { releaseWeekdays = releaseWeekdays ^ MONDAY_FLAG;    }
    public void changeFlagTuesday()   { releaseWeekdays = releaseWeekdays ^ TUESDAY_FLAG;   }
    public void changeFlagWednesday() { releaseWeekdays = releaseWeekdays ^ WEDNESDAY_FLAG; }
    public void changeFlagThursday()  { releaseWeekdays = releaseWeekdays ^ THURSDAY_FLAG;  }
    public void changeFlagFriday()    { releaseWeekdays = releaseWeekdays ^ FRIDAY_FLAG;    }
    public void changeFlagSaturday()  { releaseWeekdays = releaseWeekdays ^ SATURDAY_FLAG;  }

    public void enableFlagSunday()    { releaseWeekdays |= 1 << 6; }
    public void enableFlagMonday()    { releaseWeekdays |= 1 << 5; }
    public void enableFlagTuesday()   { releaseWeekdays |= 1 << 4; }
    public void enableFlagWednesday() { releaseWeekdays |= 1 << 3; }
    public void enableFlagThursday()  { releaseWeekdays |= 1 << 2; }
    public void enableFlagFriday()    { releaseWeekdays |= 1 << 1; }
    public void enableFlagSaturday()  { releaseWeekdays |= 1; }

    public void disableFlagSunday()    { releaseWeekdays &= ~(1 << 6); }
    public void disableFlagMonday()    { releaseWeekdays &= ~(1 << 5); }
    public void disableFlagTuesday()   { releaseWeekdays &= ~(1 << 4); }
    public void disableFlagWednesday() { releaseWeekdays &= ~(1 << 3); }
    public void disableFlagThursday()  { releaseWeekdays &= ~(1 << 2); }
    public void disableFlagFriday()    { releaseWeekdays &= ~(1 << 1); }
    public void disableFlagSaturday()  { releaseWeekdays &= ~(1); }
    //endregion
}
