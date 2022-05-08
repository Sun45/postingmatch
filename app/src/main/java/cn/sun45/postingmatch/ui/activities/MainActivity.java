package cn.sun45.postingmatch.ui.activities;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.sun45.postingmatch.R;
import cn.sun45.postingmatch.database.MyDatabase;
import cn.sun45.postingmatch.database.PostingDao;
import cn.sun45.postingmatch.database.PostingModel;
import cn.sun45.postingmatch.framework.MyApplication;
import cn.sun45.postingmatch.framework.logic.RequestListener;
import cn.sun45.postingmatch.logic.PostingLogic;
import cn.sun45.postingmatch.server.PostingServer;
import cn.sun45.postingmatch.server.PostingServerListener;
import cn.sun45.postingmatch.tts.TtsManager;
import cn.sun45.postingmatch.ui.views.postinglist.PostingList;
import cn.sun45.postingmatch.ui.views.postinglist.PostingListListener;
import cn.sun45.postingmatch.util.PostingUtil;
import cn.sun45.postingmatch.util.Utils;

/**
 * Created by Sun45 on 2022/1/31
 */
public class MainActivity extends AppCompatActivity implements ServiceConnection, PostingServerListener {
    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout mRefresh;
    private PostingList mPostingList;

    private PostingLogic mPostingLogic;

    private boolean loading;

    private int page = 1;
    private int limit = 25;

    private int pullLimit = 5;

    private boolean inback = false;

    private FrameLayout showLay;
    private TextView showText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initNet();
    }


    @Override
    protected void onPause() {
        super.onPause();
        inback = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        inback = false;
    }

    private void initData() {
        if (!Utils.canDrawOverlays()) {
            Utils.requestAlertWindowPermissions(this);
        }
        ignoreBatteryOptimization(this);
    }

    private void initView() {
        mRefresh = findViewById(R.id.refresh);
        mPostingList = findViewById(R.id.postinglist);
        showLay = new FrameLayout(this);
        showText = new TextView(this);
        showText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        showText.setBackgroundColor(Color.WHITE);
        showText.setTextColor(Color.BLACK);
        showLay.addView(showText);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mPostingList.setListener(new PostingListListener() {
            @Override
            public void scrollToBottom() {
                loadPage();
            }
        });
        Utils.addViewToWindow(showLay, false);
    }

    private void initNet() {
        mPostingLogic = new PostingLogic();
        refresh();
        Intent intent = new Intent(this, PostingServer.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    private void refresh() {
        if (!loading) {
            page = 1;
            getPostingList(false);
        }
    }

    private void loadPage() {
        if (!loading) {
            page++;
            getPostingList(false);
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Utils.logD(TAG, "onServiceConnected");
        ((PostingServer.MyBinder) service).getService().setListener(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Utils.logD(TAG, "onServiceDisconnected");
    }

    @Override
    public void pullNewPosting() {
        Utils.logD(TAG, "pullNewPosting");
        if (inback) {
            Utils.logD(TAG, "auto pull");
            getPostingList(true);
        }
    }

    private void getPostingList(boolean pull) {
        Utils.logD(TAG, "getPostingList pull:" + pull + " page:" + page);
        loading = true;
        mPostingLogic.getPostingList(pull ? 1 : page, pull ? pullLimit : limit, new RequestListener<List<PostingModel>>() {
            @Override
            public void onSuccess(List<PostingModel> result) {
                if (result != null) {
                    Utils.logD(TAG, "getPostingList result.size():" + result.size());
                    loading = false;
                    if (!pull) {
                        mRefresh.setRefreshing(false);
                        if (page == 1) {
                            mPostingList.setData(result);
                        } else {
                            mPostingList.appendData(result);
                        }
                    }
//                Log.d(TAG, "==========");
//                for (PostingModel m : result) {
////                    if (PostingUtil.matchPosting(m.getTitle())) {
////                        Log.d(TAG, m.getTitle());
////                    }
//                    int n = PostingUtil.compare(m.getTitle());
//                    if (n == 100 || n == 1) {
//                        Log.d(TAG, m.getTitle() + " " + n);
//                    }
////                    if (m.getTitle().matches(".*行会招人.*")) {
////                        Log.d(TAG, m.getTitle());
////                    }
//                }
//                Log.d(TAG, "==========");
                    insertAndfindDifference(result);
                } else {
                    Utils.logD(TAG, "getPostingList " + null);
                }
            }

            @Override
            public void onFailed(String message) {
                loading = false;
                mRefresh.setRefreshing(false);
            }
        });
    }

    private void insertAndfindDifference(List<PostingModel> models) {
        if (models != null && models.size() > 0) {
            new Thread() {
                @Override
                public void run() {
                    List<PostingModel> newModelList = new ArrayList<>();
                    PostingDao postingDao = MyDatabase.getInstance(MainActivity.this).postingDao();
                    for (int i = 0; i < models.size(); i++) {
                        PostingModel postingModel = models.get(i);
                        if (postingDao.getPostingById(postingModel.getId()) == null) {
                            if (i <= 20 && match(postingModel)) {
                                newModelList.add(postingModel);
                            }
                            postingDao.insert(postingModel);
                        }
                    }
                    notice(newModelList);
                    super.run();
                }
            }.start();
        }
    }

    private boolean match(PostingModel postingModel) {
        String title = postingModel.getTitle();
        Utils.logD(TAG, "match title:" + title);
        boolean result = PostingUtil.matchPosting(title);
        Utils.logD(TAG, "result:" + result);
        return result;
    }

    private void notice(List<PostingModel> newModelList) {
        Utils.logD(TAG, "hasNew size:" + newModelList.size());
        String showContent = "";
        if (newModelList.size() > 0) {
            tts(newModelList.size() + "个匹配数据");
            boolean first = true;
            for (int i = 0; i < newModelList.size(); i++) {
                PostingModel model = newModelList.get(i);
                String title = model.getTitle();
//        playSound(R.raw.b);
                TtsManager.getInstance().speek(title);
                if (first) {
                    first = false;
                } else {
                    showContent += "\n";
                }
                showContent += title;
            }
        } else {
//            TtsManager.getInstance().speek("无匹配");
//            playSound(R.raw.water);
            showContent = "无匹配";
        }
        String finalShowContent = showContent;
        showText.post(new Runnable() {
            @Override
            public void run() {
                showText.setText(finalShowContent);
            }
        });
    }

    private void ignoreBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if (!hasIgnored) {
                //未加入电池优化的白名单 则弹出系统弹窗供用户选择(这个弹窗也是一个页面)
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                startActivity(intent);
            } else {
                //已加入电池优化的白名单 则进入系统电池优化页面
                Intent powerUsageIntent = new Intent("ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
                ResolveInfo resolveInfo = activity.getPackageManager().resolveActivity(powerUsageIntent, 0);
                //判断系统是否有这个页面
                if (resolveInfo != null) {
                    startActivity(powerUsageIntent);
                }
            }
        }
    }

    /**
     * 适合播放声音短，文件小
     * 可以同时播放多种音频
     * 消耗资源较小
     */
    public static void playSound(int rawId) {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        }
        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        soundPool.load(MyApplication.application, rawId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1, 0.5f, 0.5f, 0, 0, 1);
            }
        });
        //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
        //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
//        soundPool.play(1, 1, 1, 0, 0, 1);
        //回收Pool中的资源
//        soundPool.release();
    }

    public static void shake() {
        Vibrator vibrator = (Vibrator) MyApplication.application.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

    public static void tts(String content) {
        TtsManager.getInstance().speek(content);
    }
}
