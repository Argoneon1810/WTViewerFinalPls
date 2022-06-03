package ark.noah.wtviewerfinalpls;

import android.content.Context;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class WtwtLinkParser {
    static class Info {
        int toonID;
        int episodeID;
        String toonType;
    }

    public static boolean isLinkValid(String urlToParse) {
        return extractInfo(urlToParse) != null;
    }

    private static Info extractInfo(String url) {
        String toon = "";
        String num = "";

        try {
            URL aURL = new URL(url);
            int queryDividerIndex;

            String queries = aURL.getQuery();
            if (queries != null && queries.length() > 0) {
                queryDividerIndex = queries.indexOf("&");
                if (queryDividerIndex != -1) {
                    toon = queries.substring(0, queryDividerIndex);
                    num = queries.substring(queryDividerIndex + 1);

                    int queryValueAssignIndex = toon.indexOf("=");
                    if (queryValueAssignIndex != -1) {
                        toon = toon.substring(queryValueAssignIndex + 1);
                    }

                    queryValueAssignIndex = num.indexOf("=");
                    if (queryValueAssignIndex != -1) {
                        num = num.substring(queryValueAssignIndex + 1);
                    }
                }
            }

            Info info = new Info();
            info.toonID = Integer.parseInt(toon);
            info.episodeID = Integer.parseInt(num);
            info.toonType = aURL.getPath();
            return info;
        } catch (MalformedURLException | NumberFormatException e) {
            return null;
        }
    }

    public static boolean tryPopulateInfo(Context applicationContext, String[] urls, int[] toonIDs, int[] episodeIds, String[] toonTypes) {
        for (int i = 0; i < urls.length; ++i) {
            Info info = extractInfo(urls[i]);

            if(info == null) {
                Toast.makeText(applicationContext, applicationContext.getString(R.string.notif_invalid_link_in_list), Toast.LENGTH_SHORT).show();
                return false;
            }

            toonIDs[i] = info.toonID;
            episodeIds[i] = info.episodeID;
            toonTypes[i] = info.toonType;
        }
        return true;
    }

    public static String rebuildLinkEpisodes(ToonsContainer container) {
        String entryPoint = EntryPointGetter.getLastValidEntryPoint();
        if(entryPoint.equals("")) return "";

        String temp = entryPoint + container.toonType;
        temp = temp.substring(0, temp.length()-1);
        temp = temp + "1";
        temp = temp + "?toon=" + container.toonID;

        return temp;
    }
}
