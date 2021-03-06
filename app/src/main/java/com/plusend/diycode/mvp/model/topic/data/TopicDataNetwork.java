package com.plusend.diycode.mvp.model.topic.data;

import android.util.Log;
import com.plusend.diycode.mvp.model.topic.entity.FavoriteTopic;
import com.plusend.diycode.mvp.model.topic.entity.FollowTopic;
import com.plusend.diycode.mvp.model.topic.entity.Reply;
import com.plusend.diycode.mvp.model.topic.entity.Topic;
import com.plusend.diycode.mvp.model.topic.entity.TopicDetail;
import com.plusend.diycode.mvp.model.topic.entity.TopicReply;
import com.plusend.diycode.mvp.model.topic.entity.UnFavoriteTopic;
import com.plusend.diycode.mvp.model.topic.entity.UnFollowTopic;
import com.plusend.diycode.mvp.model.topic.event.CreateTopicReplyEvent;
import com.plusend.diycode.mvp.model.topic.event.FavoriteEvent;
import com.plusend.diycode.mvp.model.topic.event.FollowEvent;
import com.plusend.diycode.mvp.model.topic.event.NewTopicEvent;
import com.plusend.diycode.mvp.model.topic.event.RepliesEvent;
import com.plusend.diycode.mvp.model.topic.event.TopicDetailEvent;
import com.plusend.diycode.mvp.model.topic.event.TopicRepliesEvent;
import com.plusend.diycode.mvp.model.topic.event.TopicsEvent;
import com.plusend.diycode.mvp.model.topic.event.UnFavoriteEvent;
import com.plusend.diycode.mvp.model.topic.event.UnFollowEvent;
import com.plusend.diycode.util.Constant;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TopicDataNetwork implements TopicData {
  private static final String TAG = "NetworkData";
  private TopicService service;
  private static TopicDataNetwork networkData = new TopicDataNetwork();

  private TopicDataNetwork() {
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.diycode.cc/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    service = retrofit.create(TopicService.class);
  }

  public static TopicDataNetwork getInstance() {
    return networkData;
  }

  @Override public void getTopics(String type, Integer nodeId, Integer offset, Integer limit) {
    Call<List<Topic>> call = service.getTopics(type, nodeId, offset, limit);
    call.enqueue(new Callback<List<Topic>>() {
      @Override
      public void onResponse(Call<List<Topic>> call, retrofit2.Response<List<Topic>> response) {
        if (response.isSuccessful()) {
          List<Topic> topicList = response.body();
          Log.v(TAG, "topicList:" + topicList);
          EventBus.getDefault().post(new TopicsEvent(topicList));
        } else {
          Log.e(TAG, "getTopics STATUS: " + response.code());
          EventBus.getDefault().post(new TopicsEvent(null));
        }
      }

      @Override public void onFailure(Call<List<Topic>> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new TopicsEvent(null));
      }
    });
  }

  @Override public void getTopic(int id) {
    Call<TopicDetail> call = service.getTopic(id);
    call.enqueue(new Callback<TopicDetail>() {
      @Override
      public void onResponse(Call<TopicDetail> call, retrofit2.Response<TopicDetail> response) {
        if (response.isSuccessful()) {
          TopicDetail topicDetail = response.body();
          Log.v(TAG, "getTopic topicDetail:" + topicDetail);
          EventBus.getDefault().post(new TopicDetailEvent(topicDetail));
          //Map<String, List<String>> map = response.headers().toMultimap();
          //for (Map.Entry<String, List<String>> entry : map.entrySet()) {
          //  Log.d(TAG, "Key : " + entry.getKey() + " ,Value : " + entry.getValue());
          //}
        } else {
          Log.e(TAG, "getTopic STATUS: " + response.code());
          EventBus.getDefault().post(new TopicDetailEvent(null));
        }
      }

      @Override public void onFailure(Call<TopicDetail> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new TopicDetailEvent(null));
      }
    });
  }

  @Override public void getReplies(int id, Integer offset, Integer limit) {
    Call<List<TopicReply>> call = service.getReplies(id, offset, limit);
    call.enqueue(new Callback<List<TopicReply>>() {
      @Override public void onResponse(Call<List<TopicReply>> call,
          retrofit2.Response<List<TopicReply>> response) {
        if (response.isSuccessful()) {
          List<TopicReply> topicReplyList = response.body();
          Log.v(TAG, "topicReplyList:" + topicReplyList);
          EventBus.getDefault().post(new TopicRepliesEvent(topicReplyList));
        } else {
          Log.e(TAG, "getReplies STATUS: " + response.code());
          EventBus.getDefault().post(new TopicRepliesEvent(null));
        }
      }

      @Override public void onFailure(Call<List<TopicReply>> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new TopicRepliesEvent(null));
      }
    });
  }


  @Override public void newTopic(final String title, String body, int nodeId) {
    Call<TopicDetail> call =
        service.newTopic(Constant.VALUE_TOKEN_PREFIX + Constant.VALUE_TOKEN, title, body, nodeId);
    call.enqueue(new Callback<TopicDetail>() {
      @Override public void onResponse(Call<TopicDetail> call, Response<TopicDetail> response) {
        if (response.isSuccessful()) {
          TopicDetail topicDetail = response.body();
          Log.v(TAG, "topicDetail: " + topicDetail);
          EventBus.getDefault().postSticky(new NewTopicEvent(topicDetail));
          EventBus.getDefault().post(new NewTopicEvent(topicDetail));
        } else {
          Log.e(TAG, "newTopic STATUS: " + response.code());
          EventBus.getDefault().post(new NewTopicEvent(null));
        }
      }

      @Override public void onFailure(Call<TopicDetail> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new NewTopicEvent(null));
      }
    });
  }

  @Override public void favorite(int id) {
    Call<FavoriteTopic> call =
        service.favoriteTopic(Constant.VALUE_TOKEN_PREFIX + Constant.VALUE_TOKEN, id);
    call.enqueue(new Callback<FavoriteTopic>() {
      @Override public void onResponse(Call<FavoriteTopic> call, Response<FavoriteTopic> response) {
        if (response.isSuccessful()) {
          FavoriteTopic favoriteTopic = response.body();
          Log.v(TAG, "favorite: " + favoriteTopic);
          EventBus.getDefault().post(new FavoriteEvent(favoriteTopic.getOk() == 1));
        } else {
          Log.e(TAG, "favorite STATUS: " + response.code());
          EventBus.getDefault().post(new FavoriteEvent(false));
        }
      }

      @Override public void onFailure(Call<FavoriteTopic> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new FavoriteEvent(false));
      }
    });
  }

  @Override public void unFavorite(int id) {
    Call<UnFavoriteTopic> call =
        service.unFavoriteTopic(Constant.VALUE_TOKEN_PREFIX + Constant.VALUE_TOKEN, id);
    call.enqueue(new Callback<UnFavoriteTopic>() {
      @Override
      public void onResponse(Call<UnFavoriteTopic> call, Response<UnFavoriteTopic> response) {
        if (response.isSuccessful()) {
          UnFavoriteTopic unFavoriteTopic = response.body();
          Log.v(TAG, "unFavorite: " + unFavoriteTopic);
          EventBus.getDefault().post(new UnFavoriteEvent(unFavoriteTopic.getOk() == 1));
        } else {
          Log.e(TAG, "unFavorite STATUS: " + response.code());
          EventBus.getDefault().post(new UnFavoriteEvent(false));
        }
      }

      @Override public void onFailure(Call<UnFavoriteTopic> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new UnFavoriteEvent(false));
      }
    });
  }

  @Override public void follow(int id) {
    Call<FollowTopic> call =
        service.followTopic(Constant.VALUE_TOKEN_PREFIX + Constant.VALUE_TOKEN, id);
    call.enqueue(new Callback<FollowTopic>() {
      @Override public void onResponse(Call<FollowTopic> call, Response<FollowTopic> response) {
        if (response.isSuccessful()) {
          FollowTopic followTopic = response.body();
          Log.v(TAG, "follow: " + followTopic);
          EventBus.getDefault().post(new FollowEvent(followTopic.getOk() == 1));
        } else {
          Log.e(TAG, "follow STATUS: " + response.code());
          EventBus.getDefault().post(new FollowEvent(false));
        }
      }

      @Override public void onFailure(Call<FollowTopic> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new FollowEvent(false));
      }
    });
  }

  @Override public void unFollow(int id) {
    Call<UnFollowTopic> call =
        service.unFollowTopic(Constant.VALUE_TOKEN_PREFIX + Constant.VALUE_TOKEN, id);
    call.enqueue(new Callback<UnFollowTopic>() {
      @Override public void onResponse(Call<UnFollowTopic> call, Response<UnFollowTopic> response) {
        if (response.isSuccessful()) {
          UnFollowTopic unFollowTopic = response.body();
          Log.v(TAG, "unFollow: " + unFollowTopic);
          EventBus.getDefault().post(new UnFollowEvent(unFollowTopic.getOk() == 1));
        } else {
          Log.e(TAG, "unFollow STATUS: " + response.code());
          EventBus.getDefault().post(new UnFollowEvent(false));
        }
      }

      @Override public void onFailure(Call<UnFollowTopic> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new UnFollowEvent(false));
      }
    });
  }

  @Override public void createReply(int id, String body) {
    Call<TopicReply> call =
        service.createReply(Constant.VALUE_TOKEN_PREFIX + Constant.VALUE_TOKEN, id, body);
    call.enqueue(new Callback<TopicReply>() {
      @Override public void onResponse(Call<TopicReply> call, Response<TopicReply> response) {
        if (response.isSuccessful()) {
          TopicReply topicReply = response.body();
          Log.v(TAG, "topicReply:" + topicReply);
          EventBus.getDefault().postSticky(new CreateTopicReplyEvent(true));
        } else {
          Log.e(TAG, "createReply STATUS: " + response.code());
          EventBus.getDefault().post(new CreateTopicReplyEvent(false));
        }
      }

      @Override public void onFailure(Call<TopicReply> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        EventBus.getDefault().post(new CreateTopicReplyEvent(false));
      }
    });
  }
}
