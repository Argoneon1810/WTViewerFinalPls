package ark.noah.wtviewerfinalpls.ui.edit;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.databinding.FragmentEditBinding;
import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class FragmentEdit extends Fragment {
    EditViewModel viewModel;
    FragmentEditBinding binding;

    ColorDrawable redTransparent;

    ToonsContainer currentContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ViewModelProvider(this).get(EditViewModel.class);
        binding = FragmentEditBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        redTransparent = new ColorDrawable(requireContext().getColor(R.color.red_transparent));

        binding.etxtEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentContainer.toonName = s.toString();
            }
        });

        binding.btnEditSun.setOnClickListener(v -> {
            if(setButtonMatchingStateAndReturn(v, !currentContainer.releasesOnSun()))
                currentContainer.enableFlagSun();
            else
                currentContainer.disableFlagSun();
        });
        binding.btnEditMon.setOnClickListener(v -> {
            if(setButtonMatchingStateAndReturn(v, !currentContainer.releasesOnMon()))
                currentContainer.enableFlagMon();
            else
                currentContainer.disableFlagMon();
        });
        binding.btnEditTue.setOnClickListener(v -> {
            if(setButtonMatchingStateAndReturn(v, !currentContainer.releasesOnTue()))
                currentContainer.enableFlagTue();
            else
                currentContainer.disableFlagTue();
        });
        binding.btnEditWed.setOnClickListener(v -> {
            if(setButtonMatchingStateAndReturn(v, !currentContainer.releasesOnWed()))
                currentContainer.enableFlagWed();
            else
                currentContainer.disableFlagWed();
        });
        binding.btnEditThu.setOnClickListener(v -> {
            if(setButtonMatchingStateAndReturn(v, !currentContainer.releasesOnThu()))
                currentContainer.enableFlagThu();
            else
                currentContainer.disableFlagThursday();
        });
        binding.btnEditFri.setOnClickListener(v -> {
            if(setButtonMatchingStateAndReturn(v, !currentContainer.releasesOnFri()))
                currentContainer.enableFlagFri();
            else
                currentContainer.disableFlagFriday();
        });
        binding.btnEditSat.setOnClickListener(v -> {
            if(setButtonMatchingStateAndReturn(v, !currentContainer.releasesOnSat()))
                currentContainer.enableFlagSat();
            else
                currentContainer.disableFlagSaturday();
        });

        binding.btnEditConfirm.setOnClickListener(v -> {
            if(binding.etxtEditTitle.getText().toString().equals("")) {
                Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_empty_name_typed), Toast.LENGTH_SHORT).show();
                return;
            }

            FragmentEditDirections.ActionFragmentEditToNavMain action =
                    FragmentEditDirections.actionFragmentEditToNavMain();
            action.setToon(new ToonsContainer[]{currentContainer});
            action.setWasEdit(true);
            Navigation.findNavController(view).navigate(action);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentEditArgs args = FragmentEditArgs.fromBundle(getArguments());
        currentContainer = args.getToon();

        binding.etxtEditTitle.setText(currentContainer.toonName);

        setButtonMatchingStateAndReturn(binding.btnEditSun, currentContainer.releasesOnSun());
        setButtonMatchingStateAndReturn(binding.btnEditMon, currentContainer.releasesOnMon());
        setButtonMatchingStateAndReturn(binding.btnEditTue, currentContainer.releasesOnTue());
        setButtonMatchingStateAndReturn(binding.btnEditWed, currentContainer.releasesOnWed());
        setButtonMatchingStateAndReturn(binding.btnEditThu, currentContainer.releasesOnThu());
        setButtonMatchingStateAndReturn(binding.btnEditFri, currentContainer.releasesOnFri());
        setButtonMatchingStateAndReturn(binding.btnEditSat, currentContainer.releasesOnSat());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean setButtonMatchingStateAndReturn(View view, boolean state) {
        if(state) view.setForeground(redTransparent); else view.setForeground(null);
        return state;
    }
}