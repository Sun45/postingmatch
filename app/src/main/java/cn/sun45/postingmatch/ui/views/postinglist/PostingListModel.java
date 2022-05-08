package cn.sun45.postingmatch.ui.views.postinglist;

import cn.sun45.postingmatch.database.PostingModel;

/**
 * Created by Sun45 on 2022/1/30
 * 帖子列表数据模型
 */
public class PostingListModel {
    //类型
    private int type;

    //帖子数据模型
    private PostingModel postingModel;
    //扩充展示
    private boolean expand;

    public PostingListModel(PostingModel postingModel) {
        this.postingModel = postingModel;
    }

    public int getType() {
        return type;
    }

    public PostingModel getPostingModel() {
        return postingModel;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
