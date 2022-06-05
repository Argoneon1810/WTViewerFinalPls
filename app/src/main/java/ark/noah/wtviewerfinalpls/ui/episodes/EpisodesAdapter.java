package ark.noah.wtviewerfinalpls.ui.episodes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.EntryPointGetter;
import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.WtwtLinkParser;
import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    ArrayList<EpisodesContainer> mData;
    OnItemClickListener mOnItemClickListener;
    IDDifferenceCallback callback;

    ColorDrawable redTransparent;
    ToonsContainer currentToon;

    EpisodesAdapter(ArrayList<EpisodesContainer> containers) {
        mData = containers;
    }

    public void setCurrentToon(ToonsContainer currentToon) {
        this.currentToon = currentToon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_episode_item, parent, false);

        redTransparent = new ColorDrawable(context.getColor(R.color.red_transparent));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EpisodesContainer mContainer = mData.get(position);

        holder.Number.setText(String.valueOf(mContainer.number));
        holder.Title.setText(mContainer.title);
        holder.Date.setText(mContainer.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        int currentID = WtwtLinkParser.extractEpisodeID(EntryPointGetter.getLastValidEntryPoint() + mContainer.link);

        //since the user was able to enter episodes list, it means EntryPointGetter.getLastValidEntryPoint is always not null nor empty.
        if(currentToon.episodeID == currentID) holder.Card.setForeground(redTransparent); else holder.Card.setForeground(null);

        holder.itemView.setOnClickListener(v -> {
            if(currentID != -1)
                if(callback != null)
                    callback.onIDDifferent(currentID);
            mOnItemClickListener.onClick(v, position);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
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
        CardView Card;

        ViewHolder(View itemView) {
            super(itemView);
            Number = itemView.findViewById(R.id.tv_episodeNumber);
            Title = itemView.findViewById(R.id.tv_episodeTitle);
            Date = itemView.findViewById(R.id.tv_episodeDate);
            Card = itemView.findViewById(R.id.card_rec_episode);
        }
    }

    interface OnItemClickListener {
        void onClick(View v, int position);
    }

    interface IDDifferenceCallback {
        void onIDDifferent(int id);
    }
}
