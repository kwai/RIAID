package com.kuaishou.riaid.render.node.layout;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.ButtonAttributes;
import com.kuaishou.riaid.render.impl.touch.CommonHandlerImpl;
import com.kuaishou.riaid.render.impl.touch.CommonPressImpl;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.common.AbsWithContainerLayoutNode;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 这个是按钮的VG，实际按钮本来就是一个VG
 */
public class ButtonLayoutNode extends AbsWithContainerLayoutNode<ButtonLayoutNode.ButtonAttrs> {

  public ButtonLayoutNode(@NonNull NodeInfo<ButtonAttrs> nodeInfo) {
    super(nodeInfo);
  }

  @Nullable
  private View mForegroundView = null;

  @NonNull
  private final FrameLayout mButtonDecor = new FrameLayout(mNodeInfo.context.realContext);

  @NonNull
  @Override
  public View getRealView() {
    View realView = super.getRealView();
    return realView == null ? mButtonDecor : realView;
  }

  @Nullable
  @Override
  public View getGestureView() {
    return mForegroundView;
  }

  @NonNull
  @Override
  protected ViewGroup getRealContainer() {
    return mButtonDecor;
  }

  @Override
  public void onPressStart(boolean fromOutside) {
    super.onPressStart(true);
  }

  @Override
  public void onPressEnd(boolean fromOutside) {
    super.onPressEnd(true);
  }

  @Override
  public void loadAttributes() {
    // button需要遍历添加属性
    AbsObjectNode<?> contentNode = mNodeInfo.attrs.contentNode;
    if (contentNode != null) {
      addView(contentNode);
      contentNode.loadAttributes();
      contentNode.loadLayout();
      List<ButtonAttributes.HighlightState> pressStateList = mNodeInfo.attrs.pressStateList;
      if (ToolHelper.isListValid(pressStateList)) {
        // 让子组件填充属性
        inflatePressAttrs(pressStateList);
      }
    } else {
      ADRenderLogger.w("key = " + mNodeInfo.context.key + " button content null");
    }
  }

  @Override
  protected void layoutGroupParams() {
    // 创建按压蒙层，并且绑定布局参数
    Context context = mNodeInfo.context.realContext;
    boolean isPressMapValid = ToolHelper.isListValid(mNodeInfo.attrs.pressStateList);
    // 证明要按压状态，需要增加透明图层
    boolean handlerValid = mNodeInfo.handler != null;
    if (isPressMapValid || handlerValid) {
      mForegroundView = mForegroundView == null ? new View(context) : mForegroundView;
      ToolHelper.bindViewId(mForegroundView, mNodeInfo.context.key);
      LayoutPerformer.setFixedLayoutParams(mForegroundView, size.width, size.height);
      mForegroundView.setBackground(new ColorDrawable(Color.TRANSPARENT));
    } else {
      ADRenderLogger.d("key = " + mNodeInfo.context.key + " 没有按压态，也没有点击时间，不需要设置按压层");
    }
  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {
    AbsObjectNode<?> contentRender = mNodeInfo.attrs.contentNode;
    if (contentRender != null) {
      // 首先要找到最大尺寸，用来约束子View
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
      contentRender.onMeasure(maxWidth, maxHeight);

      size.width = contentRender.size.width;
      size.height = contentRender.size.height;
    } else {
      size.width = LayoutPerformer
          .getSideValueByMode(mNodeInfo.layout.width, mNodeInfo.layout.width, widthSpec);
      size.height = LayoutPerformer
          .getSideValueByMode(mNodeInfo.layout.height, mNodeInfo.layout.height, heightSpec);
    }
  }

  @Override
  public void onLayout() {
    AbsObjectNode<?> contentRender = mNodeInfo.attrs.contentNode;
    if (contentRender != null) {
      contentRender.onLayout();
    }
  }

  @Override
  public void draw(@NonNull ViewGroup decor) {
    AbsObjectNode<?> contentRender = mNodeInfo.attrs.contentNode;
    // 添加
    LayoutPerformer.addView(decor, mButtonDecor);
    if (contentRender != null) {
      // 子组件的添加
      contentRender.onDraw(mButtonDecor);
    }
  }

  @Override
  protected void drawForeground(@NonNull ViewGroup decor) {
    boolean isPressMapValid = ToolHelper.isListValid(mNodeInfo.attrs.pressStateList);
    // 证明要按压状态，需要增加透明图层
    boolean handlerValid = mNodeInfo.handler != null;
    if ((handlerValid || isPressMapValid) && mForegroundView != null) {
      LayoutPerformer.addView(mButtonDecor, mForegroundView);
      GestureDetector wrapper = new GestureDetector(mNodeInfo.context);
      // 绑定按压态
      if (isPressMapValid) {
        wrapper.setPressListener(new CommonPressImpl(mNodeInfo.context, this));
      }
      // 绑定单击，双击，长按
      if (handlerValid) {
        IResumeActionService service = getService(IResumeActionService.class);
        wrapper.setHandlerListener(
            new CommonHandlerImpl(mNodeInfo.handler, mNodeInfo.context, service));
      }
      wrapper.initGestureDetector(mForegroundView);
    }
  }

  @NonNull
  @Override
  protected ButtonAttrs createLayoutAttrs() {
    return new ButtonAttrs();
  }

  public static final class ButtonAttrs extends UIModel.Attrs {
    public AbsObjectNode<?> contentNode;
    public List<ButtonAttributes.HighlightState> pressStateList;
  }
}
