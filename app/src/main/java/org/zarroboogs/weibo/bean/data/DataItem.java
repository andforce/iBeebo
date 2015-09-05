
package org.zarroboogs.weibo.bean.data;

import org.zarroboogs.weibo.bean.UserBean;

import android.text.SpannableString;

public abstract class DataItem {
    public abstract SpannableString getListViewSpannableString();

    public abstract String getListviewItemShowTime();

    public abstract String getText();

    public abstract String getCreated_at();

    public abstract void setMills(long mills);

    public abstract long getMills();

    public abstract String getId();

    public abstract long getIdLong();

    public abstract UserBean getUser();

    @Override
    public boolean equals(Object o) {
        return o instanceof DataItem && ((DataItem) o).getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
