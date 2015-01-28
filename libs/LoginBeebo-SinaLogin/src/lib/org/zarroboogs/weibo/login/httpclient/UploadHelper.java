
package lib.org.zarroboogs.weibo.login.httpclient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lib.org.zarroboogs.weibo.login.javabean.UploadPicResult;
import lib.org.zarroboogs.weibo.login.utils.Constaces;
import lib.org.zarroboogs.weibo.login.utils.LogTool;
import lib.org.zarroboogs.weibo.login.utils.PatternUtils;

import org.apache.http.Header;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHeader;
import org.zarroboogs.utils.http.HeaderList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UploadHelper {
    private Context mContext;
    private AsyncHttpClient mAsyncHttpClient;

    public UploadHelper(Context context, AsyncHttpClient asyncHttpClient) {
        this.mContext = context;
        this.mAsyncHttpClient = asyncHttpClient;
    }

    public static final int MSG_UPLOAD = 0x1000;
    public static final int MSG_UPLOAD_DONE = 0x1001;
    public static final int MSG_UPLOAD_FAILED = 0x1002;

    public int mHasUploadFlag = -1;

    private String mPids = "";
    private List<String> mNeedToUpload = new ArrayList<String>();

    private OnUpFilesListener mOnUpFilesListener;
    private String mWaterMark;

    public static interface OnUpFilesListener {
        public void onUpSuccess(String pids);

        public void onUpLoadFailed();
    }

    public void uploadFiles(String waterMark, List<String> files, OnUpFilesListener listener) {
        this.mNeedToUpload = files;
        this.mOnUpFilesListener = listener;
        mHasUploadFlag = 0;
        this.mWaterMark = waterMark;
        mHandler.sendEmptyMessage(MSG_UPLOAD);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD: {
                    uploadFile(mWaterMark, mNeedToUpload.get(mHasUploadFlag));
                    break;
                }
                case MSG_UPLOAD_DONE: {
                    if (mOnUpFilesListener != null) {
                        mOnUpFilesListener.onUpSuccess(mPids);
                    }
                    break;
                }
                case MSG_UPLOAD_FAILED: {
                    if (mOnUpFilesListener != null) {
                        mOnUpFilesListener.onUpLoadFailed();
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    private void uploadFile(String waterMark, String file) {
        // "/sdcard/tencent/zebrasdk/Photoplus.jpg"

		HeaderList headerList = new HeaderList();

		headerList.add(new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
		headerList.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
		headerList.add(new BasicHeader("Accept-Language", "en-US,en;q=0.8"));
		headerList.add(new BasicHeader("Cache-Control", "max-age=0"));
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		headerList.add(new BasicHeader("Content-Type","application/octet-stream"));
		headerList.add(new BasicHeader("Host", "picupload.service.weibo.com"));
		headerList.add(new BasicHeader("Origin", "http://weibo.com"));
		headerList.add(new BasicHeader("User-Agent", Constaces.User_Agent));
		headerList.add(new BasicHeader("Referer","http://tjs.sjs.sinajs.cn/open/widget/static/swf/MultiFilesUpload.swf?version=1411256448572"));
        final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
        
        
        File uploadFile = new File(file);
        FileEntity reqEntity = new FileEntity(uploadFile, "binary/octet-stream");

        String markUrl = "http://picupload.service.weibo.com/interface/pic_upload.php?" + "app=miniblog&data=1" + waterMark
                + "&mime=image/png&ct=0.2805887470021844";
        String unMark = "http://picupload.service.weibo.com/interface/pic_upload.php?app="
                + "miniblog&data=1&mime=image/png&ct=0.2805887470021844";

        mAsyncHttpClient.post(mContext.getApplicationContext(), markUrl, headerList.build(), reqEntity, contentType,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String result = new String(responseBody);
                        Log.d("uploadFile   RES======: ", result);
                        Gson mGson = new Gson();
                        UploadPicResult ur = mGson.fromJson(PatternUtils.preasePid(result), UploadPicResult.class);
                        if (ur != null) {
                            if (TextUtils.isEmpty(ur.getPid())) {
                                mHandler.sendEmptyMessage(MSG_UPLOAD_FAILED);
                                return;
                            }
                            Log.d("uploadFile   pid: ", ur.getPid());
                            LogTool.D("uploadFile onSuccess" + " " + result);
                            mHasUploadFlag++;
                            mPids += ur.getPid() + ",";
                            if (mHasUploadFlag < mNeedToUpload.size()) {
                                mHandler.sendEmptyMessage(MSG_UPLOAD);
                            } else {
                                mHandler.sendEmptyMessage(MSG_UPLOAD_DONE);
                            }
                        } else {
                            mHandler.sendEmptyMessage(MSG_UPLOAD_DONE);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D("uploadFile onFailure" + " statusCode:" + statusCode + "   " + error.getLocalizedMessage());
                        mHandler.sendEmptyMessage(MSG_UPLOAD_FAILED);
                    }
                });
    }
}
