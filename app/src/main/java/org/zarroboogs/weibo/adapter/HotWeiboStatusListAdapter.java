
package org.zarroboogs.weibo.adapter;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.RepostWeiboWithAppSrcActivity;
import org.zarroboogs.weibo.activity.WriteCommentActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.ui.task.FavAsyncTask;
import org.zarroboogs.weibo.ui.task.UnFavAsyncTask;
import org.zarroboogs.weibo.widget.AutoScrollListView;
import org.zarroboogs.weibo.widget.TopTipsView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;
import java.util.ListIterator;

public class HotWeiboStatusListAdapter extends AbstractAppListAdapter<MessageBean> {

    private LongSparseArray<Integer> msgHeights = new LongSparseArray<>();

    private LongSparseArray<Integer> msgWidths = new LongSparseArray<>();

    private LongSparseArray<Integer> oriMsgHeights = new LongSparseArray<>();

    private LongSparseArray<Integer> oriMsgWidths = new LongSparseArray<>();

    private TopTipsView topTipBar;

    private FavAsyncTask favTask = null;

    private UnFavAsyncTask unFavTask = null;


    public HotWeiboStatusListAdapter(Fragment fragment, List<MessageBean> bean, ListView listView, boolean showOriStatus, boolean pre) {
        super(fragment, bean, listView, showOriStatus, pre);
    }

    public void setTopTipBar(TopTipsView bar) {
        this.topTipBar = bar;
        AutoScrollListView autoScrollListView = (AutoScrollListView) listView;
        autoScrollListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                View childView = Utility.getListViewItemViewFromPosition(listView, firstVisibleItem);

                if (childView == null) {
                    return;
                }

                int position = firstVisibleItem - ((ListView) view).getHeaderViewsCount();

                if (childView.getTop() == 0 && position <= 0) {
                    topTipBar.clearAndReset();
                } else {
                    handle(position + 1);
                }
                // }
            }

