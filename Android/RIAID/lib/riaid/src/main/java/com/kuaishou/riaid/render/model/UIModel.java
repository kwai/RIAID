package com.kuaishou.riaid.render.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;
import com.kuaishou.riaid.render.util.DefaultHelper;

public class UIModel {

  /**
   * 用来放布局属性的
   */
  public static final class Layout {
    // 优先级，保证测量的顺序
    public int priority = 0;
    // 只有水平和垂直排放才会用的到，并且有了weight的优先级 > priority
    public int weight = 0;
    // margin的距离，每个View都有的属性
    @NonNull
    public Edge margin = new Edge();
    // padding的距离，每个View都有的属性
    @NonNull
    public Edge padding = new Edge();
    // 默认都是自适应宽高的
    public int height = RIAIDConstants.Render.WRAP_CONTENT;
    public int width = RIAIDConstants.Render.WRAP_CONTENT;
    // 默认的最大约束是没有约束
    public int maxHeight = DefaultHelper.UNSPECIFIED;
    public int maxWidth = DefaultHelper.UNSPECIFIED;
  }

  /**
   * 用来标识尺寸大小的，主要值得是服务端下发的大小
   * 属性都是有默认值的
   */
  public static class Attrs {
    // 圆角属性（如果作用在drawable上，这个属性就是null的）
    @Nullable
    public CornerRadius cornerRadius = null;
    // 透明度
    public float alpha = 1.0F;
    // 投影
    @Nullable
    public Shadow shadow = null;
    // 背景的drawable
    @Nullable
    public Drawable backgroundDrawable = null;
    // 是否影藏
    public boolean hidden = false;
  }

  /**
   * 用来标识边界的
   */
  public static class Edge {
    public int top = 0;
    public int end = 0;
    public int start = 0;
    public int bottom = 0;
  }

  /**
   * 用来标识内部偏移量的
   */
  public static class Point {

    public int y;
    public int x;

    public Point(int x, int y) {
      this.y = y;
      this.x = x;
    }
  }

  /**
   * 用来标识尺寸的
   */
  public static class Size {
    public int width = 0;
    public int height = 0;
  }

  /**
   * 这个事件处理用的结构体，这个是服务端下发的
   */
  public static final class Handler {
    @Nullable
    public Responder click;
    @Nullable
    public Responder doubleClick;
    @Nullable
    public Responder longPress;
  }

  /**
   * 服务端下发的Video的事件
   */
  public static final class VideoHandler {
    @Nullable
    public Responder impression;
    @Nullable
    public Responder finish;
    @Nullable
    public Responder pause;
    @Nullable
    public Responder start;
    @Nullable
    public Responder resume;
  }


  /**
   * 服务端下发的Lottie的事件
   */
  public static final class LottieHandler {
    @Nullable
    public Responder start;
    @Nullable
    public Responder end;
    @Nullable
    public Responder replaceImageSuccess;
    @Nullable
    public Responder replaceImageFail;
  }

  public static final class Responder {
    @Nullable
    public int[] triggers;
  }

  /**
   * 这个是View的context
   */
  public static final class NodeContext {
    // 这个是标识这个View的，当前的唯一标识
    public int key;
    // 这个是View的最原始的model
    @NonNull
    public final Node node;
    // 渲染View需要的context
    @NonNull
    public final Context realContext;
    // 外界约束的最大尺寸
    @NonNull
    public final Size decorSize;
    // 真实的View
    @Nullable
    public IRealViewWrapper viewWrapper;

    public NodeContext(@NonNull Node node, @NonNull Context realContext,
        @NonNull Size decorSize) {
      this.node = node;
      this.decorSize = decorSize;
      this.realContext = realContext;
    }
  }

  /**
   * 这个是水平和垂直滚动容器通用的
   */
  public static final class ScrollAttrs extends Attrs {
    public boolean showScrollBar;
  }

  /**
   * 标识圆角的
   */
  public static final class CornerRadius {
    public int topRight = 0;
    public int topLeft = 0;
    public int bottomRight = 0;
    public int bottomLeft = 0;
  }

  /**
   * 投影
   */
  public static final class Shadow {
    // 这个是绘制的投影半径圆角
    public int radius = 0;
    public int offsetX = 0;
    public int offsetY = 0;
    public int color = Color.TRANSPARENT;
    // View的原始圆角
    @NonNull
    public CornerRadius cornerRadius = new CornerRadius();
  }

  /**
   * 描述富文本
   */
  public static final class RichText {
    public Handler handler;
    public Node richContent;
    public String placeHolder;
    public int richAlignMode;
  }

}
