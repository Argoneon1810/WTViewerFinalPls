package ark.noah.wtviewerfinalpls;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class EntryPointGetter {
    private static void run() {
        String url = "https://nicelink6.com/";
        Document doc;

        try {
            doc = Jsoup.connect(url).get();

            //get links
            Elements element = doc.select("ul.list");
            for (Element value : element.select("a"))
                entrypointArrayList.add(value.attr("href"));

            Message msg = entryPointHandler.obtainMessage();
            entryPointHandler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void onEntryAquired(String url);
    }

    private static ArrayList<String> entrypointArrayList = new ArrayList<>();

    static Handler entryPointHandler;
    static Thread entryLinkGetterThread = new Thread(EntryPointGetter::run);

    public static void requestEntryPointLink(Callback callback) throws InterruptedException {
        entryPointHandler = new Handler((m) -> {
            callback.onEntryAquired(entrypointArrayList.get(1));        //0 = wolf, 1 = wtwt, etc.
            return false;
        });
        stopThreadIfAlive();
        entryLinkGetterThread.start();
    }

    public static void stopThreadIfAlive() throws InterruptedException {
        if(entryLinkGetterThread.isAlive()) {
            entryLinkGetterThread.wait();
            entryLinkGetterThread.interrupt();
            entryLinkGetterThread = new Thread(EntryPointGetter::run);
        } else if(entryLinkGetterThread.getState() == Thread.State.TERMINATED) {
            entryLinkGetterThread = null;
            entryLinkGetterThread = new Thread(EntryPointGetter::run);
        } else {
            Log.i("", entryLinkGetterThread.getState().toString());
        }
    }
}
