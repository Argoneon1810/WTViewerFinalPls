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

    static String postfix;

    public static boolean isLinkValid(String urlToParse) {
        return extractInfo(urlToParse) != null;
    }

    private static Info extractInfo(String url) {
        try {
            URL aUrl = new URL(url);
            Info info = new Info();
            info.toonID = extractToonId(aUrl);
            info.episodeID = extractEpisodeID(aUrl);
            info.toonType = aUrl.getPath();
            return info;
        } catch (MalformedURLException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int extractEpisodeID(String url) {
        try {
            return extractEpisodeID(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private static int extractEpisodeID(URL aURL) {
        String queries = aURL.getQuery();
        if (queries != null && queries.length() > 0) {
            int queryDividerIndex = queries.indexOf("&");
            if (queryDividerIndex != -1) {
                String latterQuery = queries.substring(queryDividerIndex + 1);      //this is for episode number
                return Integer.parseInt(latterQuery.substring(latterQuery.indexOf("=") + 1));
            }
        }
        return -1;
    }

    private static int extractToonId(URL aURL)  {
        String queries = aURL.getQuery();
        if (queries != null && queries.length() > 0) {
            int queryDividerIndex = queries.indexOf("&");
            if (queryDividerIndex != -1) {
                String priorQuery = queries.substring(0, queryDividerIndex);        //this is for toon number
                return Integer.parseInt(priorQuery.substring(priorQuery.indexOf("=") + 1));
            }
        }
        return -1;
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
        if(entryPoint.equals("")) {
            return "";
        }

        String postfix = container.toonType;
        postfix = postfix.substring(0, postfix.length()-1);
        postfix = postfix + "1";
        postfix = postfix + "?toon=" + container.toonID;

        return entryPoint + postfix;
    }
}
