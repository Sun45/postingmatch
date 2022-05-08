package cn.sun45.postingmatch.ui.views.postinglist;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.sun45.postingmatch.database.PostingModel;
import cn.sun45.postingmatch.util.Utils;

/**
 * Created by Sun45 on 2022/1/30
 * 帖子列表
 */
public class PostingList extends RecyclerView {
    private static final String TAG = "PostingList";

    private LinearLayoutManager layoutManager;
    private PostingListAdapter adapter;

    public PostingListListener listener;

    public void setListener(PostingListListener listener) {
        this.listener = listener;
    }

    public PostingList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        adapter = new PostingListAdapter(getContext());
        setAdapter(adapter);
    }

    public void setData(List<PostingModel> list) {
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }

    public void appendData(List<PostingModel> list) {
        int oldcount = adapter.getItemCount();
        adapter.appendData(list);
        adapter.notifyItemRangeInserted(oldcount, list.size());
    }

    @Override
    public void onScrollStateChanged(int state) {
        Utils.logD(TAG, "onScrollStateChanged state:" + state);
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                if (listener != null) {
                    listener.scrollToBottom();
                }
            }
        }
    }
}
