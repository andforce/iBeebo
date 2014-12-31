
package lib.org.zarroboogs.weibo.login.httpclient;

public class WaterMark {
    public static enum POS {
        BOTTOM_RIGHT,
        BOTTOM_CENTER,
        CENTER
    }

    // &marks=1&markpos=1&logo=1&nick=%40andforce&url=weibo.com/u/2294141594
    private String nick = "";
    private String url = "";

    public WaterMark(String nick, String url) {
        this.nick = nick;
        this.url = url;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
