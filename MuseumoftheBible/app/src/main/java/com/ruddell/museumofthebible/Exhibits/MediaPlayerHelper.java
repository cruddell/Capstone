/*
 * Copyright (c) 2015. Museum of the Bible
 */

package com.ruddell.museumofthebible.Exhibits;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

/**
 * Media player class to manage media player state and greatly
 * simplify which operations are valid for our media player state
 * This class manages a common and frustrating source of player errors.
 */

public class MediaPlayerHelper implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = "MediaPlayerHelper";
    private static boolean DEBUG = false;

    private StateMediaPlayer mCurrentState;
    private String mMediaPath;
    private MediaPlayer mMediaPlayer;
    //private Context mContext;
    private boolean mPlayerReady;

    private boolean mPlayWhenReady = false;  // indicates we should begin startMediaPlayback when ready
    private int mDuration = 0;                  // duration of media in milliseconds set when prepared
    private int mStartPosition = 0;

    public static final int MEDIAFILEMISSING = 999001;

    /** class constructor */
    public MediaPlayerHelper(Context context, String mediaPath, boolean playWhenReady, MediaPlayerHelperListener listener) {
        //mContext = context;
        mMediaPath = mediaPath;

        if (mediaPath==null) {
            Log.e(TAG,"null filename!");
            return;
        } else Log.d(TAG,"loading file at:" + mediaPath);
         if(listener != null) {
            mEventListener = listener;
        } else {
            mEventListener = new DummyListener();  // if not set, add dummy listener to handle events
        }

        getMediaPlayer();
        setState(new StateIdle());

        if (mediaPath.length()>0) {
            mCurrentState.setDataSource(mediaPath);
        }

        mPlayWhenReady = playWhenReady;
    }

    /** class constructor */
    MediaPlayerHelper(Context context, boolean playWhenReady, MediaPlayerHelperListener listener) {
        //mContext = context;

        if(listener != null) {
            mEventListener = listener;
        } else {
            mEventListener = new DummyListener();  // if not set, add dummy listener to handle events
        }

        getMediaPlayer();
        setState(new StateIdle());
        mPlayWhenReady = playWhenReady;
    }

    public void setMediaPlayerHelperListener(MediaPlayerHelperListener listener) {
        if(listener != null) {
            mEventListener = listener;
        } else {
            mEventListener = new DummyListener();  // if not set, add dummy listener to handle events
        }
    }

