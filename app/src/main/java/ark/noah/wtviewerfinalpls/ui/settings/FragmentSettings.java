package ark.noah.wtviewerfinalpls.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import ark.noah.wtviewerfinalpls.DBDumpParser;
import ark.noah.wtviewerfinalpls.DBHelper;
import ark.noah.wtviewerfinalpls.EntryPointGetter;
import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.databinding.FragmentSettingsBinding;
import ark.noah.wtviewerfinalpls.ui.main.ToonsAdapter;
import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class FragmentSettings extends Fragment {

    SettingsViewModel settingsViewModel;
    FragmentSettingsBinding binding;
    DBHelper dbHelper;

//    StringBuilder dumpBuilder;

    ActivityResultLauncher<Intent> dumpSaveActivityResultLauncher;
    ActivityResultLauncher<Intent> restoreDBActivityResultLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        dbHelper = new DBHelper(requireContext());

        Context activityContext = requireContext();

        dumpSaveActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                if(intent == null) return;

                Uri uri = intent.getData();
                if(uri == null) return;

                try {
                    ParcelFileDescriptor pfd = activityContext.getContentResolver().
                            openFileDescriptor(uri, "w");
                    FileOutputStream fileOutputStream =
                            new FileOutputStream(pfd.getFileDescriptor());
                    StringBuilder dumpBuilder = new StringBuilder();
                    DatabaseUtils.dumpCursor(dbHelper.loadDBCursorToons(), dumpBuilder);
                    fileOutputStream.write(dumpBuilder.toString().getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.close();
                    pfd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(activityContext.getApplicationContext(), activityContext.getText(R.string.notif_backup), Toast.LENGTH_SHORT).show();
            }
        });

        restoreDBActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode()==Activity.RESULT_OK) {
                Intent intent = result.getData();
                if(intent == null) return;

                Uri uri = intent.getData();
                if(uri == null) return;

                try {
                    InputStream inputStream =
                            activityContext.getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
                    StringBuilder lines = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.append(line);
                    }
                    ArrayList<ToonsContainer> containers = DBDumpParser.restoreDump(lines);
                    for(ToonsContainer restored : containers) {
                        dbHelper.insertToonContent(restored);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(activityContext.getApplicationContext(), activityContext.getText(R.string.notif_restore), Toast.LENGTH_SHORT).show();
            }
        });

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
//                Log.i("", "Value Before Setting");
//                Log.i("", "entry point root: " + EntryPointGetter.EntryPointParser.getCurrentEntryRoot());
//                Log.i("", "entry point root in SharedPreference" + sharedPreferences.getString(activityContext.getString(R.string.shared_pref_entry_key), "FAILED"));
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
//                Log.i("", "Value After Setting");
//                Log.i("", "entry point root: " + EntryPointGetter.EntryPointParser.getCurrentEntryRoot());
//                Log.i("", "entry point root in SharedPreference" + sharedPreferences.getString(activityContext.getString(R.string.shared_pref_entry_key), "FAILED"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnBackup.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, "databaseDump.txt");
//            dumpBuilder = new StringBuilder();
//            DatabaseUtils.dumpCursor(dbHelper.loadDBCursorToons(), dumpBuilder);
//            intent.putExtra(Intent.EXTRA_TEXT, dumpBuilder.toString());

            dumpSaveActivityResultLauncher.launch(intent);
        });

        binding.btnRestore.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");

            restoreDBActivityResultLauncher.launch(intent);
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