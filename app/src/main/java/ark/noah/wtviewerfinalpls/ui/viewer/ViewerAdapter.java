package ark.noah.wtviewerfinalpls.ui.viewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import ark.noah.wtviewerfinalpls.R;

public class ViewerAdapter extends BaseAdapter {
    ArrayList<ViewerContainer> mData;
    LayoutInflater mLayoutInflater;

    ImageLoader imageLoader;
    ImageLoaderConfiguration config;
    DisplayImageOptions options;

    Drawable ic_loading, ic_error, ic_empty;

    @SuppressLint("UseCompatLoadingForDrawables")
    ViewerAdapter(ArrayList<ViewerContainer> list, Context context) {
        mData = list;
        mLayoutInflater = LayoutInflater.from(context);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_baseline_downloading_24)
                .showImageForEmptyUri(R.drawable.ic_baseline_device_unknown_24)
                .showImageOnFail(R.drawable.ic_baseline_error_outline_24)
                .delayBeforeLoading(1000)
                .build();
        config = new ImageLoaderConfiguration.Builder(context)
                .diskCacheExtraOptions(480, 800, null)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .writeDebugLogs()
                .build();
        imageLoader.init(config);

        ic_loading = context.getDrawable(R.drawable.ic_baseline_downloading_24);
        ic_empty   = context.getDrawable(R.drawable.ic_baseline_device_unknown_24);
        ic_error   = context.getDrawable(R.drawable.ic_baseline_error_outline_24);
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
            imageLoader.displayImage(mData.get(position).imageURL, viewHolder.imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    ((ImageView) view).setImageDrawable(ic_loading);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ImageView) view).setImageDrawable(ic_error);
                    //Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ((ImageView) view).setImageBitmap(loadedImage);
                    mData.get(position).loadedImage = loadedImage;
                }
            });
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
