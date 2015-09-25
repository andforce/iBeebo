
package org.zarroboogs.weibo.adapter;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.UserInfoActivity;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.data.DMBean;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.setting.SettingUtils;
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

public class DMConversationAdapter extends BaseAdapter {
    private List<DMBean> bean;
    private Fragment fragment;
    private LayoutInflater inflater;
    private TimeLineBitmapDownloader commander;

    private final int TYPE_NORMAL = 0;
    private final int TYPE_MYSELF = 1;

    public DMConversationAdapter(Fragment fragment, List<DMBean> bean, ListView listView) {
        this.bean = bean;
        this.commander = TimeLineBitmapDownloader.getInstance();
        this.inflater = fragment.getActivity().getLayoutInflater();
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
        if (dmBean.getUser().getId().equals(BeeboApplication.getInstance().getCurrentAccountId())) {
            return TYPE_MYSELF;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int showPosition = bean.size() - 1 - position;

        ViewHolder holder;
        if (convertView == null || convertView.getTag(getItemViewType(showPosition) == TYPE_MYSELF ? R.id.dm_MySimpleLayout : R.id.dm_NormalSimpleLayout) == null) {

            holder = new ViewHolder();

            convertView = getItemViewType(showPosition) == TYPE_MYSELF ? initMySimpleLayout(parent) : initNormalSimpleLayout(parent);

            holder.content = ViewUtility.findViewById(convertView, R.id.content);
            holder.time = ViewUtility.findViewById(convertView, R.id.time);
            holder.avatar = (TimeLineAvatarImageView) convertView.findViewById(R.id.avatar);

            convertView.setTag(getItemViewType(showPosition) == TYPE_MYSELF ? R.id.dm_MySimpleLayout : R.id.dm_NormalSimpleLayout, holder);

        } else {
            holder = (ViewHolder) convertView.getTag(getItemViewType(showPosition) == TYPE_MYSELF ? R.id.dm_MySimpleLayout : R.id.dm_NormalSimpleLayout);
        }

        configViewFont(holder);
        configLayerType(holder);
        bindViewData(holder, showPosition);
        return convertView;
    }

    private void configLayerType(ViewHolder holder) {

        boolean disableHardAccelerated = SettingUtils.disableHardwareAccelerated();
        if (!disableHardAccelerated)
            return;

        int currentWidgetLayerType = holder.content.getLayerType();

        if (View.LAYER_TYPE_SOFTWARE != currentWidgetLayerType) {
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

    private void configViewFont(ViewHolder holder) {
        holder.time.setTextSize(SettingUtils.getFontSize() - 3);
        holder.content.setTextSize(SettingUtils.getFontSize());
    }

    protected void bindViewData(ViewHolder holder, int position) {

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
        view.checkVerified(user);
        String image_url = user.getAvatar_large();
        if (!TextUtils.isEmpty(image_url)) {
            view.setVisibility(View.VISIBLE);
            commander.downloadAvatar(view.getImageView(), user, (AbsBaseTimeLineFragment) fragment);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
                    intent.putExtra("user", user);
                    getActivity().startActivity(intent);
                }
            });

        } else {
            view.setVisibility(View.GONE);
        }
    }

    private static class ViewHolder {
        TextView content;
        TextView time;
        TimeLineAvatarImageView avatar;

    }

}
