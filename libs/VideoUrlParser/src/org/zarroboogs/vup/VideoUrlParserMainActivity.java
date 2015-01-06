
package org.zarroboogs.vup;

import org.zarroboogs.vup.VideoUrlParser.OnParsedListener;

import android.app.Activity;
import android.os.Bundle;

public class VideoUrlParserMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_url_parser_main);
        VideoUrlParser videoUrlParser = new VideoUrlParser(getApplicationContext());
        videoUrlParser.parseVideoUrl("http://v.163.com/zixun/V8GAM8VMO/VADK4T54P.html?elsechannel_article_recommend", new OnParsedListener() {
            
            @Override
            public void onParseFailed() {
            }

            @Override
            public void onParseSuccess(String urlname, String ulr) {
            }
        });
    }
}
