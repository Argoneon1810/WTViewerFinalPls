package ark.noah.wtviewerfinalpls.ui.episodes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.ui.main.ToonsAdapter;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    ArrayList<EpisodesContainer> mData;
    ToonsAdapter.OnItemClickListener mOnItemClickListener;

    EpisodesAdapter(ArrayList<EpisodesContainer> containers) {
        mData = containers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_episode_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EpisodesContainer mContainer = mData.get(position);

        holder.Number.setText(String.valueOf(mContainer.number));
        holder.Title.setText(mContainer.title);
        holder.Date.setText(mContainer.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.onClick(v, position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void setOnItemClickListener(ToonsAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public EpisodesContainer getItem(int position) {
        return mData.get(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAllData(ArrayList<EpisodesContainer> itemList) {
        mData = itemList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Number, Title, Date;

        ViewHolder(View itemView) {
            super(itemView);
            Number = itemView.findViewById(R.id.tv_episodeNumber);
            Title = itemView.findViewById(R.id.tv_episodeTitle);
            Date = itemView.findViewById(R.id.tv_episodeDate);
        }
    }
}
