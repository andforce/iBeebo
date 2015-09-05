
package org.zarroboogs.weibo.bean;

import java.io.Serializable;
import java.util.TreeSet;

public class TimeLinePosition implements Serializable {

	private static final long serialVersionUID = -8442914085483115385L;

	public TimeLinePosition(int position, int top) {
        this.position = position;
        this.top = top;
    }

    public int position = 0;

    public int top = 0;

    public TreeSet<Long> newMsgIds = null;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder().append("position:").append(position).append("; top=").append(top);
        return stringBuilder.toString();
    }
}
