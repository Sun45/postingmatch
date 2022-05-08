package cn.sun45.postingmatch.ui.views.postinglist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.sun45.postingmatch.R;
import cn.sun45.postingmatch.database.PostingModel;
import cn.sun45.postingmatch.util.PostingUtil;
import cn.sun45.postingmatch.util.Utils;

/**
 * Created by Sun45 on 2022/1/30
 */
public class PostingListAdapter extends RecyclerView.Adapter<PostingListAdapter.PostingHolder> {
    public static final String TAG = "PostingListAdapter";
    private Context context;

    private List<PostingListModel> list;

    public PostingListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<PostingModel> postingModelList) {
        list = new ArrayList<>();
        buildList(postingModelList);
    }

    public void appendData(List<PostingModel> postingModelList) {
        buildList(postingModelList);
    }

    private void buildList(List<PostingModel> postingModelList) {
        for (PostingModel postingModel : postingModelList) {
            list.add(new PostingListModel(postingModel));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @NonNull
    @Override
    public PostingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostingHolder(LayoutInflater.from(context).inflate(R.layout.postinglist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostingHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class PostingHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mContent;

        private PostingListModel model;

        public PostingHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mContent = itemView.findViewById(R.id.content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model.setExpand(!model.isExpand());
                    showExpandState();
                }
            });
        }

        public void setData(PostingListModel postingListModel) {
            model = postingListModel;
            PostingModel postingModel = postingListModel.getPostingModel();
            String title = postingModel.getTitle();
            if (PostingUtil.matchPosting(title)) {
                mTitle.setTextColor(Utils.getColor(R.color.theme));
            } else {
                mTitle.setTextColor(Color.BLACK);
            }
            mTitle.setText(title);
            mContent.setText(postingModel.getContent());
            showExpandState();
        }

        public void showExpandState() {
            if (model.isExpand()) {
                mTitle.setSingleLine(false);
                mContent.setVisibility(View.VISIBLE);
            } else {
                mTitle.setSingleLine(true);
                mContent.setVisibility(View.GONE);
            }
        }
    }
}
