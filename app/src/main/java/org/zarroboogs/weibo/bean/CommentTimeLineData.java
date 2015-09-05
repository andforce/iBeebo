
package org.zarroboogs.weibo.bean;

public class CommentTimeLineData {
    public CommentListBean cmtList;
    public TimeLinePosition position;

    public CommentTimeLineData(CommentListBean cmtList, TimeLinePosition position) {
        this.cmtList = cmtList;
        this.position = position;
    }
}
