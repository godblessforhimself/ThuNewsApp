package com.java.twentynine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by Tony on 2017/9/8.
 */
interface mTtsInterface
{
    // TTS的监听回调不开放(监听初始化和播放的两个类的接口不开放，在mTTS中直接实现)
    void initEngine(Activity context, String text);//初始化
    int startSpeaking();//开始合成语音并开始播放
    void stopSpeaking();//强制结束播放，不可恢复
    void pauseSpeaking();//暂停播放，可恢复
    void resumeSpeaking();//继续播放
    boolean isSpeaking();//是否正在播放
    boolean destroy();//销毁Synthesizer，若仍在播放返回false。可以不用它。
    boolean hasInit();
    //参数设置和获取（已经有默认值，可以不调用）
    void setSpeed(int speed);//设置语速，默认50，范围[0,100]
    void setVolume(int volume);//设置合成语音音量，默认50，范围[0,100]
    void setVoicer(String voicer);//设置发音人，默认女声xiaoyan。发音人列表见http://doc.xfyun.cn/msc_android/319160
    int getSpeed();
    int getVolume();
    String getVoicer();

    int getBufferProgress();//获取缓冲进度[0,100]
    int getSpeakingProgress();//获取阅读进度[0,100]
}

public class mTts implements mTtsInterface{
    private Context mcontext;
    private String mText;
    private SpeechSynthesizer mTts;
    private int buffer,speaking;
    private boolean valid = false;
    private InitListener mListener = new InitListener() {
        @Override
        public void onInit(int i) {

            if (i == ErrorCode.SUCCESS)
            {
                showTip("在线语音连接成功...");
            }else
            {
                showTip("在线语音连接失败...");
            }
        }
    };
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

            buffer = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            speaking = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
                buffer = 100;
                speaking = 100;
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //    if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //        String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //        Log.d(TAG, "session id =" + sid);
            //    }
        }
    };

    @Override
    public void initEngine(Activity context,String text) {
        mcontext = context;
        SpeechUtility.createUtility(context,SpeechConstant.APPID +"=59a77b75");
        mTts = SpeechSynthesizer.createSynthesizer(mcontext, mListener);
        mText = text;
        Log.d("TTs","mTts " + (mTts == null ? "is null" : "not null"));
        mTts.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        mTts.setParameter(SpeechConstant.VOLUME,"50");
        mTts.setParameter(SpeechConstant.SPEED,"50");
        valid = true;
    }

    @Override
    public int startSpeaking() {
        if (mTts == null)
            return -1;
       return mTts.startSpeaking(mText,mTtsListener);
    }


    @Override
    public void stopSpeaking() {
        if (mTts == null)
        {
            showTip("mTts is null");
            return;
        }
        mTts.stopSpeaking();
        valid = false;
    }

    @Override
    public void pauseSpeaking() {
        if (mTts == null)
        {
            showTip("mTts is null");
            return;
        }
        mTts.pauseSpeaking();
    }

    @Override
    public void resumeSpeaking() {
        if (mTts == null)
        {
            showTip("mTts is null");
            return;
        }
        mTts.resumeSpeaking();
    }

    @Override
    public boolean isSpeaking() {
        if (mTts == null)
        {
            showTip("mTts is null");
            return false;
        }
        return mTts.isSpeaking();
    }

    @Override
    public boolean destroy() {
        if (mTts == null)
        {
            showTip("mTts is null");
            valid = false;
            return true;
        }
        if (mTts.isSpeaking())
        {
            showTip("Tts is speaking , destroy it...");
            mTts.stopSpeaking();
        }
        valid = false;
        return mTts.destroy();
    }

    @Override
    public boolean hasInit() {
        return valid;
    }

    @Override
    public void setSpeed(int speed) {
        if (mTts == null)
        {
            showTip("mTts is null");
        }
        mTts.setParameter(SpeechConstant.SPEED,Integer.toString(speed));
    }

    @Override
    public void setVolume(int volume) {
        if (mTts == null)
        {
            showTip("mTts is null");
        }
        mTts.setParameter(SpeechConstant.VOLUME,Integer.toString(volume));
    }

    @Override
    public void setVoicer(String voicer) {
        if (mTts == null)
        {
            showTip("mTts is null");
        }
        mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);
    }

    @Override
    public int getSpeed() {
        if (mTts == null)
        {
            showTip("mTts is null");
            return -1;
        }
        return Integer.parseInt(mTts.getParameter(SpeechConstant.SPEED));
    }

    @Override
    public int getVolume() {
        if (mTts == null)
        {
            showTip("mTts is null");
            return -1;
        }
        return Integer.parseInt(mTts.getParameter(SpeechConstant.VOLUME));
    }

    @Override
    public String getVoicer() {
        if (mTts == null)
        {
            showTip("mTts is null");
            return "Null";
        }
        return mTts.getParameter(SpeechConstant.VOICE_NAME);
    }

    @Override
    public int getBufferProgress() {
        return buffer;
    }

    @Override
    public int getSpeakingProgress() {
        return speaking;
    }
    private void showTip(String text)
    {
        Log.d("mTts.class", "Tip=" + text);
    }
}
