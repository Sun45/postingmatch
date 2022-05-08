package cn.sun45.postingmatch.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Sun45 on 2022/2/1
 * 帖子数据模型
 */
@Entity(tableName = "posting")
public class PostingModel {
    //id
    @PrimaryKey
    @ColumnInfo
    private long id;
    //标题
    @ColumnInfo
    private String title;
    //内容
    @ColumnInfo
    private String content;

    public PostingModel() {
    }

    @Ignore
    public PostingModel(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostingModel that = (PostingModel) o;
        return id == that.getId();
    }

    @Override
    public String toString() {
        return "PostingModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
