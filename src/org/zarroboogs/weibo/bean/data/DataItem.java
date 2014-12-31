package org.zarroboogs.weibo.bean.data;

import org.zarroboogs.weibo.bean.UserBean;

import android.text.SpannableString;

/**
 * User: qii Date: 12-9-6
 */
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
		if (o instanceof DataItem && ((DataItem) o).getId().equals(getId())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
