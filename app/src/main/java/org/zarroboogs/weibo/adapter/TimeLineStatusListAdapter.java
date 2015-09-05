package org.zarroboogs.weibo.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.devutils.http.request.HeaderList;
import org.zarroboogs.senior.sdk.SeniorUrl;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.ImageLoader;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.RepostWeiboWithAppSrcActivity;
import org.zarroboogs.weibo.activity.UserInfoActivity;
import org.zarroboogs.weibo.activity.WriteCommentActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dialogfragment.UserDialog;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.IWeiboDrawable;
import org.zarroboogs.weibo.support.gallery.GalleryAnimationActivity;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.lib.ClickableTextViewMentionLinkOnTouchListener;
import org.zarroboogs.weibo.support.utils.TimeLineUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.ui.task.FavAsyncTask;
import org.zarroboogs.weibo.ui.task.UnFavAsyncTask;
import org.zarroboogs.weibo.widget.TimeLineAvatarImageView;
import org.zarroboogs.weibo.widget.TimeTextView;
import org.zarroboogs.weibo.widget.TopTipsView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by andforce on 15/9/2.
 */
public class TimeLineStatusListAdapter extends BaseAdapter {

    private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
    private LongSparseArray<Integer> msgHeights = new LongSparseArray<>();
    private LongSparseArray<Integer> msgWidths = new LongSparseArray<>();
    private LongSparseArray<Integer> oriMsgHeights = new LongSparseArray<>();
    private LongSparseArray<Integer> oriMsgWidths = new LongSparseArray<>();

    private FavAsyncTask favTask = null;
    private UnFavAsyncTask unFavTask = null;

    private ArrayList<MessageBean> mMessageBeans;
    private Fragment mFragment;

    public TimeLineStatusListAdapter(Fragment fragment, ArrayList<MessageBean> messageBeans){
        this.mFragment = fragment;
        this.mMessageBeans = messageBeans;
    }

    @Override
    public int getCount() {
        return mMessageBeans.size();
    }

    @Override
    public MessageBean getItem(int position) {
        return mMessageBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public Activity getActivity(){
        return mFragment.getActivity();
    }

    public Fragment getFragment(){
        return mFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.timeline_status_list_item_layout, parent, false);
            holder = new ViewHolder();
            
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
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindOnTouchListener(holder);

        bindViewData(holder, mMessageBeans.get(position));
        
        return convertView;
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

    protected void bindViewData(final ViewHolder holder, final MessageBean msg) {

        final AccountBean accountBean = BeeboApplication.getInstance().getAccountBean();

        holder.popupMenuIb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), holder.popupMenuIb);
                popupMenu.inflate(R.menu.time_line_popmenu);

                if (TextUtils.isEmpty(accountBean.getGsid())) {
                    popupMenu.getMenu().removeItem(R.id.menu_like);
                    popupMenu.getMenu().removeItem(R.id.menu_unlike);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem arg0) {
                        int id = arg0.getItemId();
                        switch (id) {
                            case R.id.menu_like: {
                                like(accountBean.getGsid(), msg.getId());
                                break;
                            }
                            case R.id.menu_unlike: {
                                unlike(accountBean.getGsid(), msg.getId());
                                break;
                            }
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


        UserBean user = msg.getUser();
        if (user != null) {
            holder.username.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.getRemark())) {
                holder.username.setText(user.getScreen_name() + "(" + user.getRemark() + ")");
            } else {
                holder.username.setText(user.getScreen_name());
            }

            buildAvatar(holder.avatar, user);

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

        if (repost_msg != null) {
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
            // 没有转发内容，需要清理转发图片
            if (holder.repost_layout != null) {
                holder.repost_layout.setVisibility(View.GONE);
            }
            holder.repost_flag.setVisibility(View.GONE);
        }
    }