            private void handle(int position) {
                if (position > 0 && topTipBar != null && position < bean.size()) {
                    MessageBean next = bean.get(position);
                    if (next != null) {
                        MessageBean helperMsg = bean.get(position - 1);
                        long helperId = 0L;
                        if (helperMsg != null) {
                            helperId = helperMsg.getIdLong();
                        }
                        topTipBar.handle(next.getIdLong(), helperId);
                    }
                }

            }
        });
    }

    @Override
    protected void bindViewData(final ViewHolder holder, int position) {

        if (listView.getCheckedItemPosition() == position + listView.getHeaderViewsCount()) {
            holder.listview_root.setBackgroundColor(checkedBG);
        }

        final MessageBean msg = bean.get(position);

        UserBean user = msg.getUser();
        if (user != null) {
            holder.username.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.getRemark())) {
                holder.username.setText(user.getScreen_name() + "(" + user.getRemark() + ")");
            } else {
                holder.username.setText(user.getScreen_name());
            }
            if (!showOriStatus && !SettingUtils.getEnableCommentRepostListAvatar()) {
                holder.avatar.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            } else {
                buildAvatar(holder.avatar, position, user);
            }

        } else {
            holder.username.setVisibility(View.INVISIBLE);
            holder.avatar.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(msg.getListViewSpannableString())) {
            boolean haveCachedHeight = msgHeights.get(msg.getIdLong()) != null;
            ViewGroup.LayoutParams layoutParams = holder.weiboTextContent.getLayoutParams();
            if (haveCachedHeight) {
                layoutParams.height = msgHeights.get(msg.getIdLong());
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            boolean haveCachedWidth = msgWidths.get(msg.getIdLong()) != null;
            if (haveCachedWidth) {
                layoutParams.width = msgWidths.get(msg.getIdLong());
            } else {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            holder.weiboTextContent.requestLayout();
            holder.weiboTextContent.setText(msg.getListViewSpannableString());
            if (!haveCachedHeight) {
                msgHeights.append(msg.getIdLong(), layoutParams.height);
            }

            if (!haveCachedWidth) {
                msgWidths.append(msg.getIdLong(), layoutParams.width);
            }
        } else {
            TimeLineUtility.addJustHighLightLinks(msg);
            holder.weiboTextContent.setText(msg.getListViewSpannableString());
        }

        holder.time.setTime(msg.getMills());
        if (holder.source != null) {
            holder.source.setText(msg.getSourceString());
        }

        if (showOriStatus) {
            boolean checkRepostsCount = (msg.getReposts_count() != 0);
            boolean checkCommentsCount = (msg.getComments_count() != 0);
            boolean checkPic = msg.havePicture()
                    || (msg.getRetweeted_status() != null && msg.getRetweeted_status().havePicture());
            checkPic = (checkPic && !SettingUtils.isEnablePic());
            boolean checkGps = (msg.getGeo() != null) && msg.getGeo().getLat() != 0.0f && msg.getGeo().getLon() != 0.0f;

            if (!checkRepostsCount && !checkCommentsCount && !checkPic && !checkGps) {
                holder.count_layout.setVisibility(View.INVISIBLE);
            } else {
                holder.count_layout.setVisibility(View.VISIBLE);

                if (checkPic) {
                    holder.timeline_pic.setVisibility(View.VISIBLE);
                } else {
                    holder.timeline_pic.setVisibility(View.GONE);
                }

                if (checkGps) {
                    holder.timeline_gps.setVisibility(View.VISIBLE);
                } else {
                    holder.timeline_gps.setVisibility(View.INVISIBLE);
                }

                if (checkRepostsCount) {
                    holder.repost_count.setText(String.valueOf(msg.getReposts_count()));
                    holder.repost_count.setVisibility(View.VISIBLE);
                } else {
                    holder.repost_count.setVisibility(View.GONE);
                }

                if (checkCommentsCount) {
                    holder.comment_count.setText(String.valueOf(msg.getComments_count()));
                    holder.comment_count.setVisibility(View.VISIBLE);
                } else {
                    holder.comment_count.setVisibility(View.GONE);
                }
            }
        }

        holder.repost_content.setVisibility(View.GONE);
        holder.repost_content_pic.setVisibility(View.GONE);
        holder.repost_content_pic_multi.setVisibility(View.GONE);

        holder.content_pic.setVisibility(View.GONE);
        holder.content_pic_multi.setVisibility(View.GONE);

        if (msg.havePicture()) {
            if (msg.isMultiPics()) {
                buildMultiPic(msg, holder.content_pic_multi);
            } else {
                buildPic(msg, holder.content_pic);
            }
        }

        MessageBean repost_msg = msg.getRetweeted_status();

        if (repost_msg != null && showOriStatus) {
            // 转发
            if (holder.repost_layout != null) {
                holder.repost_layout.setVisibility(View.VISIBLE);
            }
            holder.repost_flag.setVisibility(View.VISIBLE);
            // sina weibo official account can send repost message with picture,
            // fuck sina weibo
            if (holder.content_pic.getVisibility() != View.GONE) {
                holder.content_pic.setVisibility(View.GONE);
            }
            buildRepostContent(msg, repost_msg, holder);

        } else {
            if (holder.repost_layout != null) {
                holder.repost_layout.setVisibility(View.GONE);
            }
            holder.repost_flag.setVisibility(View.GONE);
        }

        holder.popupMenuIb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), holder.popupMenuIb);
                popupMenu.inflate(R.menu.time_line_popmenu);
                popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem arg0) {
                        int id = arg0.getItemId();
                        switch (id) {
                            case R.id.menu_repost: {
                                Intent intent = new Intent(getActivity(), RepostWeiboWithAppSrcActivity.class);
                                intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
                                intent.putExtra("msg", msg);
                                getActivity().startActivity(intent);
                                break;
                            }
                            case R.id.menu_comment: {
                                Intent intent = new Intent(getActivity(), WriteCommentActivity.class);
                                intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
                                intent.putExtra("msg", msg);
                                getActivity().startActivity(intent);
                                break;
                            }

                            case R.id.menu_fav: {
                                if (Utility.isTaskStopped(favTask)) {
                                    favTask = new FavAsyncTask(BeeboApplication.getInstance().getAccessToken(), msg.getId());
                                    favTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                                }
                                break;
                            }

                            case R.id.menu_unfav: {
                                if (Utility.isTaskStopped(favTask) && Utility.isTaskStopped(unFavTask)) {
                                    unFavTask = new UnFavAsyncTask(BeeboApplication.getInstance().getAccessToken(), msg.getId());
                                    unFavTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                                }
                                return true;
                            }

                            case R.id.menu_copy: {
                                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", msg.getText()));
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

    private void buildRepostContent(MessageBean msg, final MessageBean repost_msg, ViewHolder holder) {
        holder.repost_content.setVisibility(View.VISIBLE);
        if (!repost_msg.getId().equals(holder.repost_content.getTag())) {
            boolean haveCachedHeight = oriMsgHeights.get(msg.getIdLong()) != null;
            ViewGroup.LayoutParams layoutParams = holder.repost_content.getLayoutParams();
            if (haveCachedHeight) {
                layoutParams.height = oriMsgHeights.get(msg.getIdLong());
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            boolean haveCachedWidth = oriMsgWidths.get(msg.getIdLong()) != null;
            if (haveCachedWidth) {
                layoutParams.width = oriMsgWidths.get(msg.getIdLong());
            } else {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            holder.repost_content.requestLayout();
            holder.repost_content.setText(repost_msg.getListViewSpannableString());

            if (!haveCachedHeight) {
                oriMsgHeights.append(msg.getIdLong(), layoutParams.height);
            }

            if (!haveCachedWidth) {
                oriMsgWidths.append(msg.getIdLong(), layoutParams.width);
            }

            holder.repost_content.setText(repost_msg.getListViewSpannableString());
            holder.repost_content.setTag(repost_msg.getId());
        }

        if (repost_msg.havePicture()) {
            if (repost_msg.isMultiPics()) {
                buildMultiPic(repost_msg, holder.repost_content_pic_multi);
            } else {
                buildPic(repost_msg, holder.repost_content_pic);
            }

        }
    }

    public void addNewData(List<MessageBean> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.bean.addAll(0, newValue);

        // remove duplicate null flag, [x,y,null,null,z....]

        ListIterator<MessageBean> listIterator = this.bean.listIterator();

        boolean isLastItemNull = false;
        while (listIterator.hasNext()) {
            MessageBean msg = listIterator.next();
            if (msg == null) {
                if (isLastItemNull) {
                    listIterator.remove();
                }
                isLastItemNull = true;
            } else {
                isLastItemNull = false;
            }
        }
    }
}
