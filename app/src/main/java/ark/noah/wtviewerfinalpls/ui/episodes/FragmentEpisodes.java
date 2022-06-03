package ark.noah.wtviewerfinalpls.ui.episodes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ark.noah.wtviewerfinalpls.databinding.FragmentEpisodesBinding;

public class FragmentEpisodes extends Fragment {

    private EpisodesViewModel galleryViewModel;
    private FragmentEpisodesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(EpisodesViewModel.class);

        binding = FragmentEpisodesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //TODO

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}