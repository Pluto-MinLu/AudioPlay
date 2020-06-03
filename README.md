# 使用**MediaPlayer**进行音频播放

[！[]（https://www.jitpack.io/v/Pluto-MinLu/AudioPlay.svg）]（https://www.jitpack.io/#Pluto-MinLu/AudioPlay）

### 播放本地文件，raw，assets，网络资源效果。

![播放效果图](https://github.com/Chen-Xi-g/AudioPlay/blob/master/mediaplay.gif)

 How to
--

##### To get a Git project into your build:
 
##### **Step 1. Add the JitPack repository to your build file**
 
##### Gradle

 ```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

##### **Step 2. Add the dependency**

 ```
dependencies {
       implementation 'com.github.Chen-Xi-g:AudioPlay:v1.1.0'
}
```

 ### 如何使用(How to use)
 
 #### 1.初始化(Initialization)
 
```java
/**
 * 在需要使用的类中进行初始化
 */
MediaPlayerUtils mMediaPlayerUtils = new MediaPlayerUtils();
```
 
 #### 2.使用(Use)
 
```java
 /**
  *  设置播放类型。
  */
 //播放sd卡资源
 MediaPlayerUtils.setFilePlay(File file);
 //播放raw下文件
 MediaPlayerUtils.setRawPlay(Context context,int rawId);
 //播放assets下文件
 MediaPlayerUtils.setAssetsName(Context context,String assetsName);
 //播放网络资源
 MediaPlayerUtils.setNetPath(String netPath);

 //开始播放
 MediaPlayerUtils.start();
 //停止播放
 MediaPlayerUtils.stop();
 //暂停播放
 MediaPlayerUtils.pause();
 //继续播放
 MediaPlayerUtils.resume();
 //或
 MediaPlayerUtils.getMediaPlayer().start();
 //是否正在运行
 MediaPlayerUtils.isRunning();
//是否在播放中
 MediaPlayerUtils.isPlaying();
//播放文件的时长
 MediaPlayerUtils.getDuration();
//获取当前播放位置
 MediaPlayerUtils.getCurrentPosition();
//左右声道大小 0-1
 MediaPlayerUtils.setVolume(float leftVolume, float rightVolume);
//设置唤醒方式 需要在清单文件AndroidManifest.xml中添加权限 
//<uses-permission android:name="android.permission.WAKE_LOCK" />
 MediaPlayerUtils.setWakeMode(Context context, int mode);
//播放时不熄屏
 MediaPlayerUtils.setScreenOnWhilePlaying(boolean screenOn);
//指定播放位置 毫秒
 MediaPlayerUtils.seekTo(int msec);
//是否循环播放
 MediaPlayerUtils.setLooping(boolean looping);
//获取当前播放资源类型
 MediaPlayerUtils.getMusicType();
//多久获取一次进度 毫秒
 MediaPlayerUtils.setSleep(int sleep);
//获取MediaPlayer对象
 MediaPlayerUtils.getMediaPlayer();
```

 ### 3.MediaPlayer监听回调
```java
/**
 * MediaPlayer状态回调
 */
MediaPlayerUtils.setMediaPlayFunctionListener();

public interface MediaPlayFunctionListener {
  // 准备完成
  void prepared();
  // 开始播放
  void start();
  // 暂停
  void pause();
  // 停止播放
  void stop();
  //重置
  void reset();
}

 /**
 * 获取当前信息的回调
 */
MediaPlayerUtils.setMediaPlayInfoListener();

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
```

### 具体代码可以看Demo中示例

 ### 如果你感觉对你有用的话请点一下Star吧，而且你还可以打赏一波(If you feel useful to you, please click Star, or you can reward it.)
 
 <img src="http://r.photo.store.qq.com/psb?/V12LSg7n0Vj1Fg/JIE.r7vzYd0JdQV4.U8AFDF2wy5d*DXixdQZ2ZFiV6I!/r/dEYBAAAAAAAA" height = "400" width = "300">      <img src="http://r.photo.store.qq.com/psb?/V12LSg7n0Vj1Fg/64q8qbMEanfoAXbFWxrESl6QXS7ITX63kCabiSRL440!/r/dLYAAAAAAAAA" height = "400" width = "300">
 
 ### 如何联系我(How to contact me)
 
 **QQ:** 1217056667
 
 **邮箱(Email):** a912816369@gmail.com
 
 **小站:** www.minlukj.com
