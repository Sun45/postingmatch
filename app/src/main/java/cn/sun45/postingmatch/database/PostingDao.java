package cn.sun45.postingmatch.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by Sun45 on 2022/5/1
 * 帖子数据DAO
 */
@Dao
public interface PostingDao {
    @Insert()
    void insert(PostingModel postingModel);

    @Delete
    void delete(PostingModel postingModel);

    @Update
    void update(PostingModel postingModel);

    @Query("SELECT * FROM posting")
    List<PostingModel> getPostingList();

    @Query("SELECT * FROM posting WHERE id = :id")
    PostingModel getPostingById(long id);
}
