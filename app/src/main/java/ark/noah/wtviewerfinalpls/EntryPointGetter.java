package ark.noah.wtviewerfinalpls;

import android.os.Handler;
import android.os.Looper;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class EntryPointGetter {
    public static class EntryPointParser {
        public static final String defaultEntryRoot = "https://nicelink6.com/";
        public static final String[] validEntryRootList = {
                "https://nicelink1.com/",
                "https://nicelink2.com/",
                "https://nicelink3.com/",
                "https://nicelink4.com/",
                "https://nicelink5.com/",
                "https://nicelink6.com/",
                "https://nicelink7.com/",
                "https://nicelink8.com/",
                "https://nicelink9.com/",
                "https://nicelink10.com/"
        };

        private static String currentEntryRoot;

        interface Callback {
            void onValidLinkSet();
        }

        static void validateLink(String url, Callback callback) {
            new Thread(() -> {
                if (Arrays.stream(validEntryRootList).anyMatch(s -> {
                    try {
                        return s.equals(new URL(url).toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return false;
                    }
                })) {
                    currentEntryRoot = url;
                    callback.onValidLinkSet();
                } else {
                    currentEntryRoot = defaultEntryRoot;
                    callback.onValidLinkSet();
                }
            }).start();
        }

        public static String getCurrentEntryRoot() { return currentEntryRoot; }
    }

    private static String lastValidEntryPoint;

    private static ArrayList<String> entrypointArrayList = new ArrayList<>();

    static Thread entryLinkGetterThread = new Thread(EntryPointGetter::run);

    static Callback callback;

    private static void run() {
        Document doc;

        try {
            doc = Jsoup.connect(EntryPointParser.currentEntryRoot).get();

            //get links
            Elements element = doc.select("ul.list");
            for (Element value : element.select("a"))
                entrypointArrayList.add(value.attr("href"));

            lastValidEntryPoint = entrypointArrayList.get(1);       //0 = wolf, 1 = wtwt, etc.

            new Handler(Looper.getMainLooper()).post(() -> {
                if(callback != null)
                    callback.onEntryAquired(lastValidEntryPoint);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void onEntryAquired(String url);
    }

    public static void requestEntryPointLink(Callback callback) {
        EntryPointGetter.callback = callback;
        stopThreadIfAlive();
        entryLinkGetterThread.start();
    }

    public static void stopThreadIfAlive() {
        try {
            if(entryLinkGetterThread.isAlive()) {
                entryLinkGetterThread.wait();
                entryLinkGetterThread.interrupt();
                entryLinkGetterThread = new Thread(EntryPointGetter::run);
            } else if(entryLinkGetterThread.getState() == Thread.State.TERMINATED) {
                entryLinkGetterThread = null;
                entryLinkGetterThread = new Thread(EntryPointGetter::run);
            } else {
                Log.i("", "entry point getter thread status: " + entryLinkGetterThread.getState().toString());
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getLastValidEntryPoint() {
        if(!(lastValidEntryPoint == null)) return lastValidEntryPoint;
        return "";
    }

    private static boolean isValidEntryRoot(String url) {
        return Arrays.stream(EntryPointParser.validEntryRootList).anyMatch(s -> {
            try {
                return s.equals(new URL(url).toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }
    public static boolean trySetEntryRoot(String url) {
        if(isValidEntryRoot(url)) {
            EntryPointParser.currentEntryRoot = url;
            return true;
        }
        else {
            EntryPointParser.currentEntryRoot = EntryPointParser.defaultEntryRoot;
            return false;
        }
    }
}
