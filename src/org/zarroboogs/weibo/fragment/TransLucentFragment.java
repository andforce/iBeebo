
package org.zarroboogs.weibo.fragment;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TransLucentFragment extends Fragment {

    public static void setInsets(Activity context, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        SystemBarTintManager.SystemBarConfig config = tintManager
                .getConfig();
        view.setPadding(0, config.getPixelInsetTop(true),
                config.getPixelInsetRight(), config.getPixelInsetBottom());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setInsets(getActivity(), container);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
