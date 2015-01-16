
package lib.org.zarroboogs.weibo.login.utils;

public class Constaces {
    // 第一次Get请求返回的数据
    public static final String retcode = "retcode";
    public static final String servertime = "servertime";
    public static final String pcid = "pcid";
    public static final String nonce = "nonce";
    public static final String pubkey = "pubkey";
    public static final String rsakv = "rsakv";
    public static final String exectime = "exectime";

    public static final boolean isMobile = false;

    public static final String SSOLOGIN_JS = "ssologin.js(v1.4.18)";
    public static final String LOGIN_FIRST_URL = "http://login.sina.com.cn/sso/login.php?client=" + SSOLOGIN_JS;

    public static final String User_Agent = "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    // "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36";

    public static final String ADDBLOGURL = "http://widget.weibo.com/public/aj_addMblog.php";
    public static final String REPOST_WEIBO = "http://widget.weibo.com/public/aj_repost.php";

    public static final int MSG_ENCODE_PWD = 0x0002;
    public static final int MSG_ENCODE_PWD_DONW = 0x0003;
    public static final int MSG_AFTER_LOGIN_DONE = 0x0004;
    public static final int MSG_LONGIN_SUCCESS = 0x0005;
    public static final int MSG_SHOW_DOOR = 0x0006;
    
    public static final int MSG_ENCODE_PWD_ERROR = 0x0007;
}
