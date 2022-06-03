package ark.noah.wtviewerfinalpls.ui.main;


import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import ark.noah.wtviewerfinalpls.R;

public class ToonsAdapter extends RecyclerView.Adapter<ToonsAdapter.ViewHolder> {
    private ArrayList<ToonsContainer> mData;

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView ToonName;
        TextView ReleaseWeekday;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_rec_main);
            cardView.setOnClickListener(v -> {
                mData.get(getAdapterPosition());
            });
            ToonName = itemView.findViewById(R.id.tv_toons_title);
            ReleaseWeekday = itemView.findViewById(R.id.tv_toons_release);
        }
    }

    public ToonsAdapter(ArrayList<ToonsContainer> list) {
        mData = list;
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

    public ArrayList<ToonsContainer> getmData() {
        return mData;
    }

    public void AddItems(ToonsContainer[] containers) {
        mData.addAll(Arrays.asList(containers.clone()));
    }
}
