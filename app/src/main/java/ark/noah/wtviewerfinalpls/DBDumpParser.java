package ark.noah.wtviewerfinalpls;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class DBDumpParser {
    public static ArrayList<ToonsContainer> restoreDump(StringBuilder sb) {
        String value = sb.toString();
        Queue<String> allSubs = new LinkedList<>();
        allSubs = getAllSubs(allSubs, value);

        ArrayList<ToonsContainer> toReturn = new ArrayList<>();
        String current;
        while((current = allSubs.poll()) != null)
            toReturn.add(parseItem(current));

        allSubs.clear();
        return toReturn;
    }

    static Queue<String> getAllSubs(Queue<String> toReturn, String value) {
        int index = value.indexOf("}");
        if(index == -1) return toReturn;
        toReturn.add(value.substring(0, index));
        return getAllSubs(toReturn, value.substring(index+1));
    }

    static ToonsContainer parseItem(String value) {
        int title_index = value.indexOf(DBHelper.COL_TITLE);
        int type_index = value.indexOf(DBHelper.COL_TYPE);
        int toonid_index = value.indexOf(DBHelper.COL_TOONID);
        int epiid_index = value.indexOf(DBHelper.COL_EPIID);
        int release_index = value.indexOf(DBHelper.COL_RELEASEDAY);
        int hide_index = value.indexOf(DBHelper.COL_HIDE);

        String title, type;
        int toonid, epiid, release;
        boolean hide;

        String titleSub = value.substring(title_index, type_index-1);
        titleSub = titleSub.substring(titleSub.indexOf("=")+1).trim();
        title = titleSub;

        String typeSub = value.substring(type_index, toonid_index-1);
        typeSub = typeSub.substring(typeSub.indexOf("=")+1).trim();
        type = typeSub;

        String toonidSub = value.substring(toonid_index, epiid_index-1);
        toonidSub = toonidSub.substring(toonidSub.indexOf("=")+1).trim();
        toonid = Integer.parseInt(toonidSub);

        String epiidSub = value.substring(epiid_index, release_index-1);
        epiidSub = epiidSub.substring(epiidSub.indexOf("=")+1).trim();
        epiid = Integer.parseInt(epiidSub);

        if(hide_index == -1) {                                                  //compatibilty option (because the column 'hide' is added later.)
            String releaseSub = value.substring(release_index);
            releaseSub = releaseSub.substring(releaseSub.indexOf("=")+1).trim();
            release = Integer.parseInt(releaseSub);
        } else {
            String releaseSub = value.substring(release_index, hide_index-1);
            releaseSub = releaseSub.substring(releaseSub.indexOf("=")+1).trim();
            release = Integer.parseInt(releaseSub);
        }

        if(hide_index == -1) {                                                  //compatibilty option (because the column 'hide' is added later.)
            hide = false;
        } else {
            String hideSub = value.substring(hide_index);
            hideSub = hideSub.substring(hideSub.indexOf("=") + 1).trim();
            hide = Integer.parseInt(hideSub) != 0;
        }
        return new ToonsContainer(-1, title, type, toonid, epiid, release, hide);
    }
}
