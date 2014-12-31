package lib.org.zarroboogs.weibo.login.javabean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import android.text.TextUtils;
import android.util.Log;

public class RequestResultParser {

	private boolean mIsLogin = false;
	private String mErrorReason = "";
	private String mResponseString;
	private String mLocationReplace;
	
	
	
	public String getLocationReplace() {
        return mLocationReplace;
    }

    public void setLocationReplace(String mLocationReplace) {
        this.mLocationReplace = mLocationReplace;
    }

    private Gson mGson;

	public RequestResultParser() {
        mGson = new Gson();
    }
	
    public <T extends Object> T parse(byte[] response, Class<T> bean) {
        if (response == null) {
            return null;
        }
        String responseString = new String(response);
        if (TextUtils.isEmpty(responseString)) {
            return null;
        }
        if (mGson == null) {
        	mGson = new Gson();
		}
        return mGson.fromJson(responseString, bean);
    }
	   public RequestResultParser(String entity) {
	        try {
//	            String entityString = EntityUtils.toString(entity, "GBK");
	            
	            String result = URLDecoder.decode(entity, "GBK");
	            result = URLDecoder.decode(entity, "GBK");
	            
	            Log.d("LoginResultHelper ", "" + result);
	            //http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack&sudaref=widget.weibo.com&
	            //retcode=4038&reason=%B5%C7%C2%BC%B4%CE%CA%FD%B9%FD%B6%E0%A1%A3%3Ca%20href%3D%22http%3A%2F%2Fhelp.weibo.com%2Ffaq%2Fq%2F85%2F12699%2312699%22%20target%3D%22_blank%22%3E%B2%E9%BF%B4%B0%EF%D6%FA%3C%2Fa%3E&#39;"/>
	            if (result.contains("\"retcode\":0")) {
	                mIsLogin = true;
	            } else if (result
	                    .contains("reason=为了您的帐号安全，请输入验证码")) {
	                // 为了您的帐号安全，请输入验证码
	                mIsLogin = false;
	                mErrorReason = "为了您的帐号安全，请输入验证码";
	                //location.replace("http://widget.weibo.com/public/aj_login.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack&sudaref=widget.weibo.com&retcode=4049&reason=%CE%AA%C1%CB%C4%FA%B5%C4%D5%CA%BA%C5%B0%B2%C8%AB%A3%AC%C7%EB%CA%E4%C8%EB%D1%E9%D6%A4%C2%EB");
//	                </script>
	            } else if (result.contains("登录名或密码错误")) {
	                // 用户名或密码错误
	                mIsLogin = false;
	                mErrorReason = "登录名或密码错误";
	            } else if (result.contains("输入的验证码不正确")) {
	                mIsLogin = false;
	                mErrorReason = "输入的验证码不正确";
	            } else if (result.contains("请输入正确的密码")) {
	                mIsLogin = false;
	                mErrorReason = "请输入正确的密码";
	            } else if (result.contains("抱歉！登录失败，请稍候再试")) {
	                mIsLogin = false;
                    mErrorReason = "抱歉！登录失败，请稍候再试";
                }
                String tmp = result.split("location.replace\\(")[1];
                String tmp2 = tmp.split("\\);")[0];
                
                mLocationReplace = tmp2.replace("\"", "");
                
	            this.mResponseString = result;
	        } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	   
	public RequestResultParser(HttpEntity entity) {
		try {
			String entityString = EntityUtils.toString(entity, "GBK");
			
			String result = URLDecoder.decode(entityString, "GBK");
			result = URLDecoder.decode(entityString, "GBK");
			
			Log.d("LoginResultHelper ", "" + result);
			//http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack&sudaref=widget.weibo.com&
			//retcode=4038&reason=%B5%C7%C2%BC%B4%CE%CA%FD%B9%FD%B6%E0%A1%A3%3Ca%20href%3D%22http%3A%2F%2Fhelp.weibo.com%2Ffaq%2Fq%2F85%2F12699%2312699%22%20target%3D%22_blank%22%3E%B2%E9%BF%B4%B0%EF%D6%FA%3C%2Fa%3E&#39;"/>
			if (result.contains("\"retcode\":0")) {
				mIsLogin = true;
			} else if (result
					.contains("reason=为了您的帐号安全，请输入验证码")) {
				// 为了您的帐号安全，请输入验证码
				mIsLogin = false;
				mErrorReason = "为了您的帐号安全，请输入验证码";
			} else if (result.contains("登录名或密码错误")) {
				// 用户名或密码错误
				mIsLogin = false;
				mErrorReason = "登录名或密码错误";
			} else if (result.contains("输入的验证码不正确")) {
				mIsLogin = false;
				mErrorReason = "输入的验证码不正确";
			} else if (result.contains("请输入正确的密码")) {
			    mIsLogin = false;
                mErrorReason = "请输入正确的密码";
            }
			this.mResponseString = result;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isLogin() {
		return mIsLogin;
	}

	public String getErrorReason() {
		return mErrorReason;
	}

	public String getResponseString() {
		return this.mResponseString;
	}

	public String getUserPageUrl() {
		if (mIsLogin) {
			String regx = "url=";
			String tmp = getResponseString().split(regx)[1];
			String url = tmp.split("retcode=0")[0] + "retcode=0";
			try {
				url = URLDecoder.decode(url, "GBK");
				url = URLDecoder.decode(url, "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return url;
		}
		return null;
	}



}
