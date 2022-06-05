package ark.noah.wtviewerfinalpls.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

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

        dbHelper = new DBHelper(requireContext());

        ArrayList<ToonsContainer> mList = dbHelper.getAllToons();
        ToonsAdapter adapter = new ToonsAdapter(mList);
        binding.recMain.setAdapter(adapter);

        binding.recMain.addOnItemTouchListener(new RecyclerTouchListener(requireContext().getApplicationContext(), binding.recMain, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(EntryPointGetter.getLastValidEntryPoint().equals("")) {
                    Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_link_not_ready), Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentMainDirections.ActionNavMainToFragmentEpisodes action = FragmentMainDirections.actionNavMainToFragmentEpisodes(adapter.getItemAtPosition(position));
                Navigation.findNavController(view).navigate(action);
            }

            @Override
            public void onLongClick(View view, int position) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), view, Gravity.END);

                popupMenu.getMenuInflater().inflate(R.menu.main_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    ToonsContainer currentItem = mList.get(position);
                    if(menuItem.getTitle().equals(requireContext().getText(R.string.menu_delete))) {
                        dbHelper.deleteToonContent(adapter.deleteItemAndGetIDOFDeleted(currentItem));
                    } else if (menuItem.getTitle().equals(requireContext().getText(R.string.menu_edit))) {
                        FragmentMainDirections.ActionNavMainToFragmentEdit action =
                                FragmentMainDirections.actionNavMainToFragmentEdit(adapter.getItemAtPosition(position));
                        Navigation.findNavController(view).navigate(action);
                    }
                    return false;
                });

                popupMenu.show();
            }
        }));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(getArguments() == null) return;

        FragmentMainArgs args = FragmentMainArgs.fromBundle(getArguments());
        ToonsContainer[] containers = args.getToon();
        boolean wasEdit = args.getWasEdit();

        ToonsAdapter adapter = ((ToonsAdapter) Objects.requireNonNull(binding.recMain.getAdapter()));

        if(containers == null) return;
        if(containers.length == 0) return;

        if(wasEdit) {
            adapter.updateItem(containers[0]);
            dbHelper.editToonContent(containers[0]);
        } else {
            int lastID = dbHelper.getToonIDAtLastPosition();
            for(int i = 1; i <= containers.length; ++i) {
                containers[i-1].dbID = lastID + i;
                dbHelper.insertToonContent(containers[i-1]);
                adapter.addItem(containers[i-1]);
            }
        }

        getArguments().clear();
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}