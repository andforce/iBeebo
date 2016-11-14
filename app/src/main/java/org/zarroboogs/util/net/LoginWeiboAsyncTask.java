
package org.zarroboogs.util.net;

public class LoginWeiboAsyncTask {

    public interface LoginWeiboCallack {
        void onLonginWeiboCallback(boolean isSuccess);
    }

    public LoginWeiboCallack mInSuccessListener;

    private String mCookie = "";

    public LoginWeiboAsyncTask(LoginWeiboCallack listener, String cookie) {
        // TODO Auto-generated constructor stub
        this.mInSuccessListener = listener;
        this.mCookie = cookie;
    }

}
