package com.kuaishou.riaid.render.service.inner;

/**
 * 内部服务，渲染时长等
 */
public class NodeDurationService {

  private long mTotalDuration = 0;

  public void durationAdd(long duration) {
    mTotalDuration += duration;
  }

  public long getTotalDuration() {
    return mTotalDuration;
  }
}
