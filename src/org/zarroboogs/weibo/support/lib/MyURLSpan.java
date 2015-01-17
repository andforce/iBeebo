
package org.zarroboogs.weibo.support.lib;

import io.vov.vitamio.demo.VideoViewBuffer;

import org.zarroboogs.vup.VideoUrlParser;
import org.zarroboogs.vup.VideoUrlParser.OnParsedListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.UserInfoActivity;
import org.zarroboogs.weibo.dialogfragment.LongClickLinkDialog;
import org.zarroboogs.weibo.support.utils.ThemeUtility;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.WebBrowserSelector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.support.v4.app.FragmentActivity;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * User: qii Date: 12-8-20
 */
public class MyURLSpan extends ClickableSpan implements ParcelableSpan {

    private final String mURL;
    
    public MyURLSpan(String url) {
        mURL = url;
    }

    public MyURLSpan(Parcel src) {
        mURL = src.readString();
    }

    public int getSpanTypeId() {
        return 11;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mURL);
    }

    public String getURL() {
        return mURL;
    }

    public void onClick(View widget) {
        Uri uri = Uri.parse(getURL());
        final Context context = widget.getContext();
        if (uri.getScheme().startsWith("http")) {
            String url = uri.toString();
            if (Utility.isWeiboAccountIdLink(url)) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("id", Utility.getIdFromWeiboAccountLink(url));
                context.startActivity(intent);
            } else if (Utility.isWeiboAccountDomainLink(url)) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("domain", Utility.getDomainFromWeiboAccountLink(url));
                context.startActivity(intent);
            } else {
                // otherwise some urls cant be opened, will be redirected to
                // sina error page
                String openUrl = url;
                if (openUrl.endsWith("/")) {
                    openUrl = openUrl.substring(0, openUrl.lastIndexOf("/"));
                }
                
                final String urlString = openUrl;
                VideoUrlParser videoUrlParser = new VideoUrlParser(widget.getContext());
                videoUrlParser.parseVideoUrl(openUrl, new OnParsedListener() {
					
					@Override
					public void onParseSuccess(String url, String name) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context, VideoViewBuffer.class);
						intent.putExtra(VideoViewBuffer.VIDEO_NAME, name);
						intent.putExtra(VideoViewBuffer.VIDEOURL, url);
						context.startActivity(intent);
					}
					
					@Override
					public void onParseFailed() {
						// TODO Auto-generated method stub
		                WebBrowserSelector.openLink(context, Uri.parse(urlString));
					}
				});

                
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }

    public void onLongClick(View widget) {
        Uri data = Uri.parse(getURL());
        if (data != null) {
            String d = data.toString();
            String newValue = "";
            if (d.startsWith("org.zarroboogs.weibo")) {
                int index = d.lastIndexOf("/");
                newValue = d.substring(index + 1);
            } else if (d.startsWith("http")) {
                newValue = d;
            }
            if (!TextUtils.isEmpty(newValue)) {
                Utility.vibrate(widget.getContext(), widget);
                LongClickLinkDialog dialog = new LongClickLinkDialog(data);
                Utility.forceShowDialog((FragmentActivity) widget.getContext(), dialog);

            }

        }
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(ThemeUtility.getColor(R.attr.link_color));
        // tp.setUnderlineText(true);
    }
}
