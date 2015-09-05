
package org.zarroboogs.weibo.dao;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * http://open.weibo.com/wiki/2/account/avatar/upload
 */
public class UploadAvatarDao {

    public boolean upload() throws WeiboException {
        String url = WeiBoURLs.AVATAR_UPLOAD;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        return HttpUtility.getInstance().executeUploadTask(url, map, image, "image", null);
    }

    private String access_token;
    private String image;

    public UploadAvatarDao(String token, String image) {
        this.access_token = token;
        this.image = image;
    }
}
