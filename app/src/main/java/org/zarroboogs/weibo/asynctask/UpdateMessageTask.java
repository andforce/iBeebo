
package org.zarroboogs.weibo.asynctask;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.ErrorCode;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.dao.ShowStatusDao;
import org.zarroboogs.weibo.fragment.BrowserWeiboMsgFragment;
import org.zarroboogs.weibo.support.utils.Utility;

public class UpdateMessageTask extends MyAsyncTask<Void, Void, MessageBean> {
    private MessageBean msg;
    private TextView content;
    private TextView recontent;
    private BrowserWeiboMsgFragment fragment;
    private WeiboException e;
    private boolean refreshPic;

    public UpdateMessageTask(BrowserWeiboMsgFragment fragment, TextView content, TextView recontent, MessageBean msg,
                             boolean refreshPic) {
        this.fragment = fragment;
        this.content = content;
        this.recontent = recontent;
        this.msg = msg;
        this.refreshPic = refreshPic;
    }

    private Activity getActivity() {
        return fragment.getActivity();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected MessageBean doInBackground(Void... params) {
        try {
            return new ShowStatusDao(BeeboApplication.getInstance().getAccessTokenHack(), msg.getId()).getMsg();
        } catch (WeiboException e) {
            this.e = e;
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onCancelled(MessageBean weiboMsgBean) {
        super.onCancelled(weiboMsgBean);
        if (Utility.isAllNotNull(getActivity(), this.e)) {
            Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();
            DevLog.printLog("BrowserWeiboMsgFragment", "UpdateMessageTask onCancelled: " + e.getError());
            if (e.getError_code() == ErrorCode.DELETED) {
                setTextViewDeleted();
            }
        }
    }

    // sometime, onPostExecute method is executed after fragment is onDestroy(),
    // you must check activity status
    @Override
    protected void onPostExecute(MessageBean newValue) {
        if (fragment.getActivity() == null)
            return;

        if (newValue != null && e == null) {
            if (isStatusDeleted(newValue)) {
                setTextViewDeleted(content);
                if (recontent.getVisibility() == View.VISIBLE) {
                    setTextViewDeleted(recontent);
                }
            } else if (isRepostDeleted(newValue)) {
                setTextViewDeleted(recontent);
            } else {
                msg = newValue;
                fragment.buildViewData(refreshPic);
                Intent intent = new Intent();
                intent.putExtra("msg", msg);
                getActivity().setResult(0, intent);
            }
        }
        super.onPostExecute(newValue);
    }

    // sometime status is deleted
    private boolean isStatusDeleted(MessageBean newValue) {

        // status is deleted
        if ((msg != null))
            if ((msg.getUser() != null) && (newValue.getUser() == null)) {
                return true;
            }

        return false;

    }

    // sometime the ori status is deleted
    private boolean isRepostDeleted(MessageBean newValue) {

        if (msg.getRetweeted_status() != null && msg.getRetweeted_status().getUser() != null) {

            // ori status is deleted
            if (newValue.getRetweeted_status() != null && newValue.getRetweeted_status().getUser() == null) {
                return true;
            }
        }

        return false;

    }

    private void setTextViewDeleted() {
        SpannableString ss = SpannableString.valueOf(content.getText());
        ss.setSpan(new StrikethroughSpan(), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(ss);
    }

    private void setTextViewDeleted(TextView tv) {
        SpannableString ss = SpannableString.valueOf(tv.getText());
        ss.setSpan(new StrikethroughSpan(), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
    }
}
