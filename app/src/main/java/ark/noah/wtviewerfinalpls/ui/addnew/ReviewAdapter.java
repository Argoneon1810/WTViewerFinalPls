package ark.noah.wtviewerfinalpls.ui.addnew;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.R;
import ark.noah.wtviewerfinalpls.ui.main.ToonsContainer;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<ToonsContainer> mData;
    Drawable foregroundColor;

    class ViewHolder extends RecyclerView.ViewHolder {
        ToonsContainer currentBoundContainer;

        EditText ToonName;
        TextView ToonID;
        TextView EpisodeID;
        TextView ToonType;
        MaterialButton Mon, Tue, Wed, Thu, Fri, Sat, Sun;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ToonName = itemView.findViewById(R.id.etxt_title);
            ToonName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    currentBoundContainer.toonName = s.toString();
                }
            });
            ToonID = itemView.findViewById(R.id.tv_toonid);
            EpisodeID = itemView.findViewById(R.id.tv_epiid);
            ToonID = itemView.findViewById(R.id.tv_toonid);
            ToonType = itemView.findViewById(R.id.tv_toontype);

            Sun = itemView.findViewById(R.id.btn_addnew_sun);
            Sun.setOnClickListener(v -> {
                if(Sun.getForeground() == null) {
                    currentBoundContainer.enableFlagSunday();
                    v.setForeground(foregroundColor);
                } else {
                    currentBoundContainer.disableFlagSunday();
                    v.setForeground(null);
                }
            });
            Mon = itemView.findViewById(R.id.btn_addnew_mon);
            Mon.setOnClickListener(v -> {
                if(Mon.getForeground() == null) {
                    currentBoundContainer.enableFlagMonday();
                    v.setForeground(foregroundColor);
                } else {
                    currentBoundContainer.disableFlagMonday();
                    v.setForeground(null);
                }
            });
            Tue = itemView.findViewById(R.id.btn_addnew_tue);
            Tue.setOnClickListener(v -> {
                if(Tue.getForeground() == null) {
                    currentBoundContainer.enableFlagTuesday();
                    v.setForeground(foregroundColor);
                } else {
                    currentBoundContainer.disableFlagTuesday();
                    v.setForeground(null);
                }
            });
            Wed = itemView.findViewById(R.id.btn_addnew_wed);
            Wed.setOnClickListener(v -> {
                if(Wed.getForeground() == null) {
                    currentBoundContainer.enableFlagWednesday();
                    v.setForeground(foregroundColor);
                } else {
                    currentBoundContainer.disableFlagWednesday();
                    v.setForeground(null);
                }
            });
            Thu = itemView.findViewById(R.id.btn_addnew_thu);
            Thu.setOnClickListener(v -> {
                if(Thu.getForeground() == null) {
                    currentBoundContainer.enableFlagThursday();
                    v.setForeground(foregroundColor);
                } else {
                    currentBoundContainer.disableFlagThursday();
                    v.setForeground(null);
                }
            });
            Fri = itemView.findViewById(R.id.btn_addnew_fri);
            Fri.setOnClickListener(v -> {
                if(Fri.getForeground() == null) {
                    currentBoundContainer.enableFlagFriday();
                    v.setForeground(foregroundColor);
                } else {
                    currentBoundContainer.disableFlagFriday();
                    v.setForeground(null);
                }
            });
            Sat = itemView.findViewById(R.id.btn_addnew_sat);
            Sat.setOnClickListener(v -> {
                if(Sat.getForeground() == null) {
                    currentBoundContainer.enableFlagSaturday();
                    v.setForeground(foregroundColor);
                } else {
                    currentBoundContainer.disableFlagSaturday();
                    v.setForeground(null);
                }
            });
        }
    }

    public ReviewAdapter(ArrayList<ToonsContainer> list) { mData = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_item_addnew_review, parent, false);

        foregroundColor = new ColorDrawable(context.getColor(R.color.red_transparent));
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.currentBoundContainer = mData.get(position);

        holder.ToonName.setText(holder.currentBoundContainer.toonName);
        holder.ToonID.setText(String.valueOf(holder.currentBoundContainer.toonID));
        holder.EpisodeID.setText(String.valueOf(holder.currentBoundContainer.episodeID));
        holder.ToonType.setText(holder.currentBoundContainer.toonType);

        if(holder.currentBoundContainer.releasesOnSunday())    holder.Sun.setForeground(foregroundColor); else holder.Sun.setForeground(null);
        if(holder.currentBoundContainer.releasesOnMonday())    holder.Mon.setForeground(foregroundColor); else holder.Mon.setForeground(null);
        if(holder.currentBoundContainer.releasesOnTuesday())   holder.Tue.setForeground(foregroundColor); else holder.Tue.setForeground(null);
        if(holder.currentBoundContainer.releasesOnWednesday()) holder.Wed.setForeground(foregroundColor); else holder.Wed.setForeground(null);
        if(holder.currentBoundContainer.releasesOnThursday())  holder.Thu.setForeground(foregroundColor); else holder.Thu.setForeground(null);
        if(holder.currentBoundContainer.releasesOnFriday())    holder.Fri.setForeground(foregroundColor); else holder.Fri.setForeground(null);
        if(holder.currentBoundContainer.releasesOnSaturday())  holder.Sat.setForeground(foregroundColor); else holder.Sat.setForeground(null);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public ArrayList<ToonsContainer> getmData() { return mData; }

    public void addNewItem(String title, int releaseWeekday, int toonID, int episodeID, String toonType) {
        ToonsContainer container = new ToonsContainer();
        container.toonName = title;
        container.releaseWeekdays = releaseWeekday;
        container.toonID = toonID;
        container.episodeID = episodeID;
        container.toonType = toonType;
        mData.add(container);
        notifyItemInserted(mData.size()-1);
    }
}
