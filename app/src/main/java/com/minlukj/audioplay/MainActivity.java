package com.minlukj.audioplay;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.minlukj.mediaplaylib.MediaPlayFunctionListener;
import com.minlukj.mediaplaylib.MediaPlayInfoListener;
import com.minlukj.mediaplaylib.MediaPlayerUtils;
import java.io.File;

import static com.minlukj.mediaplaylib.MediaPlayerUtils.PLAY_STATE0;
import static com.minlukj.mediaplaylib.MediaPlayerUtils.PLAY_STATE1;
import static com.minlukj.mediaplaylib.MediaPlayerUtils.PLAY_STATE2;
import static com.minlukj.mediaplaylib.MediaPlayerUtils.PLAY_STATE3;

public class MainActivity extends AppCompatActivity {

  private String TAG = MainActivity.class.getCanonicalName();

  private ProgressBar mMusicProgressBar,mMusicNetProgressBar;
  private Button mFile,mRaw,mAssets,mNet,mStart,mPause,mStop;
  private TextView mMusicType;

  private MediaPlayerUtils mMediaPlayerUtils;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mMusicProgressBar = findViewById(R.id.music_progressBar);
    mMusicNetProgressBar = findViewById(R.id.music_net_progressBar);
    mFile = findViewById(R.id.music_file);
    mRaw = findViewById(R.id.music_raw);
    mAssets = findViewById(R.id.music_assets);
    mNet = findViewById(R.id.music_net);
    mStart = findViewById(R.id.music_start);
    mPause = findViewById(R.id.music_pause);
    mStop = findViewById(R.id.music_stop);
    mMusicType = findViewById(R.id.music_type);
    mMusicNetProgressBar.setMax(100);

    mMediaPlayerUtils = new MediaPlayerUtils();
    initOnClick();
    mMediaPlayerUtils.setMediaPlayFunctionListener(new MediaPlayFunctionListener() {
      @Override public void prepared() {
        //准备完毕
        Log.i(TAG,"准备完毕自动开始播放");
      }

      @Override public void start() {
        //开始播放
        //设置进度条最大值
        mMusicProgressBar.setMax(mMediaPlayerUtils.getDuration());
      }

      @Override public void pause() {
        //暂停播放
        Log.i(TAG,"暂停了播放");
      }

      @Override public void stop() {
        //停止播放
        Log.i(TAG,"停止了播放");
      }

      @Override public void reset() {
        //重置
        Log.i(TAG,"重置了播放");
      }
    });

    mMediaPlayerUtils.setMediaPlayInfoListener(new MediaPlayInfoListener() {
      @Override public void onError(MediaPlayer mp, int what, int extra) {
        //错误监听
        Log.e(TAG,"播放错误");
      }

      @Override public void onCompletion(MediaPlayer mediaPlayer) {
        //播放完成监听
        Log.i(TAG,"播放完成");
      }

      @Override public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        //网络缓冲进度监听
        Log.i(TAG,String.valueOf(i));
        mMusicNetProgressBar.setProgress(i);
      }

      @Override public void onSeekComplete(MediaPlayer mediaPlayer) {
        //进度调整监听
        Log.i(TAG,"调整了进度");
      }

      @Override public void onSeekBarProgress(int progress) {
        Log.i(TAG,"progress：" + progress);
        //播放进度监听
        mMusicProgressBar.setProgress(progress);
      }
    });
  }

  private void initOnClick() {
    mFile.setOnClickListener(mOnClickListener);
    mRaw.setOnClickListener(mOnClickListener);
    mAssets.setOnClickListener(mOnClickListener);
    mNet.setOnClickListener(mOnClickListener);
    mStart.setOnClickListener(mOnClickListener);
    mPause.setOnClickListener(mOnClickListener);
    mStop.setOnClickListener(mOnClickListener);
  }

  private View.OnClickListener mOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      if (v == mFile){
        //播放本地文件
        mMediaPlayerUtils.setFilePlay(new File(Environment.getExternalStorageDirectory(),"music.mp3"));
      }else if (v == mRaw){
        //播放Raw
        mMediaPlayerUtils.setRawPlay(MainActivity.this,R.raw.music3);
      }else if (v == mAssets){
        //播放Assets
        mMediaPlayerUtils.setAssetsName(MainActivity.this,"music2.mp3");
      }else if (v == mNet){
        //播放网络资源
        mMediaPlayerUtils.setNetPath("http://www.minlukj.com/music.mp3");
      }else if (v == mStart){
        //开始播放
        mMediaPlayerUtils.start();
        switch (mMediaPlayerUtils.getMusicType()){
          case PLAY_STATE0:
            mMusicType.setText("正在播放文件类型");
            break;
          case PLAY_STATE1:
            mMusicType.setText("正在播放RAW下文件资源");
            break;
          case PLAY_STATE2:
            mMusicType.setText("正在播放ASSETS文件下资源");
            break;
          case PLAY_STATE3:
            mMusicType.setText("正在播放网络资源");
            break;

        }
      }else if (v == mPause){
        //暂停播放
        mMediaPlayerUtils.pause();
      }else if (v == mStop){
        //停止播放
        mMediaPlayerUtils.stop();
      }
    }
  };

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mMediaPlayerUtils != null)
      mMediaPlayerUtils.destory();
  }

  @Override protected void onResume() {
    super.onResume();
    if (mMediaPlayerUtils != null)
      mMediaPlayerUtils.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    if (mMediaPlayerUtils != null)
      mMediaPlayerUtils.pause();
  }
}
