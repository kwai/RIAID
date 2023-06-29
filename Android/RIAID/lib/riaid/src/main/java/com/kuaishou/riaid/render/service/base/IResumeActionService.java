package com.kuaishou.riaid.render.service.base;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.model.UIModel;

/**
 * 这个是行为的消费接口
 * 宿主业务方来实现
 */
public interface IResumeActionService {

  /**
   * 消费事件
   *
   * @param actionType 事件类型，比如click，scroll
   * @param responder  这个是具体的事件，包括上下文
   */
  void resumeRenderAction(int actionType, @NonNull UIModel.NodeContext context,
      @NonNull UIModel.Responder responder);

  /**
   * 这个定义的是事件类型
   */
  int ACTION_TYPE_CLICK = 0;
  int ACTION_TYPE_LONG_PRESS = 1;
  int ACTION_TYPE_DOUBLED_CLICK = 2;
  int ACTION_TYPE_VIDEO_IMPRESSION = 3;
  int ACTION_TYPE_VIDEO_FINISH = 4;
  int ACTION_TYPE_VIDEO_PAUSE = 5;
  int ACTION_TYPE_VIDEO_START = 6;
  int ACTION_TYPE_VIDEO_RESUME = 7;
  int ACTION_TYPE_LOTTIE_START = 8;
  int ACTION_TYPE_LOTTIE_END = 9;
  int ACTION_TYPE_LOTTIE_REPLACE_IMAGE_SUCCESS = 10;
  int ACTION_TYPE_LOTTIE_REPLACE_IMAGE_FAIL = 11;
}
