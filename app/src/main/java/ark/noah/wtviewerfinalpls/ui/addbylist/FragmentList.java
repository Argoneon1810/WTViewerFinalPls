package ark.noah.wtviewerfinalpls.ui.addbylist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.WtwtLinkParser;
import ark.noah.wtviewerfinalpls.databinding.FragmentListBinding;

public class FragmentList extends Fragment {
    private ListViewModel listViewModel;
    private FragmentListBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listViewModel = new ViewModelProvider(this).get(ListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnAddlistConfirm.setOnClickListener(v -> {
            String input = binding.etxtAddlistUrl.getText().toString();
            if(input.equals("")) {
                Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_empty_link_typed), Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i("", "initial user input was: " + input);
            ArrayList<String> urls = new ArrayList<>();
            while(true) {
                int linebreak = input.indexOf("\n");
                if(linebreak == -1) {
                    Log.i("", "current user input substring did not have a linebreak");
                    if(WtwtLinkParser.isLinkValid(input)) {
                        Log.i("", "the text was a valid link");
                        urls.add(input);
                        Log.i("", "number of item in the list are: " + urls.size());
                    }
                    break;
                }
                Log.i("", "current user input substring had a linebreak");

                String pre_linebreak = input.substring(0, linebreak);
                Log.i("", "text prior to linebreak is: " + pre_linebreak);
                if(WtwtLinkParser.isLinkValid(pre_linebreak)) {
                    Log.i("", "the text was a valid link");
                    urls.add(pre_linebreak);
                    Log.i("", "number of item in the list are: " + urls.size());
                }

                input = input.substring(linebreak + 1);
                Log.i("","next user input substring is: " + input);
            }
            Log.i("", "final url list size is " + urls.size());

            FragmentListDirections.ActionListFragmentToFragmentAddNew action = FragmentListDirections.actionListFragmentToFragmentAddNew(urls.toArray(new String[0]));
            Navigation.findNavController(view).navigate(action);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}