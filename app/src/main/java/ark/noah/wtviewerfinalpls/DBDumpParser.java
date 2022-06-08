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

        String titleSub = value.substring(title_index, type_index-1);
        titleSub = titleSub.substring(titleSub.indexOf("=")+1).trim();
        String title = titleSub;

        String typeSub = value.substring(type_index, toonid_index-1);
        typeSub = typeSub.substring(typeSub.indexOf("=")+1).trim();
        String type = typeSub;

        String toonidSub = value.substring(toonid_index, epiid_index-1);
        toonidSub = toonidSub.substring(toonidSub.indexOf("=")+1).trim();
        int toonid = Integer.parseInt(toonidSub);

        String epiidSub = value.substring(epiid_index, release_index-1);
        epiidSub = epiidSub.substring(epiidSub.indexOf("=")+1).trim();
        int epiid = Integer.parseInt(epiidSub);

        String releaseSub = value.substring(release_index);
        releaseSub = releaseSub.substring(releaseSub.indexOf("=")+1).trim();
        int release = Integer.parseInt(releaseSub);

        return new ToonsContainer(-1, title, type, toonid, epiid, release);
    }
}
