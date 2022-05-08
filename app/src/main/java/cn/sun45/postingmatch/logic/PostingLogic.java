package cn.sun45.postingmatch.logic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.sun45.postingmatch.database.PostingModel;
import cn.sun45.postingmatch.framework.logic.BaseLogic;
import cn.sun45.postingmatch.framework.logic.RequestListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Sun45 on 2022/2/1
 * 帖子请求接口
 */
public class PostingLogic extends BaseLogic {
    public interface Posting {
        @GET("web")
        Call<JSONObject> getPostingList(@Query("method") String method, @Query("forum_id") String forum_id, @Query("page") int page, @Query("limit") int limit, @Query("sort") String sort, @Query("get_sub_forum_posts") String get_sub_forum_posts);
    }

    /**
     * 获取帖子列表
     *
     * @param listener
     */
    public Call<JSONObject> getPostingList(int page, int limit, final RequestListener<List<PostingModel>> listener) {
        Call call = retrofit("https://www.bigfun.cn/api/client/").create(Posting.class).getPostingList("getForumPostList", "381", page, limit, "new", "1");
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                JSONObject result = response.body();
                logD("web", "result", result);
                if (result == null) {
                    listener.onSuccess(null);
                    return;
                }
                List<PostingModel> list = new ArrayList<>();
                JSONArray data = result.optJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.optJSONObject(i);
                    long id = object.optLong("id");
                    String title = object.optString("title");
                    String content = object.optString("content");
                    long servertime = object.optLong("server_time");
                    long posttime = object.optLong("post_time");
                    PostingModel postingModel = new PostingModel();
                    postingModel.setId(id);
                    postingModel.setTitle(title);
                    postingModel.setContent(content);
                    list.add(postingModel);
//                    int n = -1;
//                    for (int j = 0; j < list.size(); j++) {
//                        PostingModel model = list.get(j);
//                        if (postingModel.getId() >= model.getId()) {
//                            n = j;
//                            break;
//                        }
//                    }
//                    if (n == -1) {
//                        list.add(postingModel);
//                    } else {
//                        list.add(n, postingModel);
//                    }
                }
                listener.onSuccess(list);
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                listener.onFailed(t.getMessage());
            }
        });
        return call;
    }
}
