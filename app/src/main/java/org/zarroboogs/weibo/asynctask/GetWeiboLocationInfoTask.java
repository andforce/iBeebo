
package org.zarroboogs.weibo.asynctask;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.bean.GeoBean;
import org.zarroboogs.weibo.dao.BaiduGeoCoderDao;
import org.zarroboogs.weibo.dao.GoogleGeoCoderDao;
import org.zarroboogs.weibo.dao.MapDao;
import org.zarroboogs.weibo.support.utils.Utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GetWeiboLocationInfoTask extends MyAsyncTask<Void, String, Bitmap> {

    private Activity activity;

    private TextView location;

    private ImageView mapView;

    private GeoBean geoBean;

    public GetWeiboLocationInfoTask(Activity activity, GeoBean geoBean, ImageView mapView, TextView location) {
        this.geoBean = geoBean;
        this.activity = activity;
        this.mapView = mapView;
        this.location = location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        location.setVisibility(View.VISIBLE);
        location.setText(String.valueOf(geoBean.getLat() + "," + geoBean.getLon()));

    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (Utility.isGPSLocationCorrect(geoBean)) {

            String gpsLocationString = new GoogleGeoCoderDao(activity, geoBean).get();

            try {
                if (TextUtils.isEmpty(gpsLocationString)) {
                    publishProgress(new BaiduGeoCoderDao(geoBean.getLat(), geoBean.getLon()).get());
                }
            } catch (WeiboException e) {
                e.printStackTrace();
            }
        }

        MapDao dao = new MapDao(BeeboApplication.getInstance().getAccessToken(), geoBean.getLat(), geoBean.getLon());

        try {
            return dao.getMap();
        } catch (WeiboException e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (!TextUtils.isEmpty(values[0])) {
            location.setVisibility(View.VISIBLE);
            location.setText(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Bitmap s) {
        mapView.setImageBitmap(s);
        super.onPostExecute(s);
    }
}
