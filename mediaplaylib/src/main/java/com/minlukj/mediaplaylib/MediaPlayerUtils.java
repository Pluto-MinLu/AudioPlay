package com.minlukj.mediaplaylib;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.IOException;

/**
 * 播放音频文件工具类
 */
public class MediaPlayerUtils {
  private int PLAY_STATE = -1;//判断
  public static final int PLAY_STATE0 = 1;//文件
  public static final int PLAY_STATE1 = 2;//raw
  public static final int PLAY_STATE2 = 3;//assets
  public static final int PLAY_STATE3 = 4;//网络
  public static final int IS_PLAY_STOP = 100;//是否停止播放
  private int duration;
  private boolean isPlaying = false;


  //播放文件的路径
  private File targetFile;
  //播放raw媒体源
  private int rawId;
  private Context mContext;
  //播放assets媒体源
  private String assetsName;
  //播放网络资源
  private String netPath;

  private MediaPlayer mMediaPlayer = null;

  private MediaPlayFunctionListener mediaPlayFunctionListener;
  private MediaPlayInfoListener mMediaPlayInfoListener;

  //多久获取一次进度 默认500毫秒
  private int sleep = 500;

  private Handler mHandler = new Handler(new Handler.Callback() {
    @Override public boolean handleMessage(Message msg) {
      //停止前发送进度.
      if (mMediaPlayInfoListener != null && mMediaPlayer != null && isPlaying())
        mMediaPlayInfoListener.onSeekBarProgress(mMediaPlayer.getCurrentPosition());

      if (msg.what == IS_PLAY_STOP && mHandler != null){
        mHandler.removeCallbacks(mRunnable);
      }
      return false;
    }
  });
  private Runnable mRunnable = new Runnable() {
    @Override public void run() {
      if (mMediaPlayInfoListener != null && mMediaPlayer != null && isPlaying())
        mMediaPlayInfoListener.onSeekBarProgress(mMediaPlayer.getCurrentPosition());
      mHandler.postDelayed(mRunnable,sleep);
    }
  };

  public MediaPlayerUtils() {
  }

  public MediaPlayerUtils(Context context) {
    mContext = context;
  }


  /**
   * 设置文件路径
   * @param file 需要播放文件的路径
   */
  public void setFilePlay(File file) {
    this.targetFile = file;
    PLAY_STATE = PLAY_STATE0;
    stop();
  }

  /**
   * 设置Raw播放
   * @param rawId R.raw.music3
   */
  public void setRawPlay(Context context,int rawId){
    this.mContext = context;
    this.rawId = rawId;
    PLAY_STATE = PLAY_STATE1;
    stop();
  }

  /**
   * 设置Raw播放
   * 调用此方法必须在初始化时传入Context
   * @param rawId R.raw.music3
   */
  public void setRawPlay(int rawId){
    if (mContext == null){
      throw new NullPointerException("Context Null");
    }
    this.rawId = rawId;
    PLAY_STATE = PLAY_STATE1;
    stop();
  }

  /**
   * 设置Assets播放
   * @param assetsName assets文件名
   */
  public void setAssetsName(Context context,String assetsName) {
    this.mContext = context;
    this.assetsName = assetsName;
    PLAY_STATE = PLAY_STATE2;
    stop();
  }

  /**
   * 设置Assets播放
   * 调用此方法必须在初始化时传入Context
   * @param assetsName assets文件名
   */
  public void setAssetsName(String assetsName) {
    if (mContext == null){
      throw new NullPointerException("Context Null");
    }
    this.assetsName = assetsName;
    PLAY_STATE = PLAY_STATE2;
    stop();
  }

  /**
   * 设置网络资源播放
   * @param netPath 网络音乐地址
   */
  public void setNetPath(String netPath) {
    this.netPath = netPath;
    PLAY_STATE = PLAY_STATE3;
    stop();
  }

