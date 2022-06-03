package ark.noah.wtviewerfinalpls.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Objects;

import ark.noah.wtviewerfinalpls.DBHelper;
import ark.noah.wtviewerfinalpls.EntryPointGetter;
import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.databinding.FragmentMainBinding;

public class FragmentMain extends Fragment {

    private MainViewModel homeViewModel;
    private FragmentMainBinding binding;
    private DBHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        dbHelper = new DBHelper(getContext());

        ArrayList<ToonsContainer> mList = dbHelper.getAllToons();

        ToonsAdapter adapter = new ToonsAdapter(mList);

        adapter.setOnItemClickListener((v, position) -> {
            if(EntryPointGetter.getLastValidEntryPoint().equals("")) {
                Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_link_not_ready), Toast.LENGTH_SHORT).show();
                return;
            }
            FragmentMainDirections.ActionNavMainToNavGallery action = FragmentMainDirections.actionNavMainToNavGallery(adapter.getItem(position));
            Navigation.findNavController(view).navigate(action);
        });

        binding.recMain.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ToonsContainer[] containers = FragmentMainArgs.fromBundle(getArguments()).getToon();

        if(containers == null) return;
        if(containers.length == 0) return;

        ToonsAdapter adapter = ((ToonsAdapter) Objects.requireNonNull(binding.recMain.getAdapter()));
        int lastID = dbHelper.getToonIDAtLastPosition();
        for(int i = 1; i <= containers.length; ++i) {
            containers[i-1].dbID = lastID + i;
            dbHelper.insertToonContent(containers[i-1]);
            adapter.addItem(containers[i-1]);
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
        ((MainActivity) requireActivity()).resumedFromMainFragment();
    }
}