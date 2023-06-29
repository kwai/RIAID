package com.kuaishou.riaid.render.node.layout;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.common.AbsNoContainerLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;

/**
 * 这个是绝对布局的VG，需要外接把每个字View的相对坐标传递进来
 */
public class AbsoluteLayoutNode extends AbsNoContainerLayoutNode<UIModel.Attrs> {

  public AbsoluteLayoutNode(@NonNull NodeInfo<UIModel.Attrs> nodeInfo) {
    super(nodeInfo);
  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {
    // 证明不是刚性的，尺寸就要通过测量子View来指定啦啦
    int maxWidth = LayoutPerformer.getSizeByMax(widthSpec, mNodeInfo.layout.maxWidth);
    int maxHeight = LayoutPerformer.getSizeByMax(heightSpec, mNodeInfo.layout.maxHeight);
    boolean isWidthFixed = LayoutPerformer.isSizeValueFixed(mNodeInfo.layout.width);
    boolean isHeightFixed = LayoutPerformer.isSizeValueFixed(mNodeInfo.layout.height);
    if (isWidthFixed) {
      maxWidth = LayoutPerformer.getMinSize(widthSpec, mNodeInfo.layout.width);
    }
    if (isHeightFixed) {
      maxHeight = LayoutPerformer.getMinSize(heightSpec, mNodeInfo.layout.height);
    }
    // padding也是尺寸的一部分
    size.width = mNodeInfo.layout.padding.start + mNodeInfo.layout.padding.end;
    size.height = mNodeInfo.layout.padding.top + mNodeInfo.layout.padding.bottom;
    // 计算出子View可用的剩余空间
    int restWidth = maxWidth - mNodeInfo.layout.padding.start - mNodeInfo.layout.padding.end;
    int restHeight = maxHeight - mNodeInfo.layout.padding.top - mNodeInfo.layout.padding.bottom;
    // 存放子组件的margin用的
    int hMargin, vMargin;
    // 统计最大尺寸
    int childMaxWidth = 0, childMaxHeight = 0;
    for (AbsObjectNode<?> childView : priorityChildList) {
      hMargin = childView.mNodeInfo.layout.margin.start + childView.mNodeInfo.layout.margin.end;
      vMargin = childView.mNodeInfo.layout.margin.top + childView.mNodeInfo.layout.margin.bottom;
      childView.onMeasure(restWidth - hMargin, restHeight - vMargin);
      childMaxWidth = Math.max(childMaxWidth,
          childView.size.width
              + childView.mNodeInfo.layout.margin.start
              + childView.mNodeInfo.layout.margin.end);
      childMaxHeight = Math.max(childMaxHeight,
          childView.size.height
              + childView.mNodeInfo.layout.margin.top
              + childView.mNodeInfo.layout.margin.bottom);
    }

    // 接下来重新约束一下尺寸


    if (isWidthFixed) {
      size.width = maxWidth;
    } else {
      // 最后做一次约束，不能超过边界
      // 但是如果是match_parent就直接是边界尺寸
      size.width += childMaxWidth;
      size.width =
          LayoutPerformer.getSideValueByMode(mNodeInfo.layout.width, size.width, widthSpec);
    }

    if (isHeightFixed) {
      size.height = maxHeight;
    } else {
      // 最后做一次约束，不能超过边界
      // 但是如果是match_parent就直接是边界尺寸
      size.height += childMaxHeight;
      size.height =
          LayoutPerformer.getSideValueByMode(mNodeInfo.layout.height, size.height, heightSpec);
    }
  }

  @Override
  public void onLayout() {
    // 定义布局边界
    int ls = mNodeInfo.layout.padding.start, ts = mNodeInfo.layout.padding.top;
    // 开始排排放
    for (AbsObjectNode<?> childView : childList) {
      deltaMap.put(childView, new UIModel.Point(
          ls + childView.mNodeInfo.layout.margin.start,
          ts + childView.mNodeInfo.layout.margin.top));
      childView.onLayout();
    }
  }

  @NonNull
  @Override
  protected UIModel.Attrs createLayoutAttrs() {
    return new UIModel.Attrs();
  }
}
