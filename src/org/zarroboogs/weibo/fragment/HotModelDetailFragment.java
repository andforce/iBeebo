package org.zarroboogs.weibo.fragment;


import java.util.ArrayList;
import java.util.List;

import lib.org.zarroboogs.weibo.login.utils.LogTool;

import org.apache.http.Header;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.adapter.HotModelAdapter;
import org.zarroboogs.weibo.adapter.HotModelDetailAdapter;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.hot.bean.model.HotModel;
import org.zarroboogs.weibo.hot.bean.model.detail.HotModelDetail;
import org.zarroboogs.weibo.hot.bean.model.detail.HotModelDetailCard;
import org.zarroboogs.weibo.hot.bean.model.detail.Pics;
import org.zarroboogs.weibo.support.asyncdrawable.IWeiciyuanDrawable;
import org.zarroboogs.weibo.support.asyncdrawable.MsgDetailReadWorker;
import org.zarroboogs.weibo.support.gallery.GalleryAnimationActivity;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.utils.Utility;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.update.net.i;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HotModelDetailFragment extends BaseStateFragment {

	private HotModelDetailAdapter adapter;

	private int mPage = 1;

	private AsyncHttpClient mAsyncHttoClient = new AsyncHttpClient();

	private String extparam = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		extparam = getArguments().getString("extparam");
		LogTool.D("extparam: " + extparam);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout swipeFrameLayout = (RelativeLayout) inflater.inflate(
				R.layout.hot_model_detail_fragment_layout, container, false);

		final GridView gridView = (GridView) swipeFrameLayout.findViewById(R.id.modelDetailGV);
		adapter = new HotModelDetailAdapter(getActivity().getApplicationContext());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ArrayList<String> pics  = adapter.getPicStrings();
				
				ArrayList<AnimationRect> animationRectArrayList = new ArrayList<AnimationRect>();
                for (int i = 0; i < pics.size(); i++) {
                	RelativeLayout rl = (RelativeLayout)gridView.getChildAt(i);
                        AnimationRect rect = AnimationRect.buildFromImageView((ImageView)rl.getChildAt(0));
                        animationRectArrayList.add(rect);
                }
                
				Intent intent = GalleryAnimationActivity.newIntent(pics, animationRectArrayList,position);
				startActivity(intent);
				
			}
		});
		loadNewRepostData();
		return swipeFrameLayout;
	}


	public void loadNewRepostData() {
		mAsyncHttoClient.get(
				WeiBoURLs.hotModelDetail("4u8Kc2373x4U9rFAXPfxc7SC21d", mPage, extparam),
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						// TODO Auto-generated method stub
						String json = new String(responseBody);
						Utility.printLongLog("READ_JSON_DONE-GET_DATE_FROM_NET",json);

						HotModelDetail hotModel = new Gson().fromJson(json,HotModelDetail.class);
							HotModelDetailCard hCard = hotModel.getCards().get(0);
							List<Pics> pics = hCard.getPics();
							adapter.addNewData(pics);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
					}
				});
	}


}
