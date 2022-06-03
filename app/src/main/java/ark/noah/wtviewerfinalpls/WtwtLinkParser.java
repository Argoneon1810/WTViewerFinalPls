package ark.noah.wtviewerfinalpls;

import java.net.MalformedURLException;
import java.net.URL;

public class WtwtLinkParser {
    public static boolean isLinkValid(String urlToParse) {
        String toon = "";
        String num = "";

        try {
            URL aURL = new URL(urlToParse);
            int queryDividerIndex;

            String queries = aURL.getQuery();
            if(queries != null && queries.length() > 0) {
                queryDividerIndex = queries.indexOf("&");
                if(queryDividerIndex!=-1) {
                    toon = queries.substring(0, queryDividerIndex);
                    num = queries.substring(queryDividerIndex+1);

                    int queryValueAssignIndex = toon.indexOf("=");
                    if(queryValueAssignIndex!=-1) {
                        toon = toon.substring(queryValueAssignIndex + 1);
                    }

                    queryValueAssignIndex = num.indexOf("=");
                    if(queryValueAssignIndex!=-1) {
                        num = num.substring(queryValueAssignIndex + 1);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            Integer.parseInt(toon);
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }
}
