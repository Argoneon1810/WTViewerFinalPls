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

import ark.noah.wtviewerfinalpls.MainActivity;
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

        ((MainActivity)requireActivity()).hideAllFABs();

        binding.btnAddlistConfirm.setOnClickListener(v -> {
            String input = binding.etxtAddlistUrl.getText().toString();
            if(input.equals("")) {
                Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_empty_link_typed), Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> urls = new ArrayList<>();
            while(true) {
                int linebreak = input.indexOf("\n");
                if(linebreak == -1) {
                    if(WtwtLinkParser.isLinkValid(input)) {
                        urls.add(input);
                    }
                    break;
                }

                String pre_linebreak = input.substring(0, linebreak);
                if(WtwtLinkParser.isLinkValid(pre_linebreak)) {
                    urls.add(pre_linebreak);
                }

                input = input.substring(linebreak + 1);
            }

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

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).resumedFromOtherFragment();
    }
}