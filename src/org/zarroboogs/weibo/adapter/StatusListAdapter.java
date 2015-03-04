
package org.zarroboogs.weibo.adapter;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.activity.RepostWeiboWithAppSrcActivity;
import org.zarroboogs.weibo.activity.WriteCommentActivity;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.AutoScrollListView;
import org.zarroboogs.weibo.widget.TopTipsView;
import org.zarroboogs.weibo.widget.VelocityListView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class StatusListAdapter extends AbstractAppListAdapter<MessageBean> {

    private WeakHashMap<ViewHolder, Drawable> bg = new WeakHashMap<ViewHolder, Drawable>();

    private LongSparseArray<Integer> msgHeights = new LongSparseArray<Integer>();

    private LongSparseArray<Integer> msgWidths = new LongSparseArray<Integer>();

    private LongSparseArray<Integer> oriMsgHeights = new LongSparseArray<Integer>();

    private LongSparseArray<Integer> oriMsgWidths = new LongSparseArray<Integer>();

    private TopTipsView topTipBar;

    public StatusListAdapter(Fragment fragment, List<MessageBean> bean, ListView listView, boolean showOriStatus) {
        this(fragment, bean, listView, showOriStatus, false);
    }

    public StatusListAdapter(Fragment fragment, List<MessageBean> bean, ListView listView, boolean showOriStatus, boolean pre) {
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
                VelocityListView velocityListView = (VelocityListView) view;
                // if (velocityListView.getVelocity() < 0) {
                // topTipBar.hideCount();
                // } else if (velocityListView.getVelocity() > 0) {
                // if (topTipBar.getValues().size() == 0) {
                // return;
                // }

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

        final MessageBean msg = bean.get(position);
        holder.repostBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RepostWeiboWithAppSrcActivity.class);
                intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("msg", msg);
                getActivity().startActivity(intent);
            }
        });
        holder.commentBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteCommentActivity.class);
                intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("msg", msg);
                getActivity().startActivity(intent);
            }
        });
        holder.giveHeart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Boolean>() {

					@Override
					protected Boolean doInBackground(Void... params) {
						// TODO Auto-generated method stub
				        Map<String, String> map = new HashMap<String, String>();
				        map.put("access_token", GlobalContext.getInstance().getSpecialToken());
				        map.put("id", msg.getId());
				        map.put("attitude", "heart");
						try {
							String likeresult = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, WeiBoURLs.GIVE_HEART, map);
							return true;
						} catch (WeiboException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return false;
						}
					}
					protected void onPostExecute(Boolean result) {
						if (result) {
							Toast.makeText(getActivity(), "点赞成功", Toast.LENGTH_SHORT).show();
						}else {
							Toast.makeText(getActivity(), "点赞失败", Toast.LENGTH_SHORT).show();
						}
						
					};
				}.execute();
			}
		});

        UserBean user = msg.getUser();
        if (user != null) {
            holder.username.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.getRemark())) {
                holder.username.setText(new StringBuilder(user.getScreen_name()).append("(").append(user.getRemark())
                        .append(")").toString());
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
            boolean checkGps = (msg.getGeo() != null);

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
                buildPic(msg, holder.content_pic, position);
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
            buildRepostContent(msg, repost_msg, holder, position);

            if (holder.content_pic_multi != holder.repost_content_pic_multi) {
                interruptPicDownload(holder.content_pic_multi);
            }
            if (repost_msg.getPicCount() < 1) {
                if (holder.content_pic != holder.repost_content_pic) {
                    interruptPicDownload(holder.content_pic);
                }
            }
        } else {
            // 没有转发内容，需要清理转发图片
            if (holder.content_pic_multi != holder.repost_content_pic_multi) {
                interruptPicDownload(holder.repost_content_pic_multi);
            }
            if (holder.repost_content_pic != holder.content_pic) {
                interruptPicDownload(holder.repost_content_pic);
            }
            if (holder.repost_layout != null) {
                holder.repost_layout.setVisibility(View.GONE);
            }
            holder.repost_flag.setVisibility(View.GONE);
        }

        boolean interruptPic = false;
        boolean interruptMultiPic = false;
        boolean interruptRepostPic = false;
        boolean interruptRepostMultiPic = false;

        if (msg.havePicture()) {
            if (msg.isMultiPics()) {
                interruptPic = true;
            } else {
                interruptMultiPic = true;
            }
        }

        if (repost_msg != null && showOriStatus) {

            if (repost_msg.havePicture()) {
                if (repost_msg.isMultiPics()) {
                    interruptRepostPic = true;
                } else {
                    interruptRepostMultiPic = true;
                }

            }
        }

        if (interruptPic && interruptRepostPic) {
            interruptPicDownload(holder.content_pic);
            interruptPicDownload(holder.repost_content_pic);
        }

        if (interruptMultiPic && interruptRepostMultiPic) {
            interruptPicDownload(holder.content_pic_multi);
            interruptPicDownload(holder.repost_content_pic_multi);
        }

        if (interruptPic && !interruptRepostPic) {
            if (holder.content_pic != holder.repost_content_pic) {
                interruptPicDownload(holder.content_pic);
            }
        }

        if (!interruptPic && interruptRepostPic) {
            if (holder.content_pic != holder.repost_content_pic) {
                interruptPicDownload(holder.repost_content_pic);
            }
        }

        if (interruptMultiPic && !interruptRepostMultiPic) {
            if (holder.content_pic_multi != holder.repost_content_pic_multi) {
                interruptPicDownload(holder.content_pic_multi);
            }
        }

        if (!interruptMultiPic && interruptRepostMultiPic) {
            if (holder.content_pic_multi != holder.repost_content_pic_multi) {
                interruptPicDownload(holder.repost_content_pic_multi);
            }
        }
    }

    private void buildRepostContent(MessageBean msg, final MessageBean repost_msg, ViewHolder holder, int position) {
        holder.repost_content.setVisibility(View.VISIBLE);
        if (!repost_msg.getId().equals((String) holder.repost_content.getTag())) {
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
                buildPic(repost_msg, holder.repost_content_pic, position);
            }

        }
    }

}
