
package org.zarroboogs.weibo.adapter;

import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.UserInfoActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.data.DataItem;
import org.zarroboogs.weibo.dialogfragment.UserDialog;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.IPictureWorker;
import org.zarroboogs.weibo.support.asyncdrawable.IWeiboDrawable;
import org.zarroboogs.weibo.support.asyncdrawable.PictureBitmapDrawable;
import org.zarroboogs.weibo.support.gallery.GalleryAnimationActivity;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.lib.ClickableTextViewMentionLinkOnTouchListener;
import org.zarroboogs.weibo.support.utils.ThemeUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.TimeLineAvatarImageView;
import org.zarroboogs.weibo.widget.TimeTextView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAppListAdapter<T extends DataItem> extends BaseAdapter {

    protected List<T> bean;

    protected Fragment fragment;

    protected LayoutInflater inflater;

    protected ListView listView;

    protected boolean showOriStatus = true;

    protected int checkedBG;

    protected int defaultBG;

    public static final int NO_ITEM_ID = -1;

    public AbstractAppListAdapter(Fragment fragment, List<T> bean, ListView listView, boolean showOriStatus, boolean pre) {
        if (showOriStatus) {
            listView.setDivider(null);
        }

        this.bean = bean;
        this.inflater = fragment.getActivity().getLayoutInflater();
        this.listView = listView;
        this.showOriStatus = showOriStatus;
        this.fragment = fragment;

        defaultBG = fragment.getResources().getColor(R.color.transparent);
        checkedBG = ThemeUtility.getColor(R.attr.listview_checked_color);


    }

    protected Activity getActivity() {
        return fragment.getActivity();
    }

    /**
     * use getTag(int) and setTag(int, final Object) to solve getItemViewType(int) bug. When you use
     * getItemViewType(int),getTag(),setTag() together, if getItemViewType(int) change because
     * network switch to use another layout when you are scrolling listview, bug appears,the other
     * listviews in other tabs (Actionbar tab navigation) will mix several layout up, for example,
     * the correct layout should be TYPE_NORMAL_BIG_PIC, but in the listview, you can see some row's
     * layouts are TYPE_NORMAL, some are TYPE_NORMAL_BIG_PIC. if you print getItemViewType(int)
     * value to the console,their are same type
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.timeline_listview_item_layout, parent, false);

            holder = buildHolder(convertView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        configLayerType(holder);
        configViewFont(holder);
        bindViewData(holder, position);
        bindOnTouchListener(holder);

        return convertView;
    }

    private void bindOnTouchListener(ViewHolder holder) {
        holder.listview_root.setClickable(false);
        holder.username.setClickable(false);
        holder.time.setClickable(false);
        holder.weiboTextContent.setClickable(false);
        holder.repost_content.setClickable(false);

        if (holder.weiboTextContent != null) {
            holder.weiboTextContent.setOnTouchListener(onTouchListener);
        }
        if (holder.repost_content != null) {
            holder.repost_content.setOnTouchListener(onTouchListener);
        }
    }



    // weibo image widgets and its forward weibo image widgets are the same
    private ViewHolder buildHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.username = ViewUtility.findViewById(convertView, R.id.username);

        holder.weiboTextContent = ViewUtility.findViewById(convertView, R.id.weibo_text_content);
        holder.repost_content = ViewUtility.findViewById(convertView, R.id.repost_content);
        holder.time = ViewUtility.findViewById(convertView, R.id.time);
        holder.avatar = (TimeLineAvatarImageView) convertView.findViewById(R.id.avatar);

        holder.repost_content_pic = (IWeiboDrawable) convertView.findViewById(R.id.repost_content_pic);
        holder.repost_content_pic_multi = ViewUtility.findViewById(convertView, R.id.repost_content__pic_multi);

        holder.content_pic = (IWeiboDrawable) convertView.findViewById(R.id.content_pic);// holder.repost_content_pic;
        holder.content_pic_multi = ViewUtility.findViewById(convertView, R.id.content_pic_multi);

        holder.listview_root = ViewUtility.findViewById(convertView, R.id.listview_root);
        holder.repost_layout = ViewUtility.findViewById(convertView, R.id.repost_layout);
        holder.repost_flag = ViewUtility.findViewById(convertView, R.id.timeLineRepostRootLayout);
        holder.count_layout = ViewUtility.findViewById(convertView, R.id.count_layout);
        holder.repost_count = ViewUtility.findViewById(convertView, R.id.repost_count);
        holder.comment_count = ViewUtility.findViewById(convertView, R.id.comment_count);
        holder.timeline_gps = ViewUtility.findViewById(convertView, R.id.timeline_gps_iv);
        holder.timeline_pic = ViewUtility.findViewById(convertView, R.id.timeline_pic_iv);
        holder.cmmentsReply = ViewUtility.findViewById(convertView, R.id.replyIV);
        holder.source = ViewUtility.findViewById(convertView, R.id.source);
        
        holder.comment_source = ViewUtility.findViewById(convertView, R.id.comment_source);
        
        holder.popupMenuIb = ViewUtility.findViewById(convertView, R.id.popupMenuIb);
        
        return holder;
    }

    private void configLayerType(ViewHolder holder) {

        boolean disableHardAccelerated = SettingUtils.disableHardwareAccelerated();
        if (!disableHardAccelerated) {
            return;
        }

        int currentWidgetLayerType = holder.username.getLayerType();

        if (View.LAYER_TYPE_SOFTWARE != currentWidgetLayerType) {
            holder.username.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            if (holder.weiboTextContent != null) {
                holder.weiboTextContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            if (holder.repost_content != null) {
                holder.repost_content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            if (holder.time != null) {
                holder.time.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            if (holder.repost_count != null) {
                holder.repost_count.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            if (holder.comment_count != null) {
                holder.comment_count.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }

    }

    private void configViewFont(ViewHolder holder) {
        int prefFontSizeSp = SettingUtils.getFontSize();
        float currentWidgetTextSizePx;

        currentWidgetTextSizePx = holder.time.getTextSize();

        if (Utility.sp2px(prefFontSizeSp - 3) != currentWidgetTextSizePx) {
            holder.time.setTextSize(prefFontSizeSp - 3);
            if (holder.source != null) {
                holder.source.setTextSize(prefFontSizeSp - 3);
            }
        }

        currentWidgetTextSizePx = holder.weiboTextContent.getTextSize();

        if (Utility.sp2px(prefFontSizeSp) != currentWidgetTextSizePx) {
            holder.weiboTextContent.setTextSize(prefFontSizeSp);
            holder.username.setTextSize(prefFontSizeSp);
            holder.repost_content.setTextSize(prefFontSizeSp);

        }

        if (holder.repost_count != null) {
            currentWidgetTextSizePx = holder.repost_count.getTextSize();
            if (Utility.sp2px(prefFontSizeSp - 5) != currentWidgetTextSizePx) {
                holder.repost_count.setTextSize(prefFontSizeSp - 5);
            }
        }

        if (holder.comment_count != null) {
            currentWidgetTextSizePx = holder.comment_count.getTextSize();
            if (Utility.sp2px(prefFontSizeSp - 5) != currentWidgetTextSizePx) {
                holder.comment_count.setTextSize(prefFontSizeSp - 5);
            }
        }
    }

    protected abstract void bindViewData(ViewHolder holder, int position);

    protected List<T> getList() {
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
        if (position >= 0 && getList() != null && getList().size() > 0 && position < getList().size()) {
            return getList().get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (getList() != null && getList().get(position) != null && getList().size() > 0 && position < getList().size()) {
            return Long.valueOf(getList().get(position).getId());
        } else {
            return NO_ITEM_ID;
        }
    }

    public Fragment getFragment(){
        return fragment;
    }

    protected void buildAvatar(IWeiboDrawable view, int position, final UserBean user) {
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
                intent.putExtra("user", user);
                getActivity().startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UserDialog dialog = new UserDialog(user);
                dialog.show(fragment.getFragmentManager(), "");
                return true;
            }
        });
        view.checkVerified(user);
        buildAvatar(view.getImageView(), user);
    }

    protected void buildAvatar(ImageView view,  final UserBean user) {
        String image_url = user.getAvatar_large();
        if (!TextUtils.isEmpty(image_url)) {
            view.setVisibility(View.VISIBLE);
            Glide.with(getFragment()).load(image_url).crossFade().into(view);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    protected void buildMultiPic(final MessageBean msg, final GridLayout gridLayout) {
        if (SettingUtils.isEnablePic()) {
            gridLayout.setVisibility(View.VISIBLE);

            final int count = msg.getPicCount();
            for (int i = 0; i < count; i++) {
                final IWeiboDrawable pic = (IWeiboDrawable) gridLayout.getChildAt(i);
                pic.setVisibility(View.VISIBLE);
                if (SettingUtils.getEnableBigPic()) {
                    Glide.with(getFragment()).load(msg.getHighPicUrls().get(i)).crossFade().centerCrop().into(pic.getImageView());
                } else {
                    Glide.with(getFragment()).load(msg.getThumbnailPicUrls().get(i)).crossFade().centerCrop().into(pic.getImageView());
                }

                final int finalI = i;
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<AnimationRect> animationRectArrayList = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            final IWeiboDrawable pic = (IWeiboDrawable) gridLayout.getChildAt(i);
                            ImageView imageView = (ImageView) pic;
                            if (imageView.getVisibility() == View.VISIBLE) {
                                AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                                animationRectArrayList.add(rect);
                            }
                        }

                        Intent intent = GalleryAnimationActivity.newIntent(msg, animationRectArrayList, finalI);
                        getActivity().startActivity(intent);
                    }
                });

            }

            if (count < 9) {
                ImageView pic;
                switch (count) {
                    case 8:
                        pic = (ImageView) gridLayout.getChildAt(8);
                        pic.setVisibility(View.INVISIBLE);
                        break;
                    case 7:
                        for (int i = 8; i > 6; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 6:
                        for (int i = 8; i > 5; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }

                        break;
                    case 5:
                        for (int i = 8; i > 5; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        pic = (ImageView) gridLayout.getChildAt(5);
                        pic.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        for (int i = 8; i > 5; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        pic = (ImageView) gridLayout.getChildAt(5);
                        pic.setVisibility(View.INVISIBLE);
                        pic = (ImageView) gridLayout.getChildAt(4);
                        pic.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        for (int i = 8; i > 2; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        for (int i = 8; i > 2; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        pic = (ImageView) gridLayout.getChildAt(2);
                        pic.setVisibility(View.INVISIBLE);
                        break;

                }

            }

        } else {
            gridLayout.setVisibility(View.GONE);
        }

    }

    protected void buildPic(final MessageBean msg, final IWeiboDrawable view) {
        if (SettingUtils.isEnablePic()) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ImageView imageView = view.getImageView();

                    AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                    ArrayList<AnimationRect> animationRectArrayList = new ArrayList<>();
                    animationRectArrayList.add(rect);

                    Intent intent = GalleryAnimationActivity.newIntent(msg, animationRectArrayList, 0);
                    getActivity().startActivity(intent);
                }
            });
            view.setVisibility(View.VISIBLE);

            if (SettingUtils.getEnableBigPic()) {
                Glide.with(getFragment()).load(msg.getOriginal_pic()).centerCrop().crossFade().into(view.getImageView());
            } else {
                Glide.with(getFragment()).load(msg.getThumbnail_pic()).centerCrop().crossFade().into(view.getImageView());
            }

        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder {

        TextView username;

        TextView weiboTextContent;

        TextView repost_content;

        TimeTextView time;

        IWeiboDrawable avatar;

        IWeiboDrawable content_pic;

        GridLayout content_pic_multi;

        IWeiboDrawable repost_content_pic;

        GridLayout repost_content_pic_multi;

        ViewGroup listview_root;

        View repost_layout;

        View repost_flag;

        LinearLayout count_layout;

        TextView repost_count;

        TextView comment_count;

        TextView source;

        ImageView timeline_gps;

        ImageView timeline_pic;

        ImageButton cmmentsReply;
        
        ImageButton popupMenuIb;
        
        TextView comment_source;
    }

    public void removeItem(final int postion) {
        if (postion >= 0 && postion < bean.size()) {
            AppLoggerUtils.e("1");
            Animation anim = AnimationUtils.loadAnimation(fragment.getActivity(), R.anim.account_delete_slide_out_right);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                    AppLoggerUtils.e("4");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bean.remove(postion);
                    AbstractAppListAdapter.this.notifyDataSetChanged();
                    AppLoggerUtils.e("5");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            int positonInListView = postion + 1;
            int start = listView.getFirstVisiblePosition();
            int end = listView.getLastVisiblePosition();

            if (positonInListView >= start && positonInListView <= end) {
                int positionInCurrentScreen = postion - start;
                listView.getChildAt(positionInCurrentScreen + 1).startAnimation(anim);
                AppLoggerUtils.e("2");
            } else {
                bean.remove(postion);
                AbstractAppListAdapter.this.notifyDataSetChanged();
                AppLoggerUtils.e("3");
            }

        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ViewHolder holder = getViewHolderByView(v);

            return holder != null && listener.onTouch(v, event);

        }
    };

    // when view is recycled by listview, need to catch exception
    private ViewHolder getViewHolderByView(View view) {
        try {
            final int position = listView.getPositionForView(view);
            if (position == ListView.INVALID_POSITION) {
                return null;
            }
            return getViewHolderByView(position);
        } catch (NullPointerException e) {

        }
        return null;
    }

    private ViewHolder getViewHolderByView(int position) {

        int wantedPosition = position - listView.getHeaderViewsCount();
        int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
        int wantedChild = wantedPosition - firstPosition;

        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            return null;
        }

        View wantedView = listView.getChildAt(wantedChild);
        ViewHolder holder = (ViewHolder) wantedView.getTag(R.string.app_name + getItemViewType(wantedPosition));
        return holder;

    }

}
