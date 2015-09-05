
package org.zarroboogs.weibo.bean;

public class MyStatusTimeLineData {

    public MessageListBean msgList;
    public TimeLinePosition position;

    public MyStatusTimeLineData(MessageListBean msgList, TimeLinePosition position) {
        this.msgList = msgList;
        this.position = position;
    }
}
