package com.kuaishou.riaid.render.node.layout;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.common.AbsWithContainerLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;

/**
 * 这个是可以垂直方向滑动的盒子
 */
public class VScrollLayoutNode extends AbsWithContainerLayoutNode<UIModel.ScrollAttrs> {

  public VScrollLayoutNode(@NonNull NodeInfo<UIModel.ScrollAttrs> nodeInfo) {
    super(nodeInfo);
  }

  @NonNull
  private final ScrollView mScrollContainer = new ScrollView(mNodeInfo.context.realContext);

  @NonNull
  private final FrameLayout mFlContainer = new FrameLayout(mNodeInfo.context.realContext);

  @NonNull
  @Override
  public View getRealView() {
    View realView = super.getRealView();
    return realView == null ? mScrollContainer : realView;
  }

  @NonNull
  @Override
  protected ViewGroup getDecorView() {
    return mFlContainer;
  }

  @NonNull
  @Override
  protected ViewGroup getRealContainer() {
    return mScrollContainer;
  }

  @Override
  public void loadLayoutAttributes() {
    mScrollContainer.setVerticalScrollBarEnabled(mNodeInfo.attrs.showScrollBar);
  }

  @Override
  public void loadLayoutBefore() {
    super.loadLayoutBefore();
    LayoutPerformer.setPadding(mScrollContainer, mNodeInfo.layout.padding);
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
    int childTotalHeight = 0, childMaxWidth = 0, childHeightSpace;
    // 通过优先级测量
    int hMargin;
    for (AbsObjectNode<?> childView : priorityChildList) {
      hMargin = childView.mNodeInfo.layout.margin.start + childView.mNodeInfo.layout.margin.end;
      // 给子组件的垂直高度是无限的
      childView.onMeasure(restWidth - hMargin, Integer.MAX_VALUE);
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
    LayoutPerformer.setFixedLayoutParams(mScrollContainer, size.width, size.height);
  }

  @Override
  protected void layoutGroupParams() {
    LayoutPerformer.setWrapLayoutParams(mFlContainer);
  }

  @Override
  public void onLayout() {
    // 定义布局边界
    int curT = 0;
    // 开始排放，当然也是不用考虑换行的情况
    for (AbsObjectNode<?> childView : childList) {
      curT += childView.mNodeInfo.layout.margin.top;
      deltaMap.put(childView, new UIModel.Point(childView.mNodeInfo.layout.margin.start, curT));
      curT += childView.size.height + childView.mNodeInfo.layout.margin.bottom;
      childView.onLayout();
    }
  }

  @Override
  public void draw(@NonNull ViewGroup decor) {
    // 创建滚动容器
    LayoutPerformer.addView(mScrollContainer, mFlContainer);
    // 添加滚动容器
    LayoutPerformer.addView(decor, mScrollContainer);
    // 子组件绘制
    super.draw(decor);
  }

  @NonNull
  @Override
  protected UIModel.ScrollAttrs createLayoutAttrs() {
    return new UIModel.ScrollAttrs();
  }

}
