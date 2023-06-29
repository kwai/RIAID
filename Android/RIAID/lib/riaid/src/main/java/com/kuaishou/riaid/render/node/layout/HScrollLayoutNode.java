package com.kuaishou.riaid.render.node.layout;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.common.AbsWithContainerLayoutNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;

/**
 * 这个是可以水平可以滑动的盒子
 */
public class HScrollLayoutNode extends AbsWithContainerLayoutNode<UIModel.ScrollAttrs> {

  public HScrollLayoutNode(@NonNull NodeInfo<UIModel.ScrollAttrs> nodeInfo) {
    super(nodeInfo);
  }

  @NonNull
  private final HorizontalScrollView mScrollContainer =
      new HorizontalScrollView(mNodeInfo.context.realContext);

  @NonNull
  private final FrameLayout mFlContainer = new FrameLayout(mNodeInfo.context.realContext);

  @Override
  public void loadLayoutAttributes() {
    mScrollContainer.setHorizontalScrollBarEnabled(mNodeInfo.attrs.showScrollBar);
  }

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
  public void loadLayoutBefore() {
    super.loadLayoutBefore();
    LayoutPerformer.setPadding(mScrollContainer, mNodeInfo.layout.padding);
  }

  /**
   * 首先就是和HorizontalLayoutRender的测量保持一致的，区别就是onLayout的时候，有区分
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
    int restHeight = maxHeight - mNodeInfo.layout.padding.top - mNodeInfo.layout.padding.bottom;
    int childTotalWidth = 0, childMaxHeight = 0, childWidthSpace;
    // 这里面会有优先级，按照优先级来测量
    int vMargin;
    for (AbsObjectNode<?> childView : priorityChildList) {
      vMargin = childView.mNodeInfo.layout.margin.top + childView.mNodeInfo.layout.margin.bottom;
      // 水平滑动的，水平可以给子View无限的水平测量空间，这里是和HorizontalLayoutRender不同的
      childView.onMeasure(Integer.MAX_VALUE, restHeight - vMargin);
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
  protected void layoutGroupParams() {
    LayoutPerformer.setWrapLayoutParams(mFlContainer);
  }

  @Override
  public void onLayout() {
    // 定义边界
    int curL = 0;
    // 开始排放,不用考虑换行的情况，是水平可以滑动的，是可以水平一致排布下去的
    for (AbsObjectNode<?> childView : childList) {
      curL += childView.mNodeInfo.layout.margin.start;
      deltaMap.put(childView, new UIModel.Point(curL, childView.mNodeInfo.layout.margin.top));
      curL += childView.size.width + childView.mNodeInfo.layout.margin.end;
      childView.onLayout();
    }
  }

  @Override
  public void draw(@NonNull ViewGroup decor) {
    // 首先计算好位置
    LayoutPerformer.addView(mScrollContainer, mFlContainer);
    // 添加
    LayoutPerformer.addView(decor, mScrollContainer);
    // 子组件的绘制
    super.draw(decor);
  }

  @NonNull
  @Override
  protected UIModel.ScrollAttrs createLayoutAttrs() {
    return new UIModel.ScrollAttrs();
  }

}
