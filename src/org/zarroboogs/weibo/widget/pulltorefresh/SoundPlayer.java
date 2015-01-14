package org.zarroboogs.weibo.widget.pulltorefresh;



import org.zarroboogs.weibo.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/*
 * This class controls the sound playback according to the API level.
 */
public class SoundPlayer {

    public interface Player {

        public void release();

        public void play(int action);
    }

    public static Player getPlayer(Context context) {
    	return new SoundPoolPlayer(context);
    }

    public static int getAudioTypeForSoundPool() {
        return ApiHelper.getIntFieldIfExists(AudioManager.class, "STREAM_SYSTEM_ENFORCED", null, AudioManager.STREAM_RING);
    }

    /**
     * This class implements SoundClips.Player using SoundPool, which
     * exists since API level 1.
     */
    private static class SoundPoolPlayer implements Player, SoundPool.OnLoadCompleteListener {

        private static final String TAG = "SoundPoolPlayer";

        private static final int NUM_SOUND_STREAMS = 1;

        private static final int[] SOUND_RES = { // Soundtrack res IDs.
        	R.raw.pop,R.raw.psst1 };

        // ID returned by load() should be non-zero.
        private static final int ID_NOT_LOADED = 0;

        // Store the context for lazy loading.
        private Context mContext;

        // mSoundPool is created every time load() is called and cleared every
        // time release() is called.
        private SoundPool mSoundPool;

        // Sound ID of each sound resources. Given when the sound is loaded.
        private final int[] mSoundIDs;

        private final boolean[] mSoundIDReady;

        private int mSoundIDToPlay;

        public SoundPoolPlayer(Context context) {
            mContext = context;

            mSoundIDToPlay = ID_NOT_LOADED;

            mSoundPool = new SoundPool(NUM_SOUND_STREAMS, getAudioTypeForSoundPool(), 0);
            mSoundPool.setOnLoadCompleteListener(this);

            mSoundIDs = new int[SOUND_RES.length];
            mSoundIDReady = new boolean[SOUND_RES.length];
            for (int i = 0; i < SOUND_RES.length; i++) {
                mSoundIDs[i] = mSoundPool.load(mContext, SOUND_RES[i], 1);
                mSoundIDReady[i] = false;
            }
        }

        @Override
        public synchronized void release() {
            if (mSoundPool != null) {
                mSoundPool.release();
                mSoundPool = null;
            }
        }

        @Override
        public synchronized void play(int resID) {
            if (resID < 0) {
                Log.e(TAG, "Resource ID not found for action:" + resID + " in play().");
                return;
            }

            int index = ID_NOT_LOADED;
            for (int i = 0; i < SOUND_RES.length; i++) {
				if (resID == SOUND_RES[i]) {
					index = i;
				}
			}
            
            if (mSoundIDs[index] == ID_NOT_LOADED) {
                // Not loaded yet, load first and then play when the loading is complete.
                mSoundIDs[index] = mSoundPool.load(mContext, SOUND_RES[index], 1);
                mSoundIDToPlay = mSoundIDs[index];
            } else if (!mSoundIDReady[index]) {
                // Loading and not ready yet.
                mSoundIDToPlay = mSoundIDs[index];
            } else {
                mSoundPool.play(mSoundIDs[index], 1f, 1f, 0, 0, 1f);
            }
        }

        @Override
        public void onLoadComplete(SoundPool pool, int soundID, int status) {
            if (status != 0) {
                Log.e(TAG, "loading sound tracks failed (status=" + status + ")");
                for (int i = 0; i < mSoundIDs.length; i++) {
                    if (mSoundIDs[i] == soundID) {
                        mSoundIDs[i] = ID_NOT_LOADED;
                        break;
                    }
                }
                return;
            }

            for (int i = 0; i < mSoundIDs.length; i++) {
                if (mSoundIDs[i] == soundID) {
                    mSoundIDReady[i] = true;
                    break;
                }
            }

            if (soundID == mSoundIDToPlay) {
                mSoundIDToPlay = ID_NOT_LOADED;
                mSoundPool.play(soundID, 1f, 1f, 0, 0, 1f);
            }
        }
    }
}
