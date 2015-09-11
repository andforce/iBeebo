
package org.zarroboogs.weibo;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.support.utils.AppConfig;
import org.zarroboogs.weibo.support.utils.Utility;

public class WordLengthLimitWatcher implements TextWatcher {

    private EditText editText;

    public WordLengthLimitWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (Utility.length(s.toString()) > AppConfig.CREATE_MODIFY_FRIEND_GROUP_NAME_LENGTH_LIMIT) {
            editText.setError(editText.getContext().getString(R.string.group_name_must_less_than_ten_chinese_words));
        }
    }
}