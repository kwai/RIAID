package com.kuaishou.riaid.render.node.layout;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.common.AbsNoContainerLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;

/**
 * 这个是水平摆放的VG，可以理解是Flex的横向布局，或者是水平的LinearLayout，
 * 当然有可能排放的时候超出了，但是VG约束的大小是确定的，简单处理，直接不显示，也不截断了
 */
public class HorizontalLayoutNode extends AbsNoContainerLayoutNode<UIModel.Attrs> {

  public HorizontalLayoutNode(@NonNull NodeInfo<UIModel.Attrs> nodeInfo) {
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
    int childTotalWidth = 0, childMaxHeight = 0, childWidthSpace;
    int hMargin, vMargin;
    // 开始支持权重布局
    int hTotalMargin = 0, totalWeight = 0;
    // 这里面会有优先级，按照优先级来测量（用priority大小判断），首次测量，测量不需要权重的宽高
    for (AbsObjectNode<?> childView : priorityChildList) {
      if (childView.mNodeInfo.layout.weight > 0) {
        // 证明当前需要支持权重布局，等下需要二次测量的
        totalWeight += childView.mNodeInfo.layout.weight;
        // 等下需要填充父View给的空间
        childView.mNodeInfo.layout.width = RIAIDConstants.Render.MATCH_PARENT;
        hTotalMargin +=
            childView.mNodeInfo.layout.margin.start + childView.mNodeInfo.layout.margin.end;
        continue;
      }
      hMargin = childView.mNodeInfo.layout.margin.start + childView.mNodeInfo.layout.margin.end;
      vMargin = childView.mNodeInfo.layout.margin.top + childView.mNodeInfo.layout.margin.bottom;
      childView.onMeasure(restWidth - hMargin, restHeight - vMargin);
      childWidthSpace =
          childView.size.width
              + childView.mNodeInfo.layout.margin.start
              + childView.mNodeInfo.layout.margin.end;
      // 水平排放，宽度会变小，但是，高度是不会减小的
      restWidth -= childWidthSpace;
      childTotalWidth += childWidthSpace;
      childMaxHeight = Math.max(childMaxHeight,
          childView.size.height
              + childView.mNodeInfo.layout.margin.top
              + childView.mNodeInfo.layout.margin.bottom);
    }

    // 二次测量，给权重的子组件，分配具体的尺寸，当然如果有权重的话
    if (totalWeight > 0) {
      // 计算出剩余的可用空间
      restWidth -= hTotalMargin;
      int childWeight;
      for (AbsObjectNode<?> childView : priorityChildList) {
        childWeight = childView.mNodeInfo.layout.weight;
        vMargin =
            childView.mNodeInfo.layout.margin.top + childView.mNodeInfo.layout.margin.bottom;
        if (childWeight > 0) {
          // 测量一下
          childView.onMeasure(
              (int) Math.floor(1.0F * restWidth * childWeight / totalWeight), restHeight - vMargin);
          // 计算当前占据的空间
          childWidthSpace =
              childView.size.width
                  + childView.mNodeInfo.layout.margin.start
                  + childView.mNodeInfo.layout.margin.end;
          childTotalWidth += childWidthSpace;
          childMaxHeight = Math.max(childMaxHeight,
              childView.size.height
                  + childView.mNodeInfo.layout.margin.top
                  + childView.mNodeInfo.layout.margin.bottom);
        }
      }
    }

    // 接下来重新约束一下尺寸

    if (isWidthFixed) {
      size.width = maxWidth;
    } else {
      size.width += childTotalWidth;
      size.width =
          LayoutPerformer.getSideValueByMode(mNodeInfo.layout.width, size.width, widthSpec);
    }

    if (isHeightFixed) {
      size.height = maxHeight;
    } else {
      size.height += childMaxHeight;
      size.height =
          LayoutPerformer.getSideValueByMode(mNodeInfo.layout.height, size.height, heightSpec);
    }
  }

  @Override
  public void onLayout() {
    // 定义布局边界
    int ls = mNodeInfo.layout.padding.start, ts = mNodeInfo.layout.padding.top;
    int le = ls + size.width - mNodeInfo.layout.padding.end - mNodeInfo.layout.padding.start;
    int curL = ls;
    // 具体的排放逻辑
    for (AbsObjectNode<?> childView : childList) {
      curL += childView.mNodeInfo.layout.margin.start;
      // 具体的排放
      deltaMap.put(childView, new UIModel.Point(curL, ts + childView.mNodeInfo.layout.margin.top));
      curL += childView.size.width + childView.mNodeInfo.layout.margin.end;
      childView.onLayout();
    }
  }

  @NonNull
  @Override
  protected UIModel.Attrs createLayoutAttrs() {
    return new UIModel.Attrs();
  }
}
