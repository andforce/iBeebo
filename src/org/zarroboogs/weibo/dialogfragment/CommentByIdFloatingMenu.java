
package org.zarroboogs.weibo.dialogfragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.BrowserCommentActivity;
import org.zarroboogs.weibo.activity.WriteReplyToCommentActivity;
import org.zarroboogs.weibo.bean.CommentBean;

/**
 * User: qii Date: 12-12-6
 */
@SuppressLint("ValidFragment")
public class CommentByIdFloatingMenu extends DialogFragment {

    private CommentBean bean;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BEAN, bean);
    }

    public CommentByIdFloatingMenu() {

    }

    public CommentByIdFloatingMenu(CommentBean bean) {
        this.bean = bean;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            bean = (CommentBean) savedInstanceState.getParcelable(Constants.BEAN);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(this.bean.getUser().getScreen_name());
        String[] str = {
                getString(R.string.reply_to_comment), getString(R.string.view_comment)
        };
        builder.setItems(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                    case 0:
                        intent = new Intent(getActivity(), WriteReplyToCommentActivity.class);
                        intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getAccessToken());
                        intent.putExtra("msg", bean);
                        getActivity().startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), BrowserCommentActivity.class);
                        intent.putExtra("comment", bean);
                        intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getAccessToken());
                        getActivity().startActivity(intent);
                        break;
                }

            }
        });

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
