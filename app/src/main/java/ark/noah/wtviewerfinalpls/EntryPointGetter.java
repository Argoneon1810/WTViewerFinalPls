package ark.noah.wtviewerfinalpls;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class EntryPointGetter {
    private static String lastValidEntryPoint;

    private static ArrayList<String> entrypointArrayList = new ArrayList<>();

    static Thread entryLinkGetterThread = new Thread(EntryPointGetter::run);

    static Callback callback;

    private static void run() {
        String url = "https://nicelink6.com/";
        Document doc;

        try {
            doc = Jsoup.connect(url).get();

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
}
