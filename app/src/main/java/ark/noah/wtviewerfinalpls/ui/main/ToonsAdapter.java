package ark.noah.wtviewerfinalpls.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import ark.noah.wtviewerfinalpls.R;

public class ToonsAdapter extends RecyclerView.Adapter<ToonsAdapter.ViewHolder> {
    private ArrayList<ToonsContainer> mData;
    private SortManager sortManager;

    private boolean bResorted = true;

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView ToonName;
        TextView ReleaseWeekday;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_rec_main);
            ToonName = itemView.findViewById(R.id.tv_toons_title);
            ReleaseWeekday = itemView.findViewById(R.id.tv_toons_release);
        }
    }

    enum SortType {
        FIFO,
        ALPHABET,
        RELEASEDAY,
    }
    enum SortDirection {
        ASCENDING,
        DESCENDING,
    }

    class SortManager {
        private SortType sortType = SortType.FIFO;
        private SortDirection sortDirection = SortDirection.ASCENDING;

        public boolean isSortedByAlphabet() { return sortType == SortType.ALPHABET; }
        public boolean isSortedByFIFO() { return sortType == SortType.FIFO; }
        public boolean isSortedByReleaseDay() { return sortType == SortType.RELEASEDAY; }
        public void setSortType(SortType sortType) { this.sortType = sortType; }

        public boolean isAscending() { return sortDirection == SortDirection.ASCENDING; }
        public void setSortDirection(SortDirection dir) { sortDirection = dir; }
    }

    public ToonsAdapter(ArrayList<ToonsContainer> list) {
        mData = list;
        sortManager = new SortManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_item_toons, parent, false);
        return new ToonsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToonsContainer toonsContainer = mData.get(position);

        Calendar calendar = Calendar.getInstance();
        Integer[] releaseWeekdays = toonsContainer.getAllReleaseDaysInArray();
        boolean bgColorChanged = false;
        for (Integer day : releaseWeekdays) {
            if (day == calendar.get(Calendar.DAY_OF_WEEK)) {
                TypedValue typedValue = new TypedValue();
                holder.itemView.getContext().getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
                Color color = Color.valueOf(typedValue.data - 0xAA000000);
                holder.cardView.setBackgroundColor(color.toArgb());
                bgColorChanged = true;
                break;
            }
        }
        if (!bgColorChanged) {
            holder.cardView.setBackground(null);
        }

        holder.ToonName.setText(toonsContainer.toonName);
        holder.ReleaseWeekday.setText(toonsContainer.getAllReleaseDaysInString());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ToonsContainer getItemAtPosition(int position) {
        return mData.get(position);
    }

    public int getIndexOfItem(ToonsContainer container) {
        return mData.indexOf(container);
    }

    public int deleteItemAndGetIDOFDeleted(ToonsContainer target) {
        int id = target.dbID;
        int posToUpdate = mData.indexOf(target);
        mData.remove(target);
        notifyItemRemoved(posToUpdate);
        return id;
    }

    public void updateItem(ToonsContainer updated) {
        for (int i = 0; i < mData.size(); ++i) {
            if(updated.dbID == mData.get(i).dbID) {
                mData.remove(i);
                mData.add(i, updated);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public boolean isNullOrEmpty() {
        if(mData == null) return true;
        return mData.isEmpty();
    }

    public ArrayList<ToonsContainer> getmData() {
        return mData;
    }

    public void addItem(ToonsContainer container) {
        mData.add(container);
        notifyItemInserted(mData.size()-1);
    }
    public void addItems(ToonsContainer[] containers) {
        int startIndex = mData.size();
        mData.addAll(Arrays.asList(containers.clone()));
        notifyItemRangeInserted(startIndex, mData.size()-1);
    }

    public SortManager getSortManager() { return sortManager; }
    public void sortData(Comparator<ToonsContainer> toonsContainerComparator) {
        mData.sort(toonsContainerComparator);
        bResorted = true;
        notifyDataSetChanged();     //list after sort might be completely different from list before sort, so no choice.
    }

    public boolean isResorted() {
        if(bResorted) {
            bResorted = false;
            return true;
        }
        return false;
    }
}
