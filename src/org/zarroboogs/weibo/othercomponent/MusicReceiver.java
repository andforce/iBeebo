package org.zarroboogs.weibo.othercomponent;

import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.bean.MusicInfoBean;
import org.zarroboogs.weibo.support.lib.RecordOperationAppBroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * User: qii Date: 14-2-5
 */
public class MusicReceiver extends RecordOperationAppBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String artist = intent.getStringExtra("artist");
		String album = intent.getStringExtra("album");
		String track = intent.getStringExtra("track");
		if (!TextUtils.isEmpty(track)) {
			MusicInfoBean musicInfo = new MusicInfoBean();
			musicInfo.setArtist(artist);
			musicInfo.setAlbum(album);
			musicInfo.setTrack(track);
			AppLoggerUtils.d("Music" + artist + ":" + album + ":" + track);
			GlobalContext.getInstance().updateMusicInfo(musicInfo);
		}
	}
}
