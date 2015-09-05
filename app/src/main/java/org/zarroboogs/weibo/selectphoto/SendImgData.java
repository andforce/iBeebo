
package org.zarroboogs.weibo.selectphoto;

import java.util.ArrayList;
import java.util.List;

public class SendImgData {
    private static ArrayList<String> sendImgList = new ArrayList<String>();
    private static SendImgData mSendImgData;

    private SendImgData() {
    }

    public static SendImgData getInstance() {
        if (mSendImgData == null) {
            mSendImgData = new SendImgData();
        }
        return mSendImgData;
    }

    public ArrayList<String> getSendImgs() {
        return sendImgList;
    }

    public void clearSendImgs() {
        sendImgList.clear();
    }

    public void addSendimg(List<String> newDatas) {
        sendImgList.addAll(newDatas);
    }

    public void addSendImg(String newData) {
        sendImgList.add(newData);
    }

    public void removeSendImg(String newData) {
        sendImgList.remove(newData);
    }

}
