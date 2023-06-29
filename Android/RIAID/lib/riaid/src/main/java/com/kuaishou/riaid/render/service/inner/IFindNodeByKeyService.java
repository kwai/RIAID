package com.kuaishou.riaid.render.service.inner;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.node.base.AbsObjectNode;

/**
 * 内部服务，通过key找到指定的树节点
 */
public interface IFindNodeByKeyService {

  /**
   * 通过node的唯一标识key，找到指定的node节点
   *
   * @param key node的唯一标识key
   * @return 返回找到的node节点
   */
  @Nullable
  AbsObjectNode<?> findNodeByKey(int key);

  /**
   * 获取缓存的Map
   *
   * @return 返回缓存的map
   */
  @NonNull
  Map<Integer, AbsObjectNode<?>> getNodeCacheMap();


}
