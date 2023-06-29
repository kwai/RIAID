package com.kuaishou.riaid.render.node.layout;

import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.common.AbsNoContainerLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;

public class VerticalLayoutNode extends AbsNoContainerLayoutNode<UIModel.Attrs> {

  public VerticalLayoutNode(@NonNull NodeInfo<UIModel.Attrs> nodeInfo) {
    super(nodeInfo);
  }

  /**
   * 子类在实现的时候需要注意一下要点：
   * uiData：是服务器下发的约束，就是要求这个盒子具体有多大，如果服务端要求了，那么就不需要
   * 需要测量所有子View的尺寸，来确定自己的大小。可以简单理解，不用当前VG不用wrap用的是具体的尺寸
   * <p>
   * 重写这个方法的时候，可以用size.width <= 0 && size.height <= 0 来判断是不是需要，可以参考
   * HorizontalRenderLayout或者VerticalRenderLayout
   *
   * @param widthSpec  这个是父View的最大宽度边界
   * @param heightSpec 这个是父View的最大高度边界
   */
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
    int childTotalHeight = 0, childMaxWidth = 0, childHeightSpace;
    // 通过优先级测量
    int hMargin, vMargin;
    // 开始支持权重布局
    int vTotalMargin = 0, totalWeight = 0;
    // 首次测量，baby
    for (AbsObjectNode<?> childView : priorityChildList) {
      if (childView.mNodeInfo.layout.weight > 0) {
        // 证明当前需要支持权重布局，等下需要二次测量的
        totalWeight += childView.mNodeInfo.layout.weight;
        // 等下需要填充父View给的空间
        childView.mNodeInfo.layout.height = RIAIDConstants.Render.MATCH_PARENT;
        vTotalMargin +=
            childView.mNodeInfo.layout.margin.top + childView.mNodeInfo.layout.margin.bottom;
        continue;
      }
      hMargin = childView.mNodeInfo.layout.margin.start + childView.mNodeInfo.layout.margin.end;
      vMargin = childView.mNodeInfo.layout.margin.top + childView.mNodeInfo.layout.margin.bottom;
      childView.onMeasure(restWidth - hMargin, restHeight - vMargin);
      childHeightSpace =
          childView.size.height
              + childView.mNodeInfo.layout.margin.top
              + childView.mNodeInfo.layout.margin.bottom;
      // 垂直排放，高度会标小，但是宽度是不会变的
      restHeight -= childHeightSpace;
      childTotalHeight += childHeightSpace;
      childMaxWidth = Math.max(childMaxWidth,
          childView.size.width
              + childView.mNodeInfo.layout.margin.start
              + childView.mNodeInfo.layout.margin.end);
    }

    // 第二次测量
    if (totalWeight > 0) {
      // 计算出剩余的可用空间
      restHeight -= vTotalMargin;
      int childWeight;
      for (AbsObjectNode<?> childView : priorityChildList) {
        childWeight = childView.mNodeInfo.layout.weight;
        hMargin = childView.mNodeInfo.layout.margin.start + childView.mNodeInfo.layout.margin.end;
        if (childWeight > 0) {
          // 测量啦啦啦
          childView.onMeasure(
              restWidth - hMargin, (int) Math.floor(1.0F * restHeight * childWeight / totalWeight));
          // 占据的空间
          childHeightSpace =
              childView.size.height
                  + childView.mNodeInfo.layout.margin.top
                  + childView.mNodeInfo.layout.margin.bottom;
          childTotalHeight += childHeightSpace;
          childMaxWidth = Math.max(childMaxWidth,
              childView.size.width
                  + childView.mNodeInfo.layout.margin.start
                  + childView.mNodeInfo.layout.margin.end);
        }
      }
    }

    // 接下来重新约束一下尺寸

    if (isWidthFixed) {
      size.width = maxWidth;
    } else {
      size.width += childMaxWidth;
      size.width =
          LayoutPerformer.getSideValueByMode(mNodeInfo.layout.width, size.width, widthSpec);
    }

    if (isHeightFixed) {
      size.height = maxHeight;
    } else {
      size.height += childTotalHeight;
      size.height =
          LayoutPerformer.getSideValueByMode(mNodeInfo.layout.height, size.height, heightSpec);
    }
  }

  @Override
  public void onLayout() {
    // 定义布局边界
    int ls = mNodeInfo.layout.padding.start, ts = mNodeInfo.layout.padding.top;
    int te = ts + size.height - mNodeInfo.layout.padding.bottom - mNodeInfo.layout.padding.top;
    int curT = ts;
    // 开始布局排放
    for (AbsObjectNode<?> childView : childList) {
      curT += childView.mNodeInfo.layout.margin.top;
      // 继续递归排放
      deltaMap
          .put(childView, new UIModel.Point(ls + childView.mNodeInfo.layout.margin.start, curT));
      curT += childView.size.height + childView.mNodeInfo.layout.margin.bottom;
      childView.onLayout();
    }
  }

  @NonNull
  @Override
  protected UIModel.Attrs createLayoutAttrs() {
    return new UIModel.Attrs();
  }
}
