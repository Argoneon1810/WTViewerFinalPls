package ark.noah.wtviewerfinalpls.ui.episodes;

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
import androidx.navigation.Navigation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import ark.noah.wtviewerfinalpls.EntryPointGetter;
import ark.noah.wtviewerfinalpls.ExecutorRunner;
import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.WtwtLinkParser;
import ark.noah.wtviewerfinalpls.databinding.FragmentEpisodesBinding;
import ark.noah.wtviewerfinalpls.ui.main.FragmentMainArgs;
import ark.noah.wtviewerfinalpls.ui.main.ToonsAdapter;
import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class FragmentEpisodes extends Fragment implements MainActivity.BackPressEvent {
    private EpisodesViewModel galleryViewModel;
    private FragmentEpisodesBinding binding;

    private ToonsContainer currentToon;

    private String baseLink;

    final String LINKS_ARRAY_KEY = "links";
    final String NUMBERS_ARRAY_KEY = "numbers";
    final String TITLE_ARRAY_KEY = "titles";
    final String DATES_ARRAY_KEY = "dates";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(EpisodesViewModel.class);

        binding = FragmentEpisodesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ((MainActivity)requireActivity()).hideAllFABs();

        ArrayList<EpisodesContainer> mList = new ArrayList<>();
        EpisodesAdapter adapter = new EpisodesAdapter(mList);
        binding.recEpisodes.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            FragmentEpisodesDirections.ActionNavGalleryToNavSlideshow action =
                    FragmentEpisodesDirections.actionNavGalleryToNavSlideshow(EntryPointGetter.getLastValidEntryPoint() + adapter.getItem(position).link);
            Navigation.findNavController(view).navigate(action);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        currentToon = FragmentEpisodesArgs.fromBundle(getArguments()).getToon();
        baseLink = WtwtLinkParser.rebuildLinkEpisodes(currentToon);
        stopThreadIfAlive();
        episodesGetterThread.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).resumedFromOtherFragment();
    }

    public void stopThreadIfAlive() {
        try {
            if(episodesGetterThread.isAlive()) {
                episodesGetterThread.wait();
                episodesGetterThread.interrupt();
                episodesGetterThread = new Thread(this::run);
            } else if(episodesGetterThread.getState() == Thread.State.TERMINATED) {
                episodesGetterThread = null;
                episodesGetterThread = new Thread(this::run);
            } else {
                Log.i("", "entry point getter thread status: " + episodesGetterThread.getState().toString());
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler((m) -> {
        if(binding == null) return false;

        Bundle bundle = m.getData();

        ArrayList<EpisodesContainer> containers = new ArrayList<>();

        ArrayList<String> links = bundle.getStringArrayList(LINKS_ARRAY_KEY);
        ArrayList<String> numbers = bundle.getStringArrayList(NUMBERS_ARRAY_KEY);
        ArrayList<String> titles = bundle.getStringArrayList(TITLE_ARRAY_KEY);
        ArrayList<String> dates = bundle.getStringArrayList(DATES_ARRAY_KEY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < titles.size(); ++i) {
            EpisodesContainer container = new EpisodesContainer();
            container.link = links.get(i);
            container.number = Integer.parseInt(numbers.get(i));
            container.title = titles.get(i);
            container.date = LocalDate.parse(dates.get(i), formatter);
            containers.add(container);
        }
        Log.i("", "populated containers in handler length: " + containers.size());

        EpisodesAdapter adapter = (EpisodesAdapter) Objects.requireNonNull(binding.recEpisodes.getAdapter());
        adapter.updateAllData(containers);
        Log.i("", "populated containers in handler length: " + adapter.mData.size());
        return false;
    });

    Thread episodesGetterThread = new Thread(this::run);
    private void run() {
        Document doc;

        Bundle bundle = new Bundle();

        try {
            doc = Jsoup.connect(baseLink).get();

            Elements element = doc.select("div.left-box").select("ul.list");

            Iterator<Element> elementIteratorLinks = element.select("a").iterator();
            Iterator<Element> elementIteratorDatas = element.select("div.list-box").iterator();
            Iterator<Element> elementIteratorTitles = element.select("div.subject").iterator();

            ArrayList<String> links = new ArrayList<>();
            ArrayList<String> numbers = new ArrayList<>();
            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> dates = new ArrayList<>();


            while (elementIteratorLinks.hasNext()) {
                links.add(elementIteratorLinks.next().attr("href"));
                String[] allText = elementIteratorDatas.next().text().split(" ");
                numbers.add(allText[0]);
                titles.add(elementIteratorTitles.next().ownText());
                dates.add(allText[allText.length-1]);
            }

            bundle.putStringArrayList(LINKS_ARRAY_KEY, links);
            bundle.putStringArrayList(NUMBERS_ARRAY_KEY, numbers);
            bundle.putStringArrayList(TITLE_ARRAY_KEY, titles);
            bundle.putStringArrayList(DATES_ARRAY_KEY, dates);

            Message msg = handler.obtainMessage();
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressedExtra() {
        stopThreadIfAlive();
        return true;
    }
}