package com.kuaishou.riaid.render.helper;

import java.util.Iterator;
import java.util.LinkedList;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.node.base.AbsObjectNode;

/**
 * 这个是保证先对顺序的优先级队列，发现PriorityQueue会打乱相对次序，故重写一个
 * <p>
 * 举个栗子：再用PriorityQueue的时候，如果此时数组有三个元素，1-2-3号，其中1-2
 * 的优先级都是-1，3号的优先级是4，这个时候我需要的排序是，3-1-2，但是PriorityQueue给我的是3-2-1
 */
public class PriorityList implements Iterable<AbsObjectNode<?>> {

  @NonNull
  private final LinkedList<AbsObjectNode<?>> mRendChildList = new LinkedList<>();

  @NonNull
  private final LinkedList<AbsObjectNode<?>> mHelpChildList = new LinkedList<>();

  public void addRenderChild(AbsObjectNode<?> nodeObject) {
    if (nodeObject == null) {
      return;
    }
    int priority = nodeObject.mNodeInfo.layout.priority;
    for (int i = mRendChildList.size() - 1; i >= 0; i--) {
      if (mRendChildList.get(i).mNodeInfo.layout.priority < priority) {
        mHelpChildList.addFirst(mRendChildList.remove(i));
      } else {
        break;
      }
    }
    mRendChildList.add(nodeObject);
    mRendChildList.addAll(mHelpChildList);
    mHelpChildList.clear();
  }

  @NonNull
  @Override
  public Iterator<AbsObjectNode<?>> iterator() {
    return mRendChildList.iterator();
  }

}
