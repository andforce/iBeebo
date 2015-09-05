
package org.zarroboogs.weibo.setting.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.setting.fragment.AbstractFilterFragment;

@SuppressLint("ValidFragment")
public class ModifyFilterDialog extends DialogFragment {

    private String word;

    public ModifyFilterDialog() {

    }

    public ModifyFilterDialog(String word) {
        this.word = word;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText et = new EditText(getActivity());
        et.setText(word);
        et.setSelection(et.getText().toString().length());
        builder.setView(et).setTitle(getString(R.string.modify_filter_word))
                .setPositiveButton(getString(R.string.modify), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = et.getText().toString().trim();
                        if (!TextUtils.isEmpty(word)) {
                            AbstractFilterFragment filterFragment = (AbstractFilterFragment) getTargetFragment();
                            filterFragment.modifyFilter(word, newValue);
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }
}