    public void like(String gsid, String id) {

        AccountBean accountBean = BeeboApplication.getInstance().getAccountBean();


        String url = SeniorUrl.like(id);//.like(gsid,id, ua);//WeiBoURLs.like(gsid, id);
        DevLog.printLog("Like_doInBackground", "" + url);

        String cookie = SeniorUrl.geCookie(gsid, accountBean.getUsernick());
        HeaderList headerList = new HeaderList();
        headerList.addHost("m.weibo.cn");
        headerList.addAccept("application/json, text/javascript, */*; q=0.01");
        headerList.addOrigin("http://m.weibo.cn");
        headerList.addHeader("X-Requested-With", "XMLHttpRequest");
        headerList.addUserAgent("Mozilla/5.0 (Linux; Android 5.0.1; Nexus 5 Build/LRX22C) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.96 Mobile Safari/537.36");
        headerList.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headerList.addReferer("http://m.weibo.cn/");
        headerList.addAcceptEncoding("gzip, deflate");
        headerList.addAcceptLanguage("zh-CN,zh;q=0.8");
        headerList.addHeader("Cookie", cookie);
        DevLog.printLog("Like_doInBackground", "" + cookie);

        mAsyncHttpClient.get(getActivity(), url, headerList.build(), null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                String result = new String(arg2);

                if ("{\"ok\":1,\"msg\":\"succ\"}".equals(result)) {
                    Toast.makeText(getActivity(), "点赞成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "点赞失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                Toast.makeText(getActivity(), "点赞失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unlike(String gsid, String id) {

        AccountBean accountBean = BeeboApplication.getInstance().getAccountBean();

        String url = SeniorUrl.unlike(id);//WeiBoURLs.like(gsid, id);

        DevLog.printLog("Like_doInBackground", "" + url);

        String cookie = SeniorUrl.geCookie(gsid, accountBean.getUsernick());
        HeaderList headerList = new HeaderList();
        headerList.addHost("m.weibo.cn");
        headerList.addAccept("application/json, text/javascript, */*; q=0.01");
        headerList.addOrigin("http://m.weibo.cn");
        headerList.addHeader("X-Requested-With", "XMLHttpRequest");
        headerList.addUserAgent("Mozilla/5.0 (Linux; Android 5.0.1; Nexus 5 Build/LRX22C) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.96 Mobile Safari/537.36");
        headerList.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headerList.addReferer("http://m.weibo.cn/");
        headerList.addAcceptEncoding("gzip, deflate");
        headerList.addAcceptLanguage("zh-CN,zh;q=0.8");
        headerList.addHeader("Cookie", cookie);
        DevLog.printLog("Like_doInBackground", "" + cookie);

        mAsyncHttpClient.get(getActivity(), url, headerList.build(), null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                String result = new String(arg2);
                DevLog.printLog("Like_doInBackground", " Like CallBack:" + result);

                if ("{\"ok\":1,\"msg\":\"succ\",\"data\":{\"result\":true}}".equals(result)) {
                    Toast.makeText(getActivity(), "取消点赞成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "取消点赞失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "取消点赞失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void buildMultiPic(final MessageBean msg, final GridLayout gridLayout) {
        if (SettingUtils.isEnablePic()) {
            gridLayout.setVisibility(View.VISIBLE);

            final int count = msg.getPicCount();
            for (int i = 0; i < count; i++) {
                final IWeiboDrawable pic = (IWeiboDrawable) gridLayout.getChildAt(i);
                pic.setVisibility(View.VISIBLE);

                String avatar = SettingUtils.getEnableBigAvatar() ? msg.getHighPicUrls().get(i) : msg.getThumbnailPicUrls().get(i);
                ImageLoader.load(getFragment(), avatar, pic.getImageView());

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


    protected void buildAvatar(IWeiboDrawable view,final UserBean user) {
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
                dialog.show(getFragment().getFragmentManager(), "");
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
            ImageLoader.load(getFragment(), image_url, view);
        } else {
            view.setVisibility(View.GONE);
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

            String avatar = SettingUtils.getEnableBigPic() ? msg.getOriginal_pic() : msg.getThumbnail_pic();

            ImageLoader.load(getFragment(), avatar, view.getImageView());


        } else {
            view.setVisibility(View.GONE);
        }
    }


    private void buildRepostContent(MessageBean msg, final MessageBean repost_msg, ViewHolder holder) {
        holder.repost_content.setVisibility(View.VISIBLE);
        if (repost_msg != null && !repost_msg.getId().equals(holder.repost_content.getTag())) {
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

        if (repost_msg != null) {
            if (repost_msg.havePicture()) {
                if (repost_msg.isMultiPics()) {
                    buildMultiPic(repost_msg, holder.repost_content_pic_multi);
                } else {
                    buildPic(repost_msg, holder.repost_content_pic);
                }

            }
        }
    }


    public void setTopTipBar(final TopTipsView topTipBar, final ListView listView) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                if (position > 0 && topTipBar != null && position < mMessageBeans.size()) {
                    MessageBean next = mMessageBeans.get(position);
                    if (next != null) {
                        MessageBean helperMsg = mMessageBeans.get(position - 1);
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

    public void removeItem(final int postion, ListView listView) {
        if (postion >= 0 && postion < getCount()) {
            AppLoggerUtils.e("1");
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.account_delete_slide_out_right);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                    AppLoggerUtils.e("4");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mMessageBeans.remove(postion);
                    notifyDataSetChanged();
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
                mMessageBeans.remove(postion);
                notifyDataSetChanged();
                AppLoggerUtils.e("3");
            }

        }
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


    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return listener.onTouch(v, event);
        }
    };

    public void addNewData(List<MessageBean> newValue) {

        if (newValue == null || newValue.size() == 0) {
            return;
        }

        this.mMessageBeans.addAll(0, newValue);

        // remove duplicate null flag, [x,y,null,null,z....]

        ListIterator<MessageBean> listIterator = this.mMessageBeans.listIterator();

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
