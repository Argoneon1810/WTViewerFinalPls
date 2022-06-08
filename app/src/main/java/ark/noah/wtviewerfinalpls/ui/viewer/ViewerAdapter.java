package ark.noah.wtviewerfinalpls.ui.viewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.R;

public class ViewerAdapter extends BaseAdapter {
    ArrayList<ViewerContainer> mData;
    LayoutInflater mLayoutInflater;

    Drawable ic_loading, ic_error, ic_empty;

    @SuppressLint("UseCompatLoadingForDrawables")
    ViewerAdapter(ArrayList<ViewerContainer> list, Context context) {
        mData = list;
        mLayoutInflater = LayoutInflater.from(context);

        ic_loading = context.getDrawable(R.drawable.ic_baseline_downloading_24);
        ic_empty   = context.getDrawable(R.drawable.ic_baseline_device_unknown_24);
        ic_error   = context.getDrawable(R.drawable.ic_baseline_error_outline_24);

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, value, true);
        BlendModeColorFilter greyColorFilter = new BlendModeColorFilter(value.data, BlendMode.SRC_ATOP);
        ic_loading.setColorFilter(greyColorFilter);
        ic_empty.setColorFilter(greyColorFilter);
        ic_error.setColorFilter(greyColorFilter);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if(view == null) {
            view = mLayoutInflater.inflate(R.layout.list_viewer_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if(mData.get(position).loadedImage == null) {
            Glide.with(viewHolder.imageView)
                    .load(mData.get(position).imageURL)
                    .placeholder(ic_loading)
                    .error(ic_error)
                    .fallback(ic_empty)
                    .into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageBitmap(mData.get(position).loadedImage);
        }

        return view;
    }

    public static class ViewHolder {
        ImageView imageView;
        ViewHolder(View itemView) {
            imageView = itemView.findViewById(R.id.img_viewerslide);
        }
    }
}
