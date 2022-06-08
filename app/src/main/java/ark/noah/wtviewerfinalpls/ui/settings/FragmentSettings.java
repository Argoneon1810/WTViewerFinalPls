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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activityContext,
                R.layout.spinner_item, EntryPointGetter.EntryPointParser.validEntryRootList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSettingUrl.setAdapter(adapter);
        String lastEntryRoot = sharedPreferences.getString(activityContext.getString(R.string.shared_pref_entry_key), "");
        if(lastEntryRoot.equals(""))
            binding.spinnerSettingUrl.setSelection(6);
        else {
            for (int i = 0; i < adapter.getCount(); i++) {
                if(adapter.getItem(i).equals(lastEntryRoot)) {
                    binding.spinnerSettingUrl.setSelection(i);
                }
            }
        }
        binding.spinnerSettingUrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("", "Value Before Setting");
                Log.i("", "entry point root: " + EntryPointGetter.EntryPointParser.getCurrentEntryRoot());
                Log.i("", "entry point root in SharedPreference" + sharedPreferences.getString(activityContext.getString(R.string.shared_pref_entry_key), "FAILED"));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String input = (String) parent.getItemAtPosition(position);
                if (EntryPointGetter.trySetEntryRoot(input)) {
                    Toast.makeText(activityContext.getApplicationContext(), activityContext.getText(R.string.notif_valid_entry_root), Toast.LENGTH_SHORT).show();
                    editor.putString(activityContext.getString(R.string.shared_pref_entry_key), input);
                } else {
                    Toast.makeText(activityContext.getApplicationContext(), activityContext.getText(R.string.notif_valid_entry_root), Toast.LENGTH_SHORT).show();
                    editor.putString(activityContext.getString(R.string.shared_pref_entry_key), EntryPointGetter.EntryPointParser.defaultEntryRoot);
                }
                editor.apply();
                Log.i("", "Value After Setting");
                Log.i("", "entry point root: " + EntryPointGetter.EntryPointParser.getCurrentEntryRoot());
                Log.i("", "entry point root in SharedPreference" + sharedPreferences.getString(activityContext.getString(R.string.shared_pref_entry_key), "FAILED"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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