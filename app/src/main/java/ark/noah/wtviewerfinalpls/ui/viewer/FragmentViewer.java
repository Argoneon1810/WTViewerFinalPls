package ark.noah.wtviewerfinalpls.ui.viewer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.databinding.FragmentViewerBinding;

public class FragmentViewer extends Fragment {

    private ViewerViewModel slideshowViewModel;
    private FragmentViewerBinding binding;

    private String currentUrl;

    final String IMAGE_LINKS_ARRAY_KEY = "imagelinks";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(ViewerViewModel.class);

        binding = FragmentViewerBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        currentUrl = FragmentViewerArgs.fromBundle(getArguments()).getUrl();
        networkThread.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    Handler handler = new Handler((m) -> {
        if(binding == null) return false;

        Bundle bundle = m.getData();

        ArrayList<ViewerContainer> containers = new ArrayList<>();

        ArrayList<String> imageURLs = bundle.getStringArrayList(IMAGE_LINKS_ARRAY_KEY);

        for (int i = 0; i < imageURLs.size(); ++i) {
            ViewerContainer container = new ViewerContainer();
            container.imageURL = imageURLs.get(i);
            containers.add(container);
        }

        binding.listViewer.setAdapter(new ViewerAdapter(containers, requireContext()));
        return false;
    });

    Thread networkThread = new Thread(() -> {
        Document doc;

        Bundle bundle = new Bundle();

        try {
            doc = Jsoup.connect(currentUrl).get();

            Elements element = doc.select("div.wview").select("div.wbody");

            ArrayList<String> imageURLs = new ArrayList<>();

            for (Element value : element.select("img"))
                imageURLs.add(value.attr("src"));

            bundle.putStringArrayList(IMAGE_LINKS_ARRAY_KEY, imageURLs);

            Message msg = handler.obtainMessage();
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
}