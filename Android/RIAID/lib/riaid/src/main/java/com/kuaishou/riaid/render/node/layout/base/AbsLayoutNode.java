package com.kuaishou.riaid.render.node.layout.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.ButtonAttributes;
import com.kuaishou.riaid.render.helper.PriorityList;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.util.Pb2Model;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 这个布局相关的基类，负责UI组件的布局，并且负责子View的计算排布，并且每个VG的宽高都是确定的
 */
public abstract class AbsLayoutNode<T extends UIModel.Attrs> extends AbsObjectNode<T> {

  /**
   * 这个存放所有需要布局的子View
   */
  @NonNull
  protected List<AbsObjectNode<?>> childList = new ArrayList<>();

  /**
   * 这个用来临时存放排序之后的子View
   */
  @NonNull
  protected PriorityList priorityChildList = new PriorityList();

  /**
   * 子组件相对与自己，自己是不是最顶的View啦
   * 举一个例子：
   * 如果当前是ScrollView，那么ScrollView的子组件应该应该放在ScrollView中，摆放时候位置只能相对ScrollView，
   * 而不能相对最根的FrameLayout
   */
  public boolean isDecor;

  /**
   * 需要有一个map，这里存放着，当前View相对父VG的偏移量
   */
  @NonNull
  protected Map<AbsObjectNode<?>, UIModel.Point> deltaMap = new HashMap<>();

  public AbsLayoutNode(@NonNull NodeInfo<T> nodeInfo) {
    super(nodeInfo);
    isDecor = isDecor();
  }

  @Override
  public void loadAttributes() {
    loadLayoutAttributes();
    for (AbsObjectNode<?> childView : childList) {
      childView.loadAttributes();
    }
  }

  /**
   * 这个是绑定自身的属性，保证在绑定子组件属性之前，单独定义一个方法
   */
  protected void loadLayoutAttributes() {

  }

  @Override
  public void loadLayout() {
    loadLayoutBefore();
    for (AbsObjectNode<?> childNode : childList) {
      childNode.loadLayout();
    }
  }

  /**
   * 在加载layout属性之前调用
   */
  protected void loadLayoutBefore() {

  }

  /**
   * 获取包装的decor
   */
  @Nullable
  protected abstract ViewGroup getDecorView();

  @Nullable
  @Override
  public abstract View getRealView();

  @Override
  public void updateViewInfo(@Nullable IViewInfo showingViewInfo) {
    super.updateViewInfo(showingViewInfo);
    for (AbsObjectNode<?> childNode : childList) {
      childNode.updateViewInfo(childNode.showingViewInfo);
    }
  }

  @Override
  public void draw(@NonNull ViewGroup decor) {
    for (AbsObjectNode<?> childView : childList) {
      // 这里可以获取每个Render的正式尺寸，是不是被约束了
      ViewGroup decorView = getDecorView();
      childView.onDraw(decorView == null ? decor : decorView);
    }
  }

  @CallSuper
  @Override
  public void onPressStart(boolean fromOutside) {
    refreshPressUI(mNodeInfo.pressAttrs);
    for (AbsObjectNode<?> childView : childList) {
      childView.onPressStart(fromOutside);
    }
  }

  @CallSuper
  @Override
  public void onPressEnd(boolean fromOutside) {
    refreshPressUI(mNodeInfo.attrs);
    for (AbsObjectNode<?> childView : childList) {
      childView.onPressEnd(fromOutside);
    }
  }

  /**
   * 刷新按压态的ui样式
   *
   * @param attrs ui样式对象
   */
  protected abstract void refreshPressUI(@Nullable UIModel.Attrs attrs);

  @Override
  public void inflatePressAttrs(@NonNull List<ButtonAttributes.HighlightState> pressStateList) {
    int key;
    key = mNodeInfo.context.key;
    if (ToolHelper.isListValid(pressStateList)) {
      for (ButtonAttributes.HighlightState highlightState : pressStateList) {
        if (highlightState != null && key == highlightState.key) {
          if (highlightState.attributes != null) {
            mNodeInfo.pressAttrs = createLayoutAttrs();
            Context realContext = mNodeInfo.context.realContext;
            Pb2Model.pressAttrs(mNodeInfo.serviceContainer, realContext, mNodeInfo.attrs,
                mNodeInfo.pressAttrs,
                highlightState.attributes);
          }
          break;
        }
      }
    }
    for (AbsObjectNode<?> childRender : childList) {
      childRender.inflatePressAttrs(pressStateList);
    }
  }

  /**
   * 当前容器是不是最顶了,如果有shadow或者背景，那么就以为这个盒子容器需要被包裹起来
   */
  protected boolean isDecor() {
    return false;
  }

  /**
   * 创建属性对象
   */
  @NonNull
  protected abstract T createLayoutAttrs();

  /**
   * 添加需要的子View
   */
  public void addView(AbsObjectNode<?> childView) {
    if (childView != null) {
      childList.add(childView);
      childView.parentView = this;
      priorityChildList.addRenderChild(childView);
    }
  }

  /**
   * 添加需要的子View集合
   */
  public void addAllViews(List<AbsObjectNode<?>> childViewList) {
    if (ToolHelper.isListValid(childViewList)) {
      for (AbsObjectNode<?> childView : childViewList) {
        addView(childView);
      }
    }
  }

  /**
   * 默认布局布局组件是不会处理的，如果想要处理，重写测方法，在super上方写自己的逻辑即可
   *
   * @param eventType  场景值，比如下载场景
   * @param keyList    这个是render的key的集合，用来找合适的render,如果为空(list=null或者list=empty)，
   *                   直接默认匹配，如果不为空，只会匹配list中存在的key
   * @param attributes 属性json，需要修改什么属性
   * @return 返回处理的结果，是不是有组件有效消费了
   */
  public boolean dispatchEvent(@NonNull String eventType, @Nullable List<Integer> keyList,
      @Nullable Attributes attributes) {
    boolean result = false;
    for (AbsObjectNode<?> childView : childList) {
      // 如果子View场景支持，并且renderKey也匹配的上，就交给你处理
      result |= childView.dispatchEvent(eventType, keyList, attributes);
    }
    return result;
  }

  /**
   * 获取所有的子View集合
   */
  public List<AbsObjectNode<?>> getAllChildViews() {
    return childList;
  }

  /**
   * 获取相对偏移量
   */
  @NonNull
  public UIModel.Point getInnerDelta(AbsObjectNode<?> renderObject) {
    UIModel.Point result = deltaMap.get(renderObject);
    return result == null ? new UIModel.Point(0, 0) : result;
  }

}
