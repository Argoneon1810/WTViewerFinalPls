package ark.noah.wtviewerfinalpls.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ark.noah.wtviewerfinalpls.DBHelper;
import ark.noah.wtviewerfinalpls.EntryPointGetter;
import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.databinding.FragmentSettingsBinding;

public class FragmentSettings extends Fragment {
    SettingsViewModel settingsViewModel;
    FragmentSettingsBinding binding;
    DBHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        dbHelper = new DBHelper(requireContext());

        Context activityContext = requireContext();
        SharedPreferences sharedPreferences = activityContext.getSharedPreferences(getString(R.string.shared_pref_key),
                Context.MODE_PRIVATE);

        binding.etxtSettingEntryUrl.setText(sharedPreferences.getString(activityContext.getString(R.string.shared_pref_entry_key), ""));

        binding.btnSettingUrlApply.setOnClickListener(v -> {
            String input = binding.etxtSettingEntryUrl.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(EntryPointGetter.trySetEntryRoot(input)) {
                Toast.makeText(activityContext.getApplicationContext(), activityContext.getText(R.string.notif_valid_entry_root), Toast.LENGTH_SHORT).show();
                editor.putString(activityContext.getString(R.string.shared_pref_entry_key), input);
            } else {
                Toast.makeText(activityContext.getApplicationContext(), activityContext.getText(R.string.notif_valid_entry_root), Toast.LENGTH_SHORT).show();
                editor.putString(activityContext.getString(R.string.shared_pref_entry_key), EntryPointGetter.EntryPointParser.defaultEntryRoot);
            }
            editor.apply();
        });

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
        ((MainActivity) requireActivity()).resumedFromOtherFragment();
    }
}