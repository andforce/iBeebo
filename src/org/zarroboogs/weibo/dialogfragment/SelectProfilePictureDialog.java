
package org.zarroboogs.weibo.dialogfragment;

import org.zarroboogs.weibo.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * User: qii Date: 13-3-2
 */
public class SelectProfilePictureDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] items = {
                getString(R.string.take_camera), getString(R.string.select_pic)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.select)).setItems(
                items,
                (DialogInterface.OnClickListener) getActivity());
        return builder.create();
    }

}
