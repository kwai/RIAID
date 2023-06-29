package com.kuaishou.riaid.render.impl.inner;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.service.inner.IFindNodeByKeyService;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 服务的实现类
 */
public class FindNodeByKeyImpl implements IFindNodeByKeyService {

  @NonNull
  private final Map<Integer, AbsObjectNode<?>> mNodeCacheMap;

  public FindNodeByKeyImpl(@NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    this.mNodeCacheMap = nodeCacheMap;
  }

  @Nullable
  @Override
  public AbsObjectNode<?> findNodeByKey(int key) {
    if (ToolHelper.isNodeKeyValid(key)) {
      return mNodeCacheMap.get(key);
    }
    return null;
  }

  @Override
  @NonNull
  public Map<Integer, AbsObjectNode<?>> getNodeCacheMap() {
    return mNodeCacheMap;
  }
}
