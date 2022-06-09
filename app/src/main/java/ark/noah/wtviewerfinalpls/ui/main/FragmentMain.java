package ark.noah.wtviewerfinalpls.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

import ark.noah.wtviewerfinalpls.DBHelper;
import ark.noah.wtviewerfinalpls.EntryPointGetter;
import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.WtwtLinkParser;
import ark.noah.wtviewerfinalpls.databinding.FragmentMainBinding;

public class FragmentMain extends Fragment {

    private MainViewModel homeViewModel;
    private FragmentMainBinding binding;
    private DBHelper dbHelper;

    SharedPreferences sharedPreferences;

    Drawable ic_up, ic_down;
    BlendModeColorFilter blackColorFilter;

    private static final int ASCENDING = 0;
    private static final int DESCENDING = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        dbHelper = new DBHelper(requireContext());

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.shared_pref_key),
                Context.MODE_PRIVATE);

        prepareIcon();
        prepareRecycler();

        sortAdapterBySharedPreference();

        setHasOptionsMenu(true);

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
            binding.recMain.scrollToPosition(adapter.getIndexOfItem(containers[0]));
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        if(menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
            for (int i = 0; i < menu.size(); ++i) {
                menu.getItem(i).setIconTintBlendMode(BlendMode.SRC_ATOP);

                SpannableString spannableString = new SpannableString(menu.getItem(i).getTitle());
                spannableString.setSpan(new ForegroundColorSpan(blackColorFilter.getColor()), 0, spannableString.length(), 0);
                menu.getItem(i).setTitle(spannableString);

                Menu subMenu = menu.getItem(i).getSubMenu();
                for(int j = 0; j < subMenu.size(); ++j) {
                    subMenu.getItem(j).setIconTintBlendMode(BlendMode.SRC_ATOP);

                    SpannableString spannableString2 = new SpannableString(subMenu.getItem(j).getTitle());
                    spannableString2.setSpan(new ForegroundColorSpan(blackColorFilter.getColor()), 0, spannableString2.length(), 0);
                    subMenu.getItem(j).setTitle(spannableString2);
                }
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        ToonsAdapter adapter = (ToonsAdapter) binding.recMain.getAdapter();
        if(adapter == null) return;

        if(!adapter.isResorted()) return;

        ToonsAdapter.SortManager sortManager = adapter.getSortManager();
        Menu nestedMenu = menu.getItem(0).getSubMenu();
        for (int i = 0; i < nestedMenu.size(); ++i) {
            MenuItem subItem = nestedMenu.getItem(i);
            if(subItem.getItemId() == R.id.action_sort_fifo)
                if(sortManager.isSortedByFIFO())
                    if(sortManager.isAscending()) subItem.setIcon(ic_down); else subItem.setIcon(ic_up);
                else subItem.setIcon(null);
            else if(subItem.getItemId() == R.id.action_sort_alphabet)
                if(sortManager.isSortedByAlphabet())
                    if(sortManager.isAscending()) subItem.setIcon(ic_down); else subItem.setIcon(ic_up);
                else subItem.setIcon(null);
            else if(subItem.getItemId() == R.id.action_sort_release)
                if(sortManager.isSortedByReleaseDay())
                    if(sortManager.isAscending()) subItem.setIcon(ic_down); else subItem.setIcon(ic_up);
                else subItem.setIcon(null);
            else subItem.setIcon(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        ToonsAdapter adapter = (ToonsAdapter) binding.recMain.getAdapter();

        if(adapter == null) return super.onOptionsItemSelected(menuItem);

        ToonsAdapter.SortManager sortManager = adapter.getSortManager();

        if(menuItem.getItemId() == R.id.action_sort_alphabet) {
            sortByAlphabet(adapter, sortManager);
            return true;
        } else if(menuItem.getItemId() == R.id.action_sort_release) {
            sortByRelease(adapter, sortManager);
            return true;
        } else if(menuItem.getItemId() == R.id.action_sort_fifo) {
            sortByFIFO(adapter, sortManager);
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
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


    private void sortByAlphabet(ToonsAdapter adapter, ToonsAdapter.SortManager sortManager, boolean reverse) {
        if(!reverse) sortByAlphabet(adapter, sortManager);

        if(sortManager.isAscending()) sortManager.setSortDirection(ToonsAdapter.SortDirection.DESCENDING);
        else sortManager.setSortDirection(ToonsAdapter.SortDirection.ASCENDING);

        sortByAlphabet(adapter, sortManager);
    }
    private void sortByAlphabet(ToonsAdapter adapter, ToonsAdapter.SortManager sortManager) {
        boolean isAlphabet = sortManager.isSortedByAlphabet();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_sort_type_key), getString(R.string.action_sort_alphabet));
        if (isAlphabet && sortManager.isAscending()) {
            sortManager.setSortDirection(ToonsAdapter.SortDirection.DESCENDING);
            adapter.sortData(Comparator.comparing((ToonsContainer t) -> t.toonName.toLowerCase(Locale.ROOT)).reversed());
            editor.putInt(getString(R.string.shared_pref_sort_direction_key), DESCENDING);
        } else {
            if (!isAlphabet) sortManager.setSortType(ToonsAdapter.SortType.ALPHABET);
            sortManager.setSortDirection(ToonsAdapter.SortDirection.ASCENDING);
            adapter.sortData(Comparator.comparing(t -> t.toonName.toLowerCase(Locale.ROOT)));
            editor.putInt(getString(R.string.shared_pref_sort_direction_key), ASCENDING);
        }
        editor.apply();
    }

    private void sortByRelease(ToonsAdapter adapter, ToonsAdapter.SortManager sortManager, boolean reverse) {
        if(!reverse) sortByRelease(adapter, sortManager);

        if(sortManager.isAscending()) sortManager.setSortDirection(ToonsAdapter.SortDirection.DESCENDING);
        else sortManager.setSortDirection(ToonsAdapter.SortDirection.ASCENDING);

        sortByRelease(adapter, sortManager);
    }
    private void sortByRelease(ToonsAdapter adapter, ToonsAdapter.SortManager sortManager) {
        boolean isRelease = sortManager.isSortedByReleaseDay();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_sort_type_key), getString(R.string.action_sort_release));
        if (isRelease && sortManager.isAscending()) {
            sortManager.setSortDirection(ToonsAdapter.SortDirection.DESCENDING);
            editor.putInt(getString(R.string.shared_pref_sort_direction_key), DESCENDING);
            adapter.sortData(Comparator.comparingInt(ToonsContainer::getFirstReleaseDay).reversed());
        } else {
            if (!isRelease) sortManager.setSortType(ToonsAdapter.SortType.RELEASEDAY);
            sortManager.setSortDirection(ToonsAdapter.SortDirection.ASCENDING);
            editor.putInt(getString(R.string.shared_pref_sort_direction_key), ASCENDING);
            adapter.sortData(Comparator.comparingInt(ToonsContainer::getFirstReleaseDay));
        }
        editor.apply();
    }

    private void sortByFIFO(ToonsAdapter adapter, ToonsAdapter.SortManager sortManager, boolean reverse) {
        if(!reverse) sortByFIFO(adapter, sortManager);

        if(sortManager.isAscending()) sortManager.setSortDirection(ToonsAdapter.SortDirection.DESCENDING);
        else sortManager.setSortDirection(ToonsAdapter.SortDirection.ASCENDING);

        sortByFIFO(adapter, sortManager);
    }
    private void sortByFIFO(ToonsAdapter adapter, ToonsAdapter.SortManager sortManager) {
        boolean isFIFO = sortManager.isSortedByFIFO();
        Log.i("", "dir value in sharedpref before setting: " + sharedPreferences.getInt(getString(R.string.shared_pref_sort_direction_key), -1));
        Log.i("", "type value in sharedpref before setting: " + sharedPreferences.getString(getString(R.string.shared_pref_sort_type_key), "failed"));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_sort_type_key), getString(R.string.action_sort_fifo));
        if (isFIFO && sortManager.isAscending()) {
            sortManager.setSortDirection(ToonsAdapter.SortDirection.DESCENDING);
            editor.putInt(getString(R.string.shared_pref_sort_direction_key), DESCENDING);
            adapter.sortData(Comparator.comparing((ToonsContainer t) -> t.dbID).reversed());
        } else {
            if (!isFIFO) sortManager.setSortType(ToonsAdapter.SortType.FIFO);
            sortManager.setSortDirection(ToonsAdapter.SortDirection.ASCENDING);
            editor.putInt(getString(R.string.shared_pref_sort_direction_key), ASCENDING);
            adapter.sortData(Comparator.comparing(t -> t.dbID));
        }
        editor.apply();
        Log.i("", "dir value in sharedpref after setting: " + sharedPreferences.getInt(getString(R.string.shared_pref_sort_direction_key), -1));
        Log.i("", "type value in sharedpref after setting: " + sharedPreferences.getString(getString(R.string.shared_pref_sort_type_key), "failed"));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void prepareIcon() {
        ic_up = requireContext().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24).mutate();
        ic_down = requireContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24).mutate();

        TypedValue value = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorOnPrimaryReversed, value, true);
        @ColorInt int fontColorInt = value.data;
        blackColorFilter = new BlendModeColorFilter(fontColorInt, BlendMode.SRC_ATOP);

        ic_up.setColorFilter(blackColorFilter);
        ic_down.setColorFilter(blackColorFilter);
    }

    private void prepareRecycler() {
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
                if(!WtwtLinkParser.isWebToon(adapter.getItemAtPosition(position).toonType)) {
                    Toast.makeText(requireContext().getApplicationContext(), requireContext().getText(R.string.notif_is_not_webtoon), Toast.LENGTH_LONG).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WtwtLinkParser.rebuildLinkEpisodes(adapter.getItemAtPosition(position))));
                    startActivity(browserIntent);
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
    }

    private void sortAdapterBySharedPreference() {
        String sorttype = sharedPreferences.getString(getString(R.string.shared_pref_sort_type_key), getString(R.string.action_sort_fifo));
        int sortdir = sharedPreferences.getInt(getString(R.string.shared_pref_sort_direction_key), ASCENDING);

        ToonsAdapter adapter = (ToonsAdapter)binding.recMain.getAdapter();
        if(adapter == null) return;

        ToonsAdapter.SortManager sortManager = adapter.getSortManager();

        if(sortdir == DESCENDING) sortManager.setSortDirection(ToonsAdapter.SortDirection.DESCENDING);
        else sortManager.setSortDirection(ToonsAdapter.SortDirection.ASCENDING);

        Log.i("", sorttype);
        if(sorttype.equals(getString(R.string.action_sort_alphabet))) {
            sortManager.setSortType(ToonsAdapter.SortType.ALPHABET);
            sortByAlphabet(adapter, sortManager, true);
        }
        else if(sorttype.equals(getString(R.string.action_sort_release))) {
            sortManager.setSortType(ToonsAdapter.SortType.RELEASEDAY);
            sortByRelease(adapter, sortManager, true);
        }
        /*if(sorttype.equals(getString(R.string.action_sort_fifo)))*/ else {
            sortManager.setSortType(ToonsAdapter.SortType.FIFO);
            sortByFIFO(adapter, sortManager, true);
        }
    }
}