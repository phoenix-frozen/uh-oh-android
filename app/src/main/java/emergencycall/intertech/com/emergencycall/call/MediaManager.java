package emergencycall.intertech.com.emergencycall.call;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

/**
 * Created by liudassurvila on 30/08/2014.
 */
public class MediaManager {

    private static final int DELAY_BETWEEN_FILES = 5000;

    private Context mContext;

    private PhoneStateManager.CallListener mListener;

    private MediaPlayer mMediaPlayer;

    private String[] mMediaFiles = { "Pre-recording1.mp3", "Pre-recording2 - loop.mp3" };
    private int mCurrentPlayingFile = 0;

    public MediaManager(Context context, PhoneStateManager.CallListener listener) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mContext = context;
        mListener = listener;
    }

    public void play() {
        try {

            AssetFileDescriptor descriptor = mContext.getAssets().openFd(mMediaFiles[mCurrentPlayingFile]);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(1f, 1f);
            // second file should play in loop
            if (mCurrentPlayingFile == 1) {
                mMediaPlayer.setLooping(true);
            }
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // when first file is complete play second
                    if (mCurrentPlayingFile == 0) {
                        mCurrentPlayingFile++;

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                play();
                            }
                        }, DELAY_BETWEEN_FILES);

                    }
                }
            });
            mMediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            mCurrentPlayingFile = 0;
            mMediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
