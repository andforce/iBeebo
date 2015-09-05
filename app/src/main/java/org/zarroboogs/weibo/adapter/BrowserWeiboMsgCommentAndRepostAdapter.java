
package org.zarroboogs.weibo.adapter;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.UserInfoActivity;
import org.zarroboogs.weibo.activity.WriteReplyToCommentActivity;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dialogfragment.UserDialog;
import org.zarroboogs.weibo.fragment.BrowserWeiboMsgFragment;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.lib.LongClickableLinkMovementMethod;
import org.zarroboogs.weibo.support.lib.MyURLSpan;
import org.zarroboogs.weibo.support.utils.ThemeUtility;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.TimeLineAvatarImageView;
import org.zarroboogs.weibo.widget.TimeTextView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class BrowserWeiboMsgCommentAndRepostAdapter extends BaseAdapter {

    private boolean isCommentList = true;

    private List<CommentBean> commentListBean;

    private List<MessageBean> repostListBean;

    private Fragment fragment;

    private ListView listView;

    private int checkedBG;

    private int defaultBG;

    private LayoutInflater inflater;

    private Map<ViewHolder, Drawable> bg = new WeakHashMap<ViewHolder, Drawable>();

    public BrowserWeiboMsgCommentAndRepostAdapter(Fragment fragment, ListView listView, List<CommentBean> commentListBean,
            List<MessageBean> repostListBean) {

        this.fragment = fragment;
        this.listView = listView;
        this.commentListBean = commentListBean;
        this.repostListBean = repostListBean;
        this.inflater = fragment.getActivity().getLayoutInflater();

        this.defaultBG = fragment.getResources().getColor(R.color.transparent);
        this.checkedBG = ThemeUtility.getColor(fragment.getActivity(), R.attr.listview_checked_color);

    }

    protected Activity getActivity() {
        return fragment.getActivity();
    }

    public void switchToRepostType() {
        this.isCommentList = false;
        notifyDataSetChanged();
    }

    public void switchToCommentType() {
        this.isCommentList = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (isCommentList) {
            return commentListBean.size();
        } else {
            return repostListBean.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (isCommentList) {
            return commentListBean.get(position);
        } else {
            return repostListBean.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (isCommentList) {
            return commentListBean.get(position).getIdLong();
        } else {
            return repostListBean.get(position).getIdLong();
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = initSimpleLayout(parent);
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

    public void bindViewData(ViewHolder holder, int position) {
        if (isCommentList) {
            bindCommentData(holder, position);
        } else {
            bindRepostData(holder, position);
        }
    }

    private void bindCommentData(final ViewHolder holder, int position) {
        Drawable drawable = bg.get(holder);
        if (drawable != null) {
            holder.listview_root.setBackgroundDrawable(drawable);

        } else {
            drawable = holder.listview_root.getBackground();
            bg.put(holder, drawable);
        }

        if (listView.getCheckedItemPosition() == position + listView.getHeaderViewsCount()) {
            holder.listview_root.setBackgroundColor(checkedBG);
        }

        final CommentBean comment = (CommentBean) getItem(position);

        UserBean user = comment.getUser();
        if (user != null) {
            holder.username.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.getRemark())) {
                holder.username.setText(new StringBuilder(user.getScreen_name()).append("(").append(user.getRemark())
                        .append(")").toString());
            } else {
                holder.username.setText(user.getScreen_name());
            }
            if (!SettingUtils.getEnableCommentRepostListAvatar()) {
                holder.avatar.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            } else {
                buildAvatar(holder.avatar, position, user);
            }

        } else {
            holder.username.setVisibility(View.INVISIBLE);
            holder.avatar.setVisibility(View.INVISIBLE);
        }

        holder.avatar.checkVerified(user);

        holder.weiboTextContent.setText(comment.getListViewSpannableString());

        holder.time.setTime(comment.getMills());

        holder.comment_source.setText(Html.fromHtml(comment.getSource()).toString());
        
        holder.reply.setVisibility(View.VISIBLE);
        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(getActivity(), holder.reply);
                popupMenu.inflate(R.menu.comments_popmenu);
                popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem arg0) {
						// TODO Auto-generated method stub
						int id = arg0.getItemId();
						switch (id) {
						case R.id.reply_comment:{
							replyComment(comment);
							break;
						}
						case R.id.menu_copy:{
							ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
							cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", comment.getText()));
							Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
							break;
						}

						default:
							break;
						}
						return false;
					}
				});
                popupMenu.show();
            }
        });
    }

	private void replyComment(final CommentBean comment) {
		Intent intent = new Intent(getActivity(), WriteReplyToCommentActivity.class);
        intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
        intent.putExtra("msg", comment);
        getActivity().startActivity(intent);
	}
	
    private void bindRepostData(ViewHolder holder, int position) {
        Drawable drawable = bg.get(holder);
        if (drawable != null) {
            holder.listview_root.setBackgroundDrawable(drawable);

        } else {
            drawable = holder.listview_root.getBackground();
            bg.put(holder, drawable);
        }

        if (listView.getCheckedItemPosition() == position + listView.getHeaderViewsCount()) {
            holder.listview_root.setBackgroundColor(checkedBG);
        }

        final MessageBean msg = (MessageBean) getItem(position);

        UserBean user = msg.getUser();
        if (user != null) {
            holder.username.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.getRemark())) {
                holder.username.setText(new StringBuilder(user.getScreen_name()).append("(").append(user.getRemark())
                        .append(")").toString());
            } else {
                holder.username.setText(user.getScreen_name());
            }
            if (!SettingUtils.getEnableCommentRepostListAvatar()) {
                holder.avatar.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            } else {
                buildAvatar(holder.avatar, position, user);
            }

        } else {
            holder.username.setVisibility(View.INVISIBLE);
            holder.avatar.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(msg.getListViewSpannableString())) {
            holder.weiboTextContent.setText(msg.getListViewSpannableString());

        } else {
            TimeLineUtility.addJustHighLightLinks(msg);
            holder.weiboTextContent.setText(msg.getListViewSpannableString());
        }

        holder.comment_source.setText(Html.fromHtml(msg.getSource()).toString());
        
        holder.avatar.checkVerified(user);

        holder.time.setTime(msg.getMills());
        holder.reply.setVisibility(View.GONE);

    }

    private View initSimpleLayout(ViewGroup parent) {
        View convertView;
        convertView = inflater.inflate(R.layout.comments_list_item, parent, false);

        return convertView;
    }

    private void bindOnTouchListener(ViewHolder holder) {
        holder.listview_root.setClickable(false);
        holder.username.setClickable(false);
        holder.time.setClickable(false);
        holder.weiboTextContent.setClickable(false);

        if (holder.weiboTextContent != null) {
            holder.weiboTextContent.setOnTouchListener(onTouchListener);
        }

    }

    private ViewHolder buildHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.username = ViewUtility.findViewById(convertView, R.id.username);
