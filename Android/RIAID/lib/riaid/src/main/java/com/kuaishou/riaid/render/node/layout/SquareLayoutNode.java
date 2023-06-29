package com.kuaishou.riaid.render.node.layout;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.common.AbsNoContainerLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;

/**
 * 这个是可以宽高相等的盒子,只能包裹一个View
 */
public class SquareLayoutNode extends AbsNoContainerLayoutNode<UIModel.Attrs> {

  public SquareLayoutNode(@NonNull NodeInfo<UIModel.Attrs> nodeInfo) {
    super(nodeInfo);
  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {
    int childCount = childList.size();
    if (childCount > 1) {
      // 当前容器只能包裹一个
      ADRenderLogger
          .w("key = " + mNodeInfo.context.key + " SquareLayoutRender can only have one child");
      AbsObjectNode<?> onlyChildNode = childList.remove(0);
      childList.clear();
      childList.add(onlyChildNode);
    }
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
    // 需要宽高相等，也就是说需要获取宽高的最小值作为边长
    int squareSize = LayoutPerformer.getMinSize(maxWidth, maxHeight);
    size.width = squareSize;
    size.height = squareSize;
    // 抛出padding就是
    int restWidth = squareSize - mNodeInfo.layout.padding.start - mNodeInfo.layout.padding.end;
    int restHeight = squareSize - mNodeInfo.layout.padding.top - mNodeInfo.layout.padding.bottom;
    int hMargin, vMargin;
    for (AbsObjectNode<?> childView : priorityChildList) {
      hMargin = childView.mNodeInfo.layout.margin.start + childView.mNodeInfo.layout.margin.end;
      vMargin = childView.mNodeInfo.layout.margin.top + childView.mNodeInfo.layout.margin.bottom;
      childView.mNodeInfo.layout.width = RIAIDConstants.Render.MATCH_PARENT;
      childView.mNodeInfo.layout.height = RIAIDConstants.Render.MATCH_PARENT;
      childView.onMeasure(restWidth - hMargin, restHeight - vMargin);
    }
  }

  @Override
  public void onLayout() {
    // 定义布局边界
    int ls = mNodeInfo.layout.padding.start, ts = mNodeInfo.layout.padding.top;
    for (AbsObjectNode<?> childView : childList) {
      deltaMap.put(childView,
          new UIModel.Point(ls + childView.mNodeInfo.layout.margin.start,
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
