package com.minlukj.mediaplaylib;

import android.media.MediaPlayer;

/**
 * 获取当前信息的回调
 */

public interface MediaPlayInfoListener {
  //播放错误监听
  void onError(MediaPlayer mp, int what, int extra);
  //播放完成监听
  void onCompletion(MediaPlayer mediaPlayer);
  //网络缓冲监听
  void onBufferingUpdate(MediaPlayer mediaPlayer, int i);
  //进度调整监听
  void onSeekComplete(MediaPlayer mediaPlayer);
  //时实播放进度
  void onSeekBarProgress(int progress);
}
