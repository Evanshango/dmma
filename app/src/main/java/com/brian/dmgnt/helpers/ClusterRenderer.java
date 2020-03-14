package com.brian.dmgnt.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brian.dmgnt.R;
import com.brian.dmgnt.models.ClusterMarker;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private static final String TAG = "ClusterRenderer";
    private final IconGenerator mIconGenerator;
    private final ImageView mImageView;
    private Context mContext;
    private static Bitmap result;

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mIconGenerator = new IconGenerator(context.getApplicationContext());
        mImageView = new ImageView(context.getApplicationContext());
        int markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        int markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
//        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        mImageView.setImageBitmap(getBitmapFromURL(item.getIconPic()));
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
                .title(item.getTitle()).snippet(item.getSnippet());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }

    private static Bitmap getBitmapFromURL(String src) {
        new Thread(() -> {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                result =  BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                result =  null;
            }
        }).start();
        return result;
    }
}
