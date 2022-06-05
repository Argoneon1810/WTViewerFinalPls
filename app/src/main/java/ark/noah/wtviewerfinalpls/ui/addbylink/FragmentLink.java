package ark.noah.wtviewerfinalpls.ui.addbylink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.WtwtLinkParser;
import ark.noah.wtviewerfinalpls.databinding.FragmentLinkBinding;

public class FragmentLink extends Fragment {
    private LinkViewModel linkViewModel;
    private FragmentLinkBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        linkViewModel =
                new ViewModelProvider(this).get(LinkViewModel.class);

        binding = FragmentLinkBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnAddlinkConfirm.setOnClickListener(v -> {
            String input = binding.etxtAddlinkUrl.getText().toString();

            if(input.equals("")) {
                Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_empty_link_typed), Toast.LENGTH_SHORT).show();
                return;
            }
            if(!WtwtLinkParser.isLinkValid(input)) {
                Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_invalid_link_typed), Toast.LENGTH_SHORT).show();
                return;
            }

            FragmentLinkDirections.ActionLinkFragmentToFragmentAddNew action =
                    FragmentLinkDirections.actionLinkFragmentToFragmentAddNew(new String[]{input});
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