
package org.zarroboogs.weibo.dialogfragment;

import org.zarroboogs.weibo.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SelectPictureDialog extends DialogFragment {

    public static SelectPictureDialog newInstance() {
        return new SelectPictureDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] items = {
                getString(R.string.get_the_last_picture), getString(R.string.take_camera), getString(R.string.select_pic)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.select)).setItems(
                items,
                (DialogInterface.OnClickListener) getActivity());
        return builder.create();
    }
}