//    public void updateMediaFile(String filename) {
//        mMediaPath = FileHelper.getAudioPath(mContext, filename);
//        updateMedia(mMediaPath);
//    }
//
//    public void updateMediaFileForTours(String filename) {
//        mMediaPath = FileHelper.getAudioPathForTours(mContext, filename);
//        updateMedia(mMediaPath);
//    }


    public void updateMedia(String filePath) {
        updateMedia(filePath, -1, mPlayWhenReady);
    }

    public void updateMedia(String filePath, int startPosition, boolean playWhenReady) {
        if(DEBUG) Log.i(TAG,"updateMedia() filePath="+filePath+"  startPosition="+startPosition+"  playWhenReady="+playWhenReady);
        try {
            mMediaPath = filePath;
            mStartPosition = startPosition;
            mPlayWhenReady = playWhenReady;

            // setup player for new media and sync our player state machine
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepareAsync();
            setState(new StatePreparing());  // media player is returned to idle state after reset
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mMediaPlayer.reset();
            setState(new StateIdle());
        }
    }

    public boolean isReady() {
        return mPlayerReady;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getCurrentPosition() {
        // returns 0 if in an error state;  otherwise returns MediaPlayer.getCurrentPosition()
        return mCurrentState.getCurrentPosition(mMediaPlayer);
    }

    public boolean isPlaying() {
        // returns false if in an error state; otherwise returns MediaPlayer.isPlaying()
        return mCurrentState.isPlaying(mMediaPlayer);
    }

    public void release() {
        if(DEBUG) Log.e(TAG, "RELEASE CALLED! Helper must be re-instantiated for future use.");
        mMediaPlayer.reset();      // return to idle state
        mMediaPlayer.release();    // release player to free up resources related to playback (eg hardware acceleration)
        mMediaPlayer = null;       // player is only a memory... but not ours
    }

    // MediaPlayerHelper public controls
    public void pause() { mCurrentState.pause(); }

    public void play() {
        Log.d(TAG,"startMediaPlayback(" + mMediaPath + ")");
        mCurrentState.play();
    }

    public void stop() {
        Log.d(TAG,"stopMediaPlayback()");
        mCurrentState.stop();
    }

    public void reset() {
        Log.d(TAG,"reset()");
        mMediaPlayer.reset();
        setState(new StateIdle());
    }

//    public void playAfterReset() {
//        int position = mMediaPlayer.getCurrentPosition();
//        mMediaPlayer.reset();
//        mMediaPlayer.prepareAsync();
//        setState(new StatePreparing());
//    }

    public void seekTo(int position) { mCurrentState.seekTo(position); }

    private void getMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);       // listen for onPrepared notification
        mMediaPlayer.setOnErrorListener(this);          // listen for player error event
        mMediaPlayer.setOnCompletionListener(this);     // listen for media completion event
        mMediaPlayer.setOnSeekCompleteListener(this);   // listen for seek complete event
    }

    private StateMediaPlayer setState(StateMediaPlayer newState) {
        mCurrentState = newState;
        if (newState instanceof StatePrepared) {
            mEventListener.onMediaPlayerReady();
            mPlayerReady = true;
        }
        return mCurrentState;
    }

    /** Support the primary Media Player listeners */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mCurrentState.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mCurrentState = new StateError();
        ((StateError)mCurrentState).onError(what, extra);

        return true;  // true indicates that the error has been managed
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mCurrentState.onPrepared();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mEventListener.onSeekComplete(mCurrentState.getCurrentPosition(mp));
    }

    abstract static class StateMediaPlayer {
//        public void setMediaPlayer(MediaPlayer player) { mPlayer = player; }
        public String toString() { return this.getClass().toString(); }

        // each state will override their valid operations
        public void pause() { invalidOperation(); }
        public void play() { invalidOperation(); }
        public void seekTo(int position) { invalidOperation(); }
        public void stop() { invalidOperation(); }
        public void prepareAsync() { invalidOperation(); }
        public void onPrepared() { invalidOperation(); }
        public void onCompletion() { invalidOperation(); }
        public void reset(MediaPlayer player) { player.reset(); }  // reset supported in all states
        public void release() { invalidOperation(); }
        public void setDataSource(String mediaPath) { invalidOperation(); }
        public boolean isPlaying(MediaPlayer player) { return player!=null && player.isPlaying(); }
        public int getCurrentPosition(MediaPlayer player) { return player.getCurrentPosition(); }

        String thisClassName() {return this.getClass().toString();}
        private void invalidOperation() {
            if(DEBUG) Log.e(TAG,"invalidOperation() mCurrentState="+this.toString());
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i=0; i<stackTrace.length; i++) {
                Log.e(thisClassName(), stackTrace[i].toString());
            }
            Log.e(thisClassName(), Arrays.toString(Thread.currentThread().getStackTrace()));
        }

    }

    // Idle occurs after media player instantiation and reset
    class StateIdle extends StateMediaPlayer {

        @Override
        public void setDataSource(String mediaPath) {
            if(DEBUG) Log.d(TAG,"setDataSource(" + mediaPath + ")");
            try {
                mMediaPlayer.setDataSource(mediaPath);
                mMediaPlayer.prepareAsync();
                setState(new StatePreparing());
            } catch (IOException e) {
                Log.e(thisClassName(), Log.getStackTraceString(e));
                mEventListener.onMediaPlayerError(MEDIAFILEMISSING,0);
            }
        }
    }

    // preparing is a transient state that occurs after requesting media preparation
    class StatePreparing extends StateMediaPlayer {

        public StatePreparing() {
            mPlayerReady = false;
            if(DEBUG) Log.i(TAG,"MediaPlayer->StatePreparing()  mStartPosition="+mStartPosition+"  mPlayWhenReady="+mPlayWhenReady);
        }
        // only supports onPrepared() listener handling

        @Override
        public void onPrepared() {
            // transition to prepared state
            setState(new StatePrepared());
            // call StatePrepared.startMediaPlayback() if startMediaPlayback has been queued
            if(mStartPosition>0) mCurrentState.seekTo(mStartPosition);
            if(mPlayWhenReady) mCurrentState.play();
        }

        @Override
        public void play() {
            mPlayWhenReady=true;
        }

    }

    // media has been prepared for playback.  Audio/sound volume can now be adjusted
    class StatePrepared extends StateMediaPlayer {
        // supports startMediaPlayback(), seekTo(), and stopMediaPlayback()
        StatePrepared() {
            if(DEBUG) Log.i(TAG,"MediaPlayer->StatePrepared()  mStartPosition="+mStartPosition+"  mPlayWhenReady="+mPlayWhenReady);
            mDuration = mMediaPlayer.getDuration();
        }

        @Override
        public void play() {
            mMediaPlayer.start();
            setState(new StateStarted());
        }

        @Override
        public void seekTo(int position) {
            // update media player but remain in prepared state
            mMediaPlayer.seekTo(position);
        }

        @Override
        public void stop() {
            mMediaPlayer.stop();
            setState(new StateStopped());
        }
    }

    // Preparing has completed.  Audio/sound volume can now be adjusted
    class StateStarted extends StateMediaPlayer {
        // supports startMediaPlayback(), seekTo(), stopMediaPlayback(), pause() and onCompletion()
        StateStarted() {
            if(DEBUG) Log.i(TAG,"MediaPlayer->StateStarted()  mStartPosition="+mStartPosition+"  mPlayWhenReady="+mPlayWhenReady);
            mEventListener.onPlayStart();
        }

        @Override
        public void play() {}   // valid but has no effect

        @Override
        public void seekTo(int position) {
            // update player's position and keep playing
            mMediaPlayer.seekTo(position);
        }

        @Override
        public void stop() {
            mMediaPlayer.stop();
            mEventListener.onPlayEnd();
            setState(new StateStopped());
        }

        @Override
        public void pause() {
            mMediaPlayer.pause();
            mEventListener.onPlayEnd();
            setState(new StatePaused());
        }

        @Override
        public void onCompletion() {
            // player reported media has completed
            setState(new StatePlaybackCompleted());
            mEventListener.onPlayEnd();
        }
    }

    // Preparing has completed.  Audio/sound volume can now be adjusted
    class StatePaused extends StateMediaPlayer {
        // supports pause(), startMediaPlayback(), seekTo(), and stopMediaPlayback()
        StatePaused() { mPlayWhenReady = false; }

        @Override
        public void pause() {}  // valid but has no effect

        @Override
        public void seekTo(int position) {
            // update media player but remain in paused state
            mMediaPlayer.seekTo(position);
        }

        @Override
        public void play() {
            mMediaPlayer.start();
            setState(new StateStarted());
        }

        @Override
        public void stop() {
            setState(new StateStopped());
            mMediaPlayer.stop();
        }
    }

    // Preparing has completed.  Audio/sound volume can now be adjusted
    class StateStopped extends StateMediaPlayer {
        StateStopped() { mPlayWhenReady = false; }
        // supports stopMediaPlayback() and prepareAsync()

        @Override
        public void stop() {}  // valid but has no effect

        @Override
        public void prepareAsync() {
            mMediaPlayer.prepareAsync();
            setState(new StatePreparing());
        }

        @Override
        public void play() {
            // MediaPlayer.start() is NOT supported in this state
            // set mPlayWhenReady flag and then prepare the player
            mPlayWhenReady = true;
            this.prepareAsync();
        }
    }

    class StatePlaybackCompleted extends StateMediaPlayer {
        StatePlaybackCompleted() { mEventListener.onPlayComplete();}

        @Override
        public void pause() {}  // valid but has no effect

        @Override
        public void play() {
            mMediaPlayer.start();
            setState(new StateStarted());
        }

        @Override
        public void seekTo(int position) {
            mMediaPlayer.seekTo(position);
        }
    }

    class StateError extends StateMediaPlayer {

        // support for MediaPlayer.onErrorListener()
        boolean onError(int what, int extra) {
            Log.e(TAG, "Media player error! (" + what + "," + extra + ")  Resetting player.");
            mEventListener.onMediaPlayerError(what,extra);
            mMediaPlayer.reset();
            setState(new StateIdle());  // media player is returned to idle state after reset
            mCurrentState.setDataSource(mMediaPath);
            return true;  // error has been handled
        }

        @Override
        public boolean isPlaying(MediaPlayer player) {
            return false;
        }

        @Override
        public int getCurrentPosition(MediaPlayer player) {
            return 0;  // media player doesn't support this call in an error state; return 0
        }

    }

    private MediaPlayerHelperListener mEventListener;

    // setting a dummy listener avoids needing to null check listener calls
    class DummyListener implements MediaPlayerHelperListener {
        @Override
        public void onMediaPlayerReady() {}
        @Override
        public void onSeekComplete(int position) {}
        @Override
        public void onPlayComplete() {}
        @Override
        public void onMediaPlayerError(int what, int extra) {}
        @Override
        public void onPlayStart() {}
        @Override
        public void onPlayEnd() {}
    }

    public interface MediaPlayerHelperListener {
        void onMediaPlayerReady();                      // media player is done preparing and ready to startMediaPlayback
        void onSeekComplete(int position);              // media player onSeekComplete() event
        void onPlayComplete();                          // media player has entered PlaybackComplete state
        void onMediaPlayerError(int what, int extra);   // media player has encountered an error and will be reset
        void onPlayStart();                             // media player has entered 'Started' state
        void onPlayEnd();                               // media player has left 'Started' state; an error does not trigger this event
    }
}
