package org.zarroboogs.weibo.adapter;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.UserInfoActivity;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.data.DMBean;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.IWeiciyuanDrawable;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.TimeLineAvatarImageView;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * User: qii Date: 12-11-15
 */
public class DMConversationAdapter extends BaseAdapter {
	private List<DMBean> bean;
	private Fragment fragment;
	private LayoutInflater inflater;
	private ListView listView;
	private TimeLineBitmapDownloader commander;

	private final int TYPE_NORMAL = 0;
	private final int TYPE_MYSELF = 1;

	public DMConversationAdapter(Fragment fragment, List<DMBean> bean, ListView listView) {
		this.bean = bean;
		this.commander = TimeLineBitmapDownloader.getInstance();
		this.inflater = fragment.getActivity().getLayoutInflater();
		this.listView = listView;
		this.fragment = fragment;

	}

	protected Activity getActivity() {
		return fragment.getActivity();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		DMBean dmBean = bean.get(position);
		if (dmBean.getUser().getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
			return TYPE_MYSELF;
		} else {
			return TYPE_NORMAL;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int showPosition = bean.size() - 1 - position;

		DMViewHolder holder = null;
		if (convertView == null || convertView.getTag(R.drawable.beebo_launcher + getItemViewType(showPosition)) == null) {
			switch (getItemViewType(showPosition)) {
			case TYPE_NORMAL:
				convertView = initNormalSimpleLayout(parent);
				break;
			case TYPE_MYSELF:
				convertView = initMySimpleLayout(parent);
				break;
			default:
				throw new IllegalArgumentException("dm user type is wrong");
			}
			holder = buildHolder(convertView);
			convertView.setTag(R.drawable.beebo_launcher + getItemViewType(showPosition), holder);
			convertView.setTag(R.string.listview_index_tag, R.drawable.beebo_launcher + getItemViewType(showPosition));

		} else {
			holder = (DMViewHolder) convertView.getTag(R.drawable.beebo_launcher + getItemViewType(showPosition));
		}

		configViewFont(holder);
		configLayerType(holder);
		bindViewData(holder, showPosition);
		return convertView;
	}

	private void configLayerType(DMViewHolder holder) {

		boolean disableHardAccelerated = SettingUtils.disableHardwareAccelerated();
		if (!disableHardAccelerated)
			return;

		int currentWidgetLayerType = holder.content.getLayerType();

		if (View.LAYER_TYPE_SOFTWARE != currentWidgetLayerType) {
			if (holder.content != null)
				holder.content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

			if (holder.time != null)
				holder.time.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

	}

	private View initNormalSimpleLayout(ViewGroup parent) {
		View convertView;
		convertView = inflater.inflate(R.layout.dmconversationadapter_item_normal_layout, parent, false);

		return convertView;
	}

	private View initMySimpleLayout(ViewGroup parent) {
		View convertView;
		convertView = inflater.inflate(R.layout.dmconversationadapter_item_myself_layout, parent, false);
		return convertView;
	}

	private DMViewHolder buildHolder(View convertView) {
		DMViewHolder holder = new DMViewHolder();
		holder.content = ViewUtility.findViewById(convertView, R.id.content);
		holder.time = ViewUtility.findViewById(convertView, R.id.time);
		holder.avatar = (TimeLineAvatarImageView) convertView.findViewById(R.id.avatar);
		return holder;
	}

	private void configViewFont(DMViewHolder holder) {
		holder.time.setTextSize(SettingUtils.getFontSize() - 3);
		holder.content.setTextSize(SettingUtils.getFontSize());
	}

	protected void bindViewData(DMViewHolder holder, int position) {

		final DMBean msg = bean.get(position);
		UserBean user = msg.getUser();
		if (user != null) {
			buildAvatar(holder.avatar, position, user);
		} else {
			holder.avatar.setVisibility(View.INVISIBLE);
		}

		if (!TextUtils.isEmpty(msg.getListViewSpannableString())) {
			holder.content.setText(msg.getListViewSpannableString());
		} else {
			TimeLineUtility.addJustHighLightLinks(msg);
			holder.content.setText(msg.getListViewSpannableString());
		}
		String time = msg.getListviewItemShowTime();

		if (!holder.time.getText().toString().equals(time)) {
			holder.time.setText(time);
		}
		holder.time.setTag(msg.getId());

	}

	protected List<DMBean> getList() {
		return bean;
	}

	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {

		if (getList() != null) {
			return getList().size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (position >= 0 && getList() != null && getList().size() > 0 && position < getList().size())
			return getList().get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (getList() != null && getList().get(position) != null && getList().size() > 0 && position < getList().size())
			return Long.valueOf(getList().get(position).getId());
		else
			return -1;
	}

	protected void buildAvatar(TimeLineAvatarImageView view, int position, final UserBean user) {
		((IWeiciyuanDrawable) view).checkVerified(user);
		String image_url = user.getProfile_image_url();
		if (!TextUtils.isEmpty(image_url)) {
			view.setVisibility(View.VISIBLE);
			commander.downloadAvatar(view.getImageView(), user, (AbsBaseTimeLineFragment) fragment);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), UserInfoActivity.class);
					intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
					intent.putExtra("user", user);
					getActivity().startActivity(intent);
				}
			});

		} else {
			view.setVisibility(View.GONE);
		}
	}

	private static class DMViewHolder {
		TextView content;
		TextView time;
		TimeLineAvatarImageView avatar;

	}

}