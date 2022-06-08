package ark.noah.wtviewerfinalpls.ui.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class ToonsContainer implements Parcelable {
    private static final int SUN_FLAG = 0b1000000;
    private static final int MON_FLAG = 0b0100000;
    private static final int TUE_FLAG = 0b0010000;
    private static final int WED_FLAG = 0b0001000;
    private static final int THU_FLAG = 0b0000100;
    private static final int FRI_FLAG = 0b0000010;
    private static final int SAT_FLAG = 0b0000001;

    public int dbID;
    public String toonName, toonType;
    public int toonID, episodeID, releaseWeekdays;

    public ToonsContainer(
        int dbID,
        String  toonName,
        String  toonType,
        int     toonID,
        int     episodeID,
        int     releaseWeekdays
    ) {
        this.dbID = dbID;
        this.toonName = toonName;
        this.toonType = toonType;
        this.toonID = toonID;
        this.episodeID = episodeID;
        this.releaseWeekdays = releaseWeekdays;
    }

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

        if(releasesOnSun()) releaseDays.add(Calendar.SUNDAY   );
        if(releasesOnMon()) releaseDays.add(Calendar.MONDAY   );
        if(releasesOnTue()) releaseDays.add(Calendar.TUESDAY  );
        if(releasesOnWed()) releaseDays.add(Calendar.WEDNESDAY);
        if(releasesOnThu()) releaseDays.add(Calendar.THURSDAY );
        if(releasesOnFri()) releaseDays.add(Calendar.FRIDAY   );
        if(releasesOnSat()) releaseDays.add(Calendar.SATURDAY );

        return releaseDays.toArray(new Integer[0]);
    }
    public int getFirstReleaseDay() {
        if(releasesOnSun()) return Calendar.SUNDAY   ;
        else if(releasesOnMon()) return Calendar.MONDAY   ;
        else if(releasesOnTue()) return Calendar.TUESDAY  ;
        else if(releasesOnWed()) return Calendar.WEDNESDAY;
        else if(releasesOnThu()) return Calendar.THURSDAY ;
        else if(releasesOnFri()) return Calendar.FRIDAY   ;
        else                     return Calendar.SATURDAY ;
    }

    public String getAllReleaseDaysInString() {
        String releaseDays = "";

        if(releasesOnSun()) releaseDays += "SUN";
        if(releasesOnMon()) releaseDays += releaseDays.length() > 0 ? ", MON" :  "MON";
        if(releasesOnTue()) releaseDays += releaseDays.length() > 0 ? ", TUE" :  "TUE";
        if(releasesOnWed()) releaseDays += releaseDays.length() > 0 ? ", WED" :  "WED";
        if(releasesOnThu()) releaseDays += releaseDays.length() > 0 ? ", THU" :  "THU";
        if(releasesOnFri()) releaseDays += releaseDays.length() > 0 ? ", FRI" :  "FRI";
        if(releasesOnSat()) releaseDays += releaseDays.length() > 0 ? ", SAT" :  "SAT";
        return releaseDays;
    }

    public boolean releasesOnSun() { return releaseMatchesFlag(releaseWeekdays, SUN_FLAG); }
    public boolean releasesOnMon() { return releaseMatchesFlag(releaseWeekdays, MON_FLAG); }
    public boolean releasesOnTue() { return releaseMatchesFlag(releaseWeekdays, TUE_FLAG); }
    public boolean releasesOnWed() { return releaseMatchesFlag(releaseWeekdays, WED_FLAG); }
    public boolean releasesOnThu() { return releaseMatchesFlag(releaseWeekdays, THU_FLAG); }
    public boolean releasesOnFri() { return releaseMatchesFlag(releaseWeekdays, FRI_FLAG); }
    public boolean releasesOnSat() { return releaseMatchesFlag(releaseWeekdays, SAT_FLAG); }

    //region flag modifying methods
    public void changeFlagSun() { releaseWeekdays = releaseWeekdays ^ SUN_FLAG;    }
    public void changeFlagMon() { releaseWeekdays = releaseWeekdays ^ MON_FLAG;    }
    public void changeFlagTue() { releaseWeekdays = releaseWeekdays ^ TUE_FLAG;   }
    public void changeFlagWed() { releaseWeekdays = releaseWeekdays ^ WED_FLAG; }
    public void changeFlagThu() { releaseWeekdays = releaseWeekdays ^ THU_FLAG;  }
    public void changeFlagFri() { releaseWeekdays = releaseWeekdays ^ FRI_FLAG;    }
    public void changeFlagSat() { releaseWeekdays = releaseWeekdays ^ SAT_FLAG;  }

    public void enableFlagSun() { releaseWeekdays |= 1 << 6; }
    public void enableFlagMon() { releaseWeekdays |= 1 << 5; }
    public void enableFlagTue() { releaseWeekdays |= 1 << 4; }
    public void enableFlagWed() { releaseWeekdays |= 1 << 3; }
    public void enableFlagThu() { releaseWeekdays |= 1 << 2; }
    public void enableFlagFri() { releaseWeekdays |= 1 << 1; }
    public void enableFlagSat() { releaseWeekdays |= 1; }

    public void disableFlagSun()    { releaseWeekdays &= ~(1 << 6); }
    public void disableFlagMon()    { releaseWeekdays &= ~(1 << 5); }
    public void disableFlagTue()   { releaseWeekdays &= ~(1 << 4); }
    public void disableFlagWed() { releaseWeekdays &= ~(1 << 3); }
    public void disableFlagThursday()  { releaseWeekdays &= ~(1 << 2); }
    public void disableFlagFriday()    { releaseWeekdays &= ~(1 << 1); }
    public void disableFlagSaturday()  { releaseWeekdays &= ~(1); }
    //endregion
}
