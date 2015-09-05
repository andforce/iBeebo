
package org.zarroboogs.weibo.bean;

public class MessageTimeLineData {
    public MessageListBean msgList;
    public TimeLinePosition position;
    public String groupId;

    public MessageTimeLineData(String groupId, MessageListBean msgList, TimeLinePosition position) {
        this.groupId = groupId;
        this.msgList = msgList;
        this.position = position;
    }
}
