package ark.noah.wtviewerfinalpls.ui.help;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.databinding.FragmentHelpBinding;
import ark.noah.wtviewerfinalpls.databinding.FragmentViewerBinding;
import ark.noah.wtviewerfinalpls.ui.viewer.ViewerViewModel;

public class FragmentHelp extends Fragment {
    HelpViewModel helpViewModel;
    FragmentHelpBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        helpViewModel = new ViewModelProvider(this).get(HelpViewModel.class);
        binding = FragmentHelpBinding.inflate(inflater, container, false);

        ((MainActivity)requireActivity()).hideAllFABs();

        return binding.getRoot();
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
}