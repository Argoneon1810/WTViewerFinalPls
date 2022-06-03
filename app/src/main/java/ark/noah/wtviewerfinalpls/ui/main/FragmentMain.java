package ark.noah.wtviewerfinalpls.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.ObjectSerializer;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.databinding.FragmentMainBinding;

public class FragmentMain extends Fragment {

    private MainViewModel homeViewModel;
    private FragmentMainBinding binding;
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //retrieve data from sharedpref if any
        ArrayList<ToonsContainer> mList = new ArrayList<>();
        sharedPreferences =
                Objects.requireNonNull(getActivity())
                        .getSharedPreferences(
                                Objects.requireNonNull(getContext()).getString(R.string.shared_pref_key),
                                Context.MODE_PRIVATE
                        );
        if(sharedPreferences.contains(getContext().getString(R.string.shared_pref_toonslist_key))) {
            try {
                Object deserializedObject = ObjectSerializer.deserialize(
                        sharedPreferences.getString(
                                getContext().getString(R.string.shared_pref_toonslist_key),
                                ObjectSerializer.serialize(new ArrayList<ToonsContainer>())
                        ));
                if(deserializedObject instanceof ArrayList<?>)
                    for(Object elem : (ArrayList<?>)deserializedObject)
                        if(elem instanceof ToonsContainer)
                            mList.add((ToonsContainer) elem);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        ToonsAdapter adapter = new ToonsAdapter(mList);

        adapter.setOnItemClickListener((v, position) -> {
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

        ((ToonsAdapter) Objects.requireNonNull(binding.recMain.getAdapter())).AddItems(containers);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //serialize and save to sharedpref the data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString(
                    requireContext().getString(R.string.shared_pref_toonslist_key),
                    ObjectSerializer.serialize(((ToonsAdapter) Objects.requireNonNull(binding.recMain.getAdapter())).getmData())
            );
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).resumedFromMainFragment();
    }
}