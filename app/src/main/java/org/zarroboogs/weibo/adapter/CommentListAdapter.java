
package org.zarroboogs.weibo.adapter;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.activity.RepostWeiboWithAppSrcActivity;
import org.zarroboogs.weibo.activity.WriteCommentActivity;
import org.zarroboogs.weibo.activity.WriteReplyToCommentActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.ui.task.FavAsyncTask;
import org.zarroboogs.weibo.widget.AutoScrollListView;
import org.zarroboogs.weibo.widget.TopTipsView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CommentListAdapter extends AbstractAppListAdapter<CommentBean> {

    private Map<ViewHolder, Drawable> bg = new WeakHashMap<>();

    private TopTipsView topTipBar;

    public CommentListAdapter(Fragment fragment, List<CommentBean> bean, ListView listView, boolean showOriStatus,
            boolean pref) {
        super(fragment, bean, listView, showOriStatus, pref);

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
            }

            private void handle(int position) {
                if (position > 0 && topTipBar != null && position < bean.size()) {
                    CommentBean next = bean.get(position);
                    if (next != null) {
                        CommentBean helperMsg = bean.get(position - 1);
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
    protected void bindViewData(final ViewHolder holder, final int position) {

        Drawable drawable = bg.get(holder);
        if (drawable != null) {
            holder.listview_root.setBackgroundDrawable(drawable);

        } else {
            drawable = holder.listview_root.getBackground();
            bg.put(holder, drawable);
        }

        if (listView.getCheckedItemPosition() == position + 1) {
            holder.listview_root.setBackgroundColor(checkedBG);
        }

        final CommentBean comment = getList().get(position);

        if (holder.popupMenuIb != null) {
            holder.popupMenuIb.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				PopupMenu popupMenu = new PopupMenu(getActivity(), holder.popupMenuIb);
    				popupMenu.inflate(R.menu.comments_to_me_popmenu);
    				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
    					
    					@Override
    					public boolean onMenuItemClick(MenuItem arg0) {
    						// TODO Auto-generated method stub
    						int id = arg0.getItemId();
    						switch (id) {
    						case R.id.reply_comment_menu:{
    							
    							Intent intent = new Intent(getActivity(), WriteReplyToCommentActivity.class);
    	                        intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
    	                        intent.putExtra("msg", comment);
    	                        getActivity().startActivity(intent);
    							break;
    						}
    						case R.id.view_weibo_menu:{
    							getActivity().startActivity(BrowserWeiboMsgActivity.newIntent(BeeboApplication.getInstance().getAccountBean(),
    									comment.getStatus(), BeeboApplication.getInstance().getAccessToken()));
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

        
        UserBean user = comment.getUser();
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

        holder.weiboTextContent.setText(comment.getListViewSpannableString());

        holder.time.setTime(comment.getMills());
        if (holder.comment_source != null) {
            holder.comment_source.setText(comment.getSourceString());
        }

        if (holder.source != null) {
        	holder.source.setText(comment.getSourceString());
		}
        
        holder.repost_content.setVisibility(View.GONE);
        holder.repost_content_pic.setVisibility(View.GONE);

        
        
        final CommentBean reply = comment.getReply_comment();
        if (holder.cmmentsReply != null) {
            holder.cmmentsReply.setVisibility(View.GONE);
        }
        
        if (reply != null && showOriStatus) {
            if (holder.repost_layout != null) {
                holder.repost_layout.setVisibility(View.VISIBLE);
            }
            holder.repost_flag.setVisibility(View.VISIBLE);
            holder.repost_content.setVisibility(View.VISIBLE);
            holder.repost_content.setText(reply.getListViewSpannableString());
            holder.repost_content.setTag(reply.getId());
            
        } else {

            MessageBean repost_msg = comment.getStatus();

            if (repost_msg != null && showOriStatus) {
                buildOriWeiboContent(repost_msg, holder, position);
            } else {
                if (holder.repost_layout != null) {
                    holder.repost_layout.setVisibility(View.GONE);
                }
                holder.repost_flag.setVisibility(View.GONE);
                if (holder.cmmentsReply != null) {
                    holder.cmmentsReply.setVisibility(View.VISIBLE);
                    holder.cmmentsReply.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu popupMenu = new PopupMenu(getActivity(), holder.cmmentsReply);
                            popupMenu.inflate(R.menu.comments_popmenu);
                            popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
								
								@Override
								public boolean onMenuItemClick(MenuItem arg0) {
									// TODO Auto-generated method stub
									int id = arg0.getItemId();
									switch (id) {
									case R.id.reply_comment:{
										replyComment(reply);
										break;
									}
									case R.id.menu_copy:{
										ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                        if (reply != null) {
                                            cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", reply.getText()));
                                        }
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
            }

        }

    }
    
	private void replyComment(final CommentBean comment) {
		Intent intent = new Intent(getActivity(), WriteReplyToCommentActivity.class);
        intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessToken());
        intent.putExtra("msg", comment);
        getActivity().startActivity(intent);
	}

    protected void buildOriWeiboContent(final MessageBean oriWeibo, ViewHolder holder, int position) {

        holder.repost_content.setVisibility(View.VISIBLE);

        holder.repost_content_pic.setVisibility(View.GONE);
        holder.repost_content_pic_multi.setVisibility(View.GONE);

        holder.content_pic.setVisibility(View.GONE);
        holder.content_pic_multi.setVisibility(View.GONE);

        holder.repost_content.setText(oriWeibo.getListViewSpannableString());

        if (oriWeibo.havePicture()) {
            if (oriWeibo.isMultiPics()) {
                buildMultiPic(oriWeibo, holder.repost_content_pic_multi);
            } else {
                buildPic(oriWeibo, holder.repost_content_pic);
            }

        }

    }

}
