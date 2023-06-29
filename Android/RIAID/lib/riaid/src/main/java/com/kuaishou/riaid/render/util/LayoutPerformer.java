package com.kuaishou.riaid.render.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.adapter.video.base.BaseVideoAdapter;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.node.layout.base.AbsLayoutNode;
import com.kuaishou.riaid.render.widget.ShadowView;

/**
 * 这个Util是和布局尺寸相关的方法
 */
public class LayoutPerformer {

  /**
   * 这是Wrap的LayoutParams
   *
   * @param view 需要设置Params的目标View
   */
  public static void setWrapLayoutParams(@NonNull View view) {
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT);
    view.setLayoutParams(params);
  }

  /**
   * 这个是定大小的LayoutParams
   *
   * @param view   需要设置Params的目标View
   * @param width  给定的宽度
   * @param height 给定的高度
   */
  public static void setFixedLayoutParams(@Nullable View view, int width, int height) {
    if (view != null) {
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
      view.setLayoutParams(params);
    }
  }

  /**
   * 这个是撑满布局的LayoutParams
   *
   * @param view 需要设置Params的目标View
   */
  public static void setMatchLayoutParams(@Nullable View view) {
    if (view != null) {
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.MATCH_PARENT);
      view.setLayoutParams(params);
    }
  }

  /**
   * 设置margins
   *
   * @param view 需要设置Params的目标View
   */
  public static void setMargins(@Nullable View view, int marginStart, int marginEnd, int marginTop,
      int marginBottom) {
    if (view != null && view.getLayoutParams() != null) {
      ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
      if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
        marginParams.topMargin = marginTop;
        marginParams.bottomMargin = marginBottom;
        marginParams.setMarginStart(marginStart);
        marginParams.setMarginEnd(marginEnd);
        view.setLayoutParams(marginParams);
      }
    }
  }

  /**
   * 给View设置Padding，方便调用
   *
   * @param view  目标View
   * @param frame 存放Padding
   */
  public static void setPadding(@Nullable View view, @NonNull UIModel.Edge frame) {
    if (view != null) {
      view.setPaddingRelative(frame.start, frame.top, frame.end, frame.bottom);
    }
  }

  /**
   * 这是sizeArr
   *
   * @param edgeWidth  这个VG给当前子View的约束宽度，最多就只能显示这么大区域了
   * @param edgeHeight 这个VG给当前子View的约束高度，最多就只能显示这么大区域了
   * @param size       这个是用来存放宽高的
   * @param width      这个是服务端下发的宽度尺寸,或者本地测量的推荐尺寸
   * @param height     这个是服务端下发的高度尺寸,或者本地测量的推荐尺寸
   * @return 有没有按照服务端或者自己测量的的尺寸展示，可能放不下了，那么尺寸就需要裁剪了
   */
  public static boolean setSizeArr(int edgeWidth, int edgeHeight,
      @NonNull UIModel.Size size, int width, int height) {
    size.width = Math.min(edgeWidth, width);
    size.height = Math.min(edgeHeight, height);
    // 多做一次安全校验总是没有问题的对吧
    size.width = Math.max(size.width, 0);
    size.height = Math.max(size.height, 0);
    return width != size.width || height != size.height;
  }

  /**
   * 取最小值
   *
   * @param edgeSize    这个VG给当前子View的约束，最多就只能显示这么大区域了
   * @param defaultSize 这个可能是自己需要设置的默认宽高
   * @return 返回最小值，放置超出边界
   */
  public static int getMinSize(int edgeSize, int defaultSize) {
    return Math.max(Math.min(edgeSize, defaultSize), 0);
  }

  /**
   * 根据测量模式，取值
   *
   * @param mode         测量模式
   * @param measuredSide 测量出来的推荐尺寸
   * @param boundSide    约束的边界尺寸
   * @return 根据测量模式以及约束，返回指定的尺寸
   */
  public static int getSideValueByMode(int mode, int measuredSide, int boundSide) {
    int result;
    // 如果父布局是无限大的，撑大就有点扯淡了
    if (mode == RIAIDConstants.Render.MATCH_PARENT) {
      // 这个时候需要撑满全局
      result = boundSide;
    } else if (isSizeValueFixed(mode)) {
      // 有具体的尺寸
      result = Math.min(mode, boundSide);
    } else {
      // 其他情况当做包裹内容处理
      result = Math.min(measuredSide, boundSide);
    }
    return Math.max(result, 0);
  }

  /**
   * 通过最大尺寸约束，获取真实尺寸
   *
   * @param size    测量得到的尺寸
   * @param maxSize 当前控件的最大尺寸
   * @return 返回约束的尺寸
   */
  public static int getSizeByMax(int size, int maxSize) {
    return Math.max(Math.min(size, maxSize), 0);
  }

  /**
   * 绝对坐标映射计算
   * 默认是支持RTL的，所以需要坐标的换换算
   *
   * @param renderObject 目标render组件
   * @param size         当前View的尺寸
   * @param absolutePos  存当前View的绝对坐标
   */
  public static void transformPos(@NonNull AbsObjectNode<?> renderObject, UIModel.Size size,
      @NonNull Rect absolutePos) {
    UIModel.Point delta = new UIModel.Point(0, 0);
    AbsLayoutNode<?> parentView = null;
    UIModel.Point innerDelta;
    // 如果父容器是可以
    while (renderObject.parentView != null) {
      parentView = renderObject.parentView;
      innerDelta = parentView.getInnerDelta(renderObject);
      delta.x += innerDelta.x;
      delta.y += innerDelta.y;
      // 如果父组件是滚动的，就不需要向上递归了,因为这个时候，只需要算出相对滚动容器的坐标即可
      if (parentView.isDecor) {
        break;
      }
      // 继续向上递归呀
      renderObject = parentView;
    }
    absolutePos.left = delta.x;
    absolutePos.top = delta.y;
    // 确定Margin的end和bottom
    if (parentView != null) {
      absolutePos.right = Math.max(parentView.size.width - (absolutePos.left + size.width), 0);
      absolutePos.bottom = Math.max(parentView.size.height - (absolutePos.top + size.height), 0);
    }
  }

  /**
   * 把target视图添加到画布上
   *
   * @param decor 这个是显示的root view
   * @param view  要显示的正式View
   */
  public static void addView(@NonNull ViewGroup decor, @Nullable View view) {
    if (view != null) {
      if (view.getParent() == null) {
        decor.addView(view);
      } else {
        ADRenderLogger.e(view.getClass().getName() + "  view already has parent");
        if (view.getParent() instanceof ViewGroup) {
          ((ViewGroup) view.getParent()).removeView(view);
          decor.addView(view);
        } else {
          ADRenderLogger.e(view.getClass().getName() + "  parent is not view group");
        }
      }
    }
  }

  /**
   * 适配适配的位置和尺寸
   *
   * @param targetView   这个是目标的需要摆放的View
   * @param adapterModel 这个是位置和尺寸信息
   */
  public static void changeVideoSizeAndPosition(@NonNull View targetView,
      @NonNull BaseVideoAdapter.AdapterModel adapterModel) {
    ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
      ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
      marginParams.width = adapterModel.newVideoSize.width;
      marginParams.height = adapterModel.newVideoSize.height;
      marginParams.setMarginStart(adapterModel.deltaPoint.x);
      marginParams.topMargin = adapterModel.deltaPoint.y;
      targetView.setLayoutParams(marginParams);
    }
  }

  /**
   * 修改添加到画布的布局参数
   *
   * @param view        要显示的正式View
   * @param absolutePos 这个View相对decor的偏移量
   * @param shadow      阴影偏移量
   */
  public static void requestLayoutByAbsolutePos(View view, Rect absolutePos,
      @Nullable UIModel.Shadow shadow) {
    if (view != null) {
      ViewGroup.LayoutParams params = view.getLayoutParams();
      if (params instanceof ViewGroup.MarginLayoutParams) {
        // 基本都是可以转换成功的
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
        // 高度已经指定了，这个时候只需要指定start和top即可
        int marginStart = absolutePos.left, marginTop = absolutePos.top;
        int marginEnd = absolutePos.right, marginBottom = absolutePos.bottom;
        // 如果有shadow，那么整个shadow容器需要往start方向和top方向移动，start和top的距离变小了
        // 自然end和bottom就会变大
        if (shadow != null) {
          marginStart -= ShadowView.getXPadding(shadow);
          marginEnd += ShadowView.getXPadding(shadow);
          marginTop -= ShadowView.getYPadding(shadow);
          marginBottom += ShadowView.getYPadding(shadow);
        }
        marginParams.topMargin = marginTop;
        marginParams.bottomMargin = marginBottom;
        marginParams.setMarginStart(marginStart);
        marginParams.setMarginEnd(marginEnd);
        view.setLayoutParams(marginParams);
      }
    }
  }

  /**
   * 判断当前View是不是有效，如果没有宽高了，还显示个锤子哦
   *
   * @param renderObject 需要检测的目标对象
   * @return 返回当前对象是不是可以渲染
   */
  public static boolean canRender(AbsObjectNode<?> renderObject) {
    return renderObject.size.width > 0 && renderObject.size.height > 0;
  }

  /**
   * 获取宽高的尺寸
   *
   * @param context 就是context
   * @param size    当前服务器下发的尺寸
   * @return 根据测量模式，完成尺寸转换
   */
  public static int getSize(@NonNull Context context, float size) {
    return size < 0 ? (int) size : ToolHelper.dip2px(context, size);
  }

  /**
   * 重新指定确定边界尺寸
   *
   * @param size      当前根据测量得到的尺寸
   * @param decorSize 父容器所能提供的最大尺寸
   * @return 返回一个边界尺寸
   */
  public static int edgeSize(int size, int decorSize) {
    return size > 0 ? Math.min(size, decorSize) : decorSize;
  }

  /**
   * 判断当前尺寸是不是fixed的，如果服务端下发的尺寸是>=0的，就以为这，该控件只能这么大
   *
   * @param sizeValue 当前的边的值大小
   * @return 返回是不是fixed的结果
   */
  public static boolean isSizeValueFixed(int sizeValue) {
    return sizeValue >= 0;
  }
}
