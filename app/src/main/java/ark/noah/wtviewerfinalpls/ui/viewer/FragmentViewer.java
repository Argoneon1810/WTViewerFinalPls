package ark.noah.wtviewerfinalpls.ui.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ark.noah.wtviewerfinalpls.databinding.FragmentViewerBinding;

public class FragmentViewer extends Fragment {

    private ViewerViewModel slideshowViewModel;
    private FragmentViewerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(ViewerViewModel.class);

        binding = FragmentViewerBinding.inflate(inflater, container, false);
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