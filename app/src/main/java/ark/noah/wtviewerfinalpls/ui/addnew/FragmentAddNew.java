package ark.noah.wtviewerfinalpls.ui.addnew;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.WtwtLinkParser;
import ark.noah.wtviewerfinalpls.databinding.FragmentAddNewBinding;
import ark.noah.wtviewerfinalpls.ui.addbyweb.FragmentWebDirections;
import ark.noah.wtviewerfinalpls.ui.main.ToonsAdapter;
import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class FragmentAddNew extends Fragment{
    private FragmentAddNewBinding binding;
    private AddNewViewModel addNewViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addNewViewModel = new ViewModelProvider(this).get(AddNewViewModel.class);
        binding = FragmentAddNewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ((MainActivity)requireActivity()).hideAllFABs();

        ArrayList<ToonsContainer> mList = new ArrayList<>();
        ReviewAdapter adapter = new ReviewAdapter(mList);
        binding.recAddnew.setAdapter(adapter);

        binding.fabConfirm.setOnClickListener((v) -> {
            FragmentAddNewDirections.ActionFragmentAddNewToNavMain action = FragmentAddNewDirections.actionFragmentAddNewToNavMain();
            action.setToon(adapter.getmData().toArray(new ToonsContainer[adapter.getItemCount()]));
            Navigation.findNavController(view).navigate(action);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ReviewAdapter adapter = (ReviewAdapter) binding.recAddnew.getAdapter();
        if(adapter == null) return;

        String[] urls = FragmentAddNewArgs.fromBundle(getArguments()).getUrls();

        if(urls == null) {
            Toast.makeText(requireContext(), "Data lost!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(urls.length == 0) {
            Toast.makeText(requireContext(), "Data lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        int[] toonIDs = new int[urls.length];
        int[] episodeIDs = new int[urls.length];
        String[] toonTypes = new String[urls.length];

        if(WtwtLinkParser.tryPopulateInfo(requireActivity().getApplicationContext(), urls, toonIDs, episodeIDs, toonTypes)) {
            for (int i = 0; i < urls.length; ++i) {
                adapter.addNewItem(
                        String.format(requireContext().getString(R.string.item_placeholder), i),
                        toonTypes[i],
                        toonIDs[i],
                        episodeIDs[i],
                        0,
                        false
                );
            }
        }
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