//        TextPaint tp = holder.username.getPaint();
//        tp.setFakeBoldText(true);
        holder.weiboTextContent = ViewUtility.findViewById(convertView, R.id.weibo_text_content);
        holder.time = ViewUtility.findViewById(convertView, R.id.time);
        holder.avatar = (TimeLineAvatarImageView) convertView.findViewById(R.id.avatar);
        holder.listview_root = ViewUtility.findViewById(convertView, R.id.listview_root);
        holder.reply = ViewUtility.findViewById(convertView, R.id.replyIV);
        holder.comment_source = ViewUtility.findViewById(convertView, R.id.comment_source);
        
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

            if (holder.time != null) {
                holder.time.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

        }

    }

    private void configViewFont(ViewHolder holder) {
        int prefFontSizeSp = SettingUtils.getFontSize();
        float currentWidgetTextSizePx;

        currentWidgetTextSizePx = holder.time.getTextSize();

        if (Utility.sp2px(prefFontSizeSp - 3) != currentWidgetTextSizePx) {
            holder.time.setTextSize(prefFontSizeSp - 3);
            holder.comment_source.setTextSize(prefFontSizeSp -3);

        }

        currentWidgetTextSizePx = holder.weiboTextContent.getTextSize();

        if (Utility.sp2px(prefFontSizeSp) != currentWidgetTextSizePx) {
            holder.weiboTextContent.setTextSize(prefFontSizeSp);
            holder.username.setTextSize(prefFontSizeSp);

        }

    }

    // onTouchListener has some strange problem, when user click link,
    // holder.listview_root may also receive a MotionEvent.ACTION_DOWN event
    // the background then changed
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ViewHolder holder = getViewHolderByView(v);

            if (holder == null) {
                return false;
            }

            Layout layout = ((TextView) v).getLayout();

            int x = (int) event.getX();
            int y = (int) event.getY();
            int offset = 0;
            if (layout != null) {

                int line = layout.getLineForVertical(y);
                offset = layout.getOffsetForHorizontal(line, x);
            }

            TextView tv = (TextView) v;
            SpannableString value = SpannableString.valueOf(tv.getText());

            LongClickableLinkMovementMethod.getInstance().onTouchEvent(tv, value, event);

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    MyURLSpan[] urlSpans = value.getSpans(0, value.length(), MyURLSpan.class);
                    boolean find = false;
                    int findStart = 0;
                    int findEnd = 0;
                    for (MyURLSpan urlSpan : urlSpans) {
                        int start = value.getSpanStart(urlSpan);
                        int end = value.getSpanEnd(urlSpan);
                        if (start <= offset && offset <= end) {
                            find = true;
                            findStart = start;
                            findEnd = end;

                            break;
                        }
                    }
                    boolean hasActionMode = ((BrowserWeiboMsgFragment) fragment).hasActionMode();
                    boolean result = false;
                    if (find && !hasActionMode) {
                        result = true;
                    }

                    if (find && !result) {
                        BackgroundColorSpan[] backgroundColorSpans = value.getSpans(0, value.length(),
                                BackgroundColorSpan.class);
                        for (BackgroundColorSpan urlSpan : backgroundColorSpans) {
                            value.removeSpan(urlSpan);
                            ((TextView) v).setText(value);
                        }
                    }

                    if (result) {
                        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(
                                ThemeUtility.getColor(R.attr.link_pressed_background_color));
                        value.setSpan(backgroundColorSpan, findStart, findEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        ((TextView) v).setText(value);
                    }

                    return result;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    LongClickableLinkMovementMethod.getInstance().removeLongClickCallback();
                    BackgroundColorSpan[] backgroundColorSpans = value
                            .getSpans(0, value.length(), BackgroundColorSpan.class);
                    for (BackgroundColorSpan urlSpan : backgroundColorSpans) {
                        value.removeSpan(urlSpan);
                        ((TextView) v).setText(value);
                    }
                    break;
            }

            return false;

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
        ViewHolder holder = (ViewHolder) wantedView.getTag();
        return holder;

    }

    private static class ViewHolder {

        RelativeLayout listview_root;

        TextView username;

        TextView weiboTextContent;

        TimeTextView time;

        TimeLineAvatarImageView avatar;

        ImageView reply;
        
        TextView comment_source;
    }

    protected void buildAvatar(TimeLineAvatarImageView view, int position, final UserBean user) {
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
        buildAvatar(view.getImageView(), position, user);
    }

    protected void buildAvatar(ImageView view, int position, final UserBean user) {
        String image_url = user.getAvatar_large();
        if (!TextUtils.isEmpty(image_url)) {
            view.setVisibility(View.VISIBLE);
            TimeLineBitmapDownloader.getInstance().downloadAvatar(view, user, false);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void removeCommentItem(final int postion) {
        if (postion >= 0 && postion < commentListBean.size()) {
            Animation anim = AnimationUtils.loadAnimation(fragment.getActivity(), R.anim.account_delete_slide_out_right);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    commentListBean.remove(postion);
                    BrowserWeiboMsgCommentAndRepostAdapter.this.notifyDataSetChanged();
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
            } else {
                commentListBean.remove(postion);
                BrowserWeiboMsgCommentAndRepostAdapter.this.notifyDataSetChanged();
            }

        }
    }
}
