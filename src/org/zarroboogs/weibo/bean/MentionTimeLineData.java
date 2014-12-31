package org.zarroboogs.weibo.bean;

/**
 * User: qii Date: 13-4-8
 */
public class MentionTimeLineData {

	public MessageListBean msgList;
	public TimeLinePosition position;

	public MentionTimeLineData(MessageListBean msgList, TimeLinePosition position) {
		this.msgList = msgList;
		this.position = position;
	}
}
