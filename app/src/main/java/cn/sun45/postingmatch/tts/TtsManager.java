package cn.sun45.postingmatch.tts;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.Stack;

import cn.sun45.postingmatch.framework.MyApplication;
import cn.sun45.postingmatch.util.Utils;

/**
 * Created by Sun45 on 2022/4/30
 * 语音转文字管理
 */
public class TtsManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "TtsManager";

    private static TtsManager instance;

    public static TtsManager getInstance() {
        if (instance == null) {
            synchronized (TtsManager.class) {
                if (instance == null) {
                    instance = new TtsManager();
                }
            }
        }
        return instance;
    }

    private TextToSpeech textToSpeech;

    public boolean ready;

    public Queue<String> queue = new LinkedList<String>();

    public TtsManager() {
        textToSpeech = new TextToSpeech(MyApplication.application, this);
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Utils.logD(TAG, "onStart");
                ready = false;
            }

            @Override
            public void onDone(String utteranceId) {
                Utils.logD(TAG, "onDone");
                ready = true;
                speekNext();
            }

            @Override
            public void onError(String utteranceId) {
                Utils.logD(TAG, "onError");
            }
        });
    }

    @Override
    public void onInit(int status) {
        Utils.logD(TAG, "onInit status:" + status);
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.CHINA);
//                textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
//                textToSpeech.setSpeechRate(1.0f);
            ready = true;
            speekNext();
        }
    }

    public void doSpeek(String content) {
        textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, "UniqueID");
    }

    public synchronized void speek(String content) {
        Utils.logD(TAG, "speek content:" + content);
        if (textToSpeech != null && ready) {
            doSpeek(content);
        } else {
            queue.add(content);
        }
    }

    public synchronized void speekNext() {
        String content = queue.poll();
        if (!TextUtils.isEmpty(content)) {
            doSpeek(content);
        }
    }
}
