
package org.zarroboogs.util.net;

public class LoginWeiboAsyncTask{

    public static interface LoginWeiboCallack {
        public void onLonginWeiboCallback(boolean isSuccess);
    }

    public LoginWeiboCallack mInSuccessListener;

    private String mCookie = "";

    public LoginWeiboAsyncTask(LoginWeiboCallack listener, String cookie) {
        // TODO Auto-generated constructor stub
        this.mInSuccessListener = listener;
        this.mCookie = cookie;
    }

}