  /**
   * 开始播放
   * @return true 开始播放， false 播放错误
   */
  public boolean start() {
    if (PLAY_STATE == PLAY_STATE1){
      mMediaPlayer = MediaPlayer.create(mContext,rawId);
    }else {
      mMediaPlayer = new MediaPlayer();
    }
    try {
      switch (PLAY_STATE){
        case PLAY_STATE0:
          mMediaPlayer.setDataSource(targetFile.getAbsolutePath());
          mMediaPlayer.prepare();
          break;
        case PLAY_STATE1:
          break;
        case PLAY_STATE2:
          AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd(assetsName);
          mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
          mMediaPlayer.prepare();
          break;
        case PLAY_STATE3:
          mMediaPlayer.setDataSource(netPath);
          mMediaPlayer.prepareAsync();
          break;
      }
      //播放完成自动停止
      mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mediaPlayer) {
          if (mMediaPlayInfoListener != null)
            mMediaPlayInfoListener.onCompletion(mediaPlayer);
          stop();
        }
      });
      //准备完毕 自动播放
      mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override public void onPrepared(MediaPlayer mediaPlayer) {
          mediaPlayer.start();
          mHandler.postDelayed(mRunnable,sleep);
          duration = mMediaPlayer.getDuration();
          if (mediaPlayFunctionListener != null) {
            mediaPlayFunctionListener.prepared();
            mediaPlayFunctionListener.start();
          }
        }
      });
      //播放错误监听
      mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
        @Override public boolean onError(MediaPlayer mp, int what, int extra) {
          if (mMediaPlayInfoListener != null) {
            mMediaPlayInfoListener.onError(mp, what, extra);
          }
          if (mMediaPlayer != null)
            stop();
          mHandler.removeCallbacks(mRunnable);
          return false;
        }
      });
      //网络缓冲监听
      mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
        @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
          if (mMediaPlayInfoListener != null)
            mMediaPlayInfoListener.onBufferingUpdate(mp,percent);
        }
      });
      //调整进度监听
      mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
        @Override public void onSeekComplete(MediaPlayer mp) {
          if (mMediaPlayInfoListener != null)
            mMediaPlayInfoListener.onSeekComplete(mp);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
      stop();
    }

    boolean result = (mMediaPlayer != null);
    this.isPlaying = result;

    return result;
  }

  /**
   *   停止播放
   */
  public void stop() {
    this.isPlaying = false;
    this.duration = 0;

    if (mMediaPlayer != null) {
      Message message = new Message();
      message.what = IS_PLAY_STOP;
      mHandler.sendMessage(message);
      mMediaPlayer.stop();
      mMediaPlayer.reset();
      if (mediaPlayFunctionListener != null)
        mediaPlayFunctionListener.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  /**
   * resume调用 继续播放也调用此方法即可
   */
  public void resume() {
    if (mMediaPlayer != null) {
      this.isPlaying = true;
      mHandler.postDelayed(mRunnable,sleep);
      mMediaPlayer.start();
    }
  }

  /**
   * 暂停
   */
  public void pause() {
    this.isPlaying = false;
    mHandler.removeCallbacks(mRunnable);
    if (mMediaPlayer != null) {
      mMediaPlayer.pause();
      if (mediaPlayFunctionListener != null)
        mediaPlayFunctionListener.pause();
    }
  }

  /**
   * 是否正在运行
   * @return true 正在运行，false停止运行
   */
  public boolean isRunning() {
    return (mMediaPlayer != null);
  }

  /**
   * 是否在播放中
   * @return true 正在播放，false 停止播放
   */
  public boolean isPlaying() {
    return this.isPlaying;
  }

  /**
   * 播放文件的时长
   * @return 文件时长
   */
  public int getDuration() {
    if (mMediaPlayer == null) {
      return this.duration;
    }

    return mMediaPlayer.getDuration();
  }

  /**
   * 获取当前播放位置
   * @return 当前播放位置值
   */
  public int getCurrentPosition() {
    if (mMediaPlayer == null) {
      return 0;
    }

    return mMediaPlayer.getCurrentPosition();
  }

  /**
   * 左右声道大小
   * @param leftVolume 左声道大小 0 - 1
   * @param rightVolume 右声道大小 0 - 1
   */
  public void setVolume(float leftVolume, float rightVolume){
    if (mMediaPlayer != null){
      mMediaPlayer.setVolume(leftVolume,rightVolume);
    }
  }

  /**
   * 设置唤醒方式 需要在清单文件AndroidManifest.xml中添加权限 <uses-permission android:name="android.permission.WAKE_LOCK" />
   * @param context 上下文
   * @param mode 唤醒模式
   */
  public void setWakeMode(Context context, int mode){
    if (mMediaPlayer != null)
      mMediaPlayer.setWakeMode(context,mode);
  }

  /**
   * 播放时不熄屏
   * @param screenOn true 不息屏，false 息屏
   */
  public void setScreenOnWhilePlaying(boolean screenOn){
    if (mMediaPlayer != null)
      mMediaPlayer.setScreenOnWhilePlaying(screenOn);
  }

  /**
   * 指定播放位置 毫秒
   * @param msec 要播放的值
   */
  public void seekTo(int msec){
    if (mMediaPlayer != null)
      mMediaPlayer.seekTo(msec);
  }

  /**
   * 是否循环播放
   * @param looping true 循环播放，false 不循环
   */
  public void setLooping(boolean looping){
    if (mMediaPlayer != null)
      mMediaPlayer.setLooping(looping);
  }

  /**
   * 获取当前播放资源类型
   * @return 当前资源类型
   */
  public int getMusicType(){
    return PLAY_STATE;
  }

  /**
   * 必须调用此方法 销毁，释放
   */
  public void destory(){
    stop();
  }

  /**
   * 多久获取一次进度 毫秒
   * @param sleep 默认500
   */
  public void setSleep(int sleep){
    this.sleep = sleep;
  }

  /**
   * 获取MediaPlayer对象
   * @return MediaPlayer
   */
  public MediaPlayer getMediaPlayer() {
    return this.mMediaPlayer;
  }

  /**
   * 功能监听
   */
  public void setMediaPlayFunctionListener(MediaPlayFunctionListener mediaPlayFunctionListener){
    this.mediaPlayFunctionListener = mediaPlayFunctionListener;
  }

  /**
   * 播放信息监听
   */
  public void setMediaPlayInfoListener(MediaPlayInfoListener mediaPlayInfoListener) {
    this.mMediaPlayInfoListener = mediaPlayInfoListener;
  }
}
