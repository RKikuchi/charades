package br.com.kiks.charades.services;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.kiks.charades.R;

/**
 * Created by rsaki on 2/10/2016.
 */
public class SoundService {
    private final SoundPool mSoundPool;
    private final int mSuccessSoundId;
    private final int mSkipSoundId;
    private final int mTickSoundId;
    private final int mStartSoundId;
    private final int mFinishSoundId;

    public SoundService(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();

        mSuccessSoundId = mSoundPool.load(context, R.raw.success, 1);
        mSkipSoundId = mSoundPool.load(context, R.raw.skip, 1);
        mTickSoundId = mSoundPool.load(context, R.raw.tick, 1);
        mStartSoundId = mSoundPool.load(context, R.raw.start, 1);
        mFinishSoundId = mSoundPool.load(context, R.raw.finish, 1);
    }

    public void playSuccess() {
        mSoundPool.play(mSuccessSoundId, 1, 1, 0, 0, 1);
    }

    public void playSkip() {
        mSoundPool.play(mSkipSoundId, 1, 1, 0, 0, 1);
    }

    public void playTick() {
        mSoundPool.play(mTickSoundId, 1, 1, 0, 0, 1);
    }

    public void playStart() {
        mSoundPool.play(mStartSoundId, 1, 1, 0, 0, 1);
    }

    public void playFinish() {
        mSoundPool.play(mFinishSoundId, 1, 1, 0, 0, 1);
    }
}
