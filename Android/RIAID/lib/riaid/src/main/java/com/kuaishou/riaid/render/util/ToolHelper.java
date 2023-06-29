package com.kuaishou.riaid.render.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.kuaishou.riaid.Riaid;
import com.kuaishou.riaid.proto.nano.RiaidModel;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.logger.RiaidLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.service.base.IDataBindingService;
import com.kuaishou.riaid.render.service.base.IRIAIDLogReportService;
import com.kuaishou.riaid.render.service.inner.IFindNodeByKeyService;
import com.kuaishou.riaid.render.service.inner.NodeDurationService;
import com.kuaishou.riaid.render.widget.ShadowView;

/**
 * 这里主要放一些工具方法
 */
public class ToolHelper {

  private static final String TAG = "ToolHelper";

  /**
   * 判断当前List是不是有效非空
   *
   * @param dataList 数据源集合
   * @return 返回判断的结果，是不是非空有数据
   */
  public static boolean isListValid(final List<?> dataList) {
    return dataList != null && dataList.size() > 0;
  }

  /**
   * 判断Map是不是有效非空
   *
   * @param map 数据源map
   * @return 返回判断的结果，是不是非空有数据
   */
  public static boolean isMapValid(final Map<?, ?> map) {
    return map != null && !map.isEmpty();
  }

  /**
   * 判断Set是不是有效非空
   *
   * @param set 数据源set
   * @return 返回判断的结果，是不是非空有数据
   */
  public static boolean isSetValid(final Set<?> set) {return set != null && set.size() > 0;}

  /**
   * 判断数组是不是有效
   *
   * @param array 数据源数组
   * @param <T>   数组的具体泛型类型
   * @return 返回当前数组是不是非空，且有元素
   */
  public static <T> boolean isArrayValid(final T[] array) {
    return array != null && array.length > 0;
  }

  /**
   * 获取真正的值，最近匹配原则，举个栗子
   * 如果服务端下发的是image_url,分为一下两种情况
   * 如果value = "https://xxxxxx" 是具体的url，直接返回
   * 如果value = ${imageUrl} 这个时候需要以imageUrl为key，从map中查找
   *
   * @param service    这个是外界提供的解析占位符的service
   * @param sourceText 原始字符串
   * @return 返回处理好之后的字符串
   */
  public static String resolveValue(@Nullable final IDataBindingService service,
      final String sourceText) {
    StringBuilder sb = new StringBuilder();
    if (sourceText != null && sourceText.trim().length() > 0) {
      String matchContent;
      boolean hasMatched = false;
      int pre = 0, next = 0, length = sourceText.length();
      while (next < length) {
        if (sourceText.charAt(next) == '$' && next + 1 < length &&
            sourceText.charAt(next + 1) == '{') {
          sb.append(sourceText, pre, next);
          // 匹配上了左边
          hasMatched = true;
          pre = next;
          next += 2;
        } else {
          // 没有匹配上的话，继续移动
          if (sourceText.charAt(next) == '}') {
            // 可以替换了,准备好了
            if (hasMatched) {
              // 重置一下，防止重复
              hasMatched = false;
              if (next - pre > 2) {
                // 这个时候才证明有东西
                matchContent = sourceText.substring(pre + 2, next).trim();
                // 去获取
                if (service != null) {
                  matchContent = service.parseDataHolder(matchContent);
                }
                sb.append(TextUtils.isEmpty(matchContent) ? "" : matchContent);
              } else {
                // 没有东西,忽略
              }
              ++next;
              pre = next;
            } else {
              ++next;
            }
          } else {
            ++next;
          }
        }
      }
      // 不上最后的
      if (pre < length) {
        sb.append(sourceText, pre, length);
      }
    }
    return sb.toString();
  }

  /**
   * 把字符串转换成int
   *
   * @param source       原始目标字符串
   * @param defaultValue 默认值，当source不合法的时候使用
   * @return 返回处理转换好的value
   */
  public static int parseInt(String source, int defaultValue) {
    if (!TextUtils.isEmpty(source)) {
      try {
        return Integer.parseInt(source);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return defaultValue;
  }

  /**
   * 字符串转float
   *
   * @param source       原始目标字符串
   * @param defaultValue 默认值，当source不合法的时候使用
   * @return 返回处理转换好的value
   */
  public static float parseFloat(String source, float defaultValue) {
    if (!TextUtils.isEmpty(source)) {
      try {
        return Float.parseFloat(source);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return defaultValue;
  }

  /**
   * 把dip转换成pixel
   *
   * @param context context
   * @param dpValue dp的value
   * @return 返回四舍五入的pixel值
   */
  public static int dip2px(Context context, float dpValue) {
    float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 把pixel转换成dip
   *
   * @param context context
   * @param pxValue pixel的value
   * @return 返回转换好的dip值
   */
  public static float px2dp(Context context, int pxValue) {
    float scale = context.getResources().getDisplayMetrics().density;
    return pxValue / scale;
  }

  /**
   * 8位字符串转换颜色
   *
   * @param colorRes     8位颜色字符串 ARGB
   * @param defaultColor 转换失败使用的默认颜色
   * @return 返回处理好的颜色
   */
  public static int parseColor(@Nullable IServiceContainer serviceContainer, String colorRes,
      int defaultColor) {

    String realColorRes = colorRes;
    if (serviceContainer != null && colorRes != null && colorRes.startsWith("$")) {
      IDataBindingService service = serviceContainer.getService(IDataBindingService.class);
      // 先去获取可能会数据替换的颜色
      String parseColor = service != null ? resolveValue(service, colorRes) : "";
      if (!TextUtils.isEmpty(parseColor)) {
        // 如果替换成功，则赋值
        realColorRes = parseColor;
      }
    }
    if (!TextUtils.isEmpty(realColorRes)) {
      try {
        return Color.parseColor(realColorRes);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return defaultColor;
  }

  /**
   * 8位字符串转换颜色
   *
   * @param colorRes 8位颜色字符串 ARGB
   * @return 返回处理好的颜色
   */
  public static Integer parseColor(String colorRes) {
    if (!TextUtils.isEmpty(colorRes)) {
      try {
        return Color.parseColor(colorRes);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 根据key获取具体的View信息包裹类
   *
   * @param key      node组件的标识
   * @param rootNode 虚拟node树的头节点
   * @return 返回匹配key的node组件
   */
  @Nullable
  public static IRealViewWrapper findViewByKey(int key, @NonNull AbsObjectNode<?> rootNode) {
    IFindNodeByKeyService service =
        rootNode.mNodeInfo.serviceContainer.getService(IFindNodeByKeyService.class);
    if (service != null) {
      return service.findNodeByKey(key);
    }
    return null;
  }

  /**
   * view2bitmap
   *
   * @param view 需要转成成bitmap的目标view
   * @return 返回装换好的bitmap
   */
  @Nullable
  public static Bitmap convertViewToBitmap(@Nullable View view) {
    Bitmap bitmap = null;
    if (view != null) {
      view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
          View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
      view.buildDrawingCache();
      bitmap = view.getDrawingCache();
    }
    return bitmap;
  }

  /**
   * 项目的RTL属性
   *
   * @return 返回当前是不是RTL属性
   */
  public static boolean isRtlByLocale() {
    return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
  }

  /**
   * View的RTL属性
   *
   * @param view 目标View
   * @return 判断当前View是不是RTL属性
   */
  public static boolean isRtl(View view) {
    return View.LAYOUT_DIRECTION_RTL == view.getLayoutDirection();
  }

  /**
   * 比较两个tree是不是一样的，找出相同Key的Node，并让他们做动画实现过渡
   *
   * @param curNodeCacheMap     当前正在展示的node的tree,key和node形成的map
   * @param showingNodeCacheMap 将要展示的node的tree，key和node形成的map
   * @return 返回尺寸发生变化的node节点
   */
  @NonNull
  public static List<IRealViewWrapper> diffRenderTree(
      @Nullable Map<Integer, AbsObjectNode<?>> curNodeCacheMap,
      @Nullable Map<Integer, AbsObjectNode<?>> showingNodeCacheMap) {
    // 返回的结果集合
    List<IRealViewWrapper> resultList = new ArrayList<>();
    if (ToolHelper.isMapValid(curNodeCacheMap) && ToolHelper.isMapValid(showingNodeCacheMap)) {
      AbsObjectNode<?> curNode, showingNode;
      for (Map.Entry<Integer, AbsObjectNode<?>> nodeEntry : curNodeCacheMap.entrySet()) {
        if (showingNodeCacheMap.get(nodeEntry.getKey()) != null) {
          curNode = nodeEntry.getValue();
          showingNode = showingNodeCacheMap.get(nodeEntry.getKey());
          // node不能为空
          if (curNode != null && showingNode != null) {
            // 判断是不是有diff
            boolean changed = diff(curNode, showingNode);
            if (changed) {
              curNode.showingViewInfo = createShowingViewInfo(
                  showingNode.mNodeInfo.attrs.alpha,
                  showingNode.size, showingNode.absolutePosition,
                  showingNode.mNodeInfo.attrs.shadow);
              resultList.add(curNode);
            }
          }
        }
      }
    }
    return resultList;
  }


  /**
   * 判断Node节点的key是不是有效
   *
   * @param key key值
   * @return 如果为0，标识没有给当前Node节点设置key，取了默认值，则为false，反之亦然
   */
  public static boolean isNodeKeyValid(int key) {
    return key != 0;
  }

  /**
   * 把key-node添加到map，这里抽出一个方法收口
   *
   * @param key          node的唯一标识key
   * @param node         node节点
   * @param nodeCacheMap 缓存map，便于快速查找
   */
  public static void addKeyNode(int key, @NonNull AbsObjectNode<?> node,
      @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    if (isNodeKeyValid(key)) {
      nodeCacheMap.put(key, node);
    }
  }

  /**
   * 比较节点有没有发生位置，尺寸，或者透明度的变化
   *
   * @param curNode     当前正在展示的节点
   * @param showingNode 即将要展示的节点
   * @return 返回比较是否发生改变的结构，只要有一个改变，就算改变
   */
  private static boolean diff(@NonNull AbsObjectNode<?> curNode,
      @NonNull AbsObjectNode<?> showingNode) {
    UIModel.Attrs curAttrs = curNode.mNodeInfo.attrs;
    UIModel.Attrs showingAttrs = showingNode.mNodeInfo.attrs;
    boolean alphaChanged = curAttrs.alpha != showingAttrs.alpha;
    boolean positionChanged = curNode.absolutePosition.left != showingNode.absolutePosition.left
        || curNode.absolutePosition.top != showingNode.absolutePosition.top;
    boolean sizeChanged = curNode.size.width != showingNode.size.width ||
        curNode.size.height != showingNode.size.height;
    return alphaChanged || positionChanged || sizeChanged;
  }

  /**
   * 构建新的属性对象
   *
   * @param alpha            目标透明度
   * @param size             目标尺寸
   * @param absolutePosition 目标位置
   * @return 返回要变换的目标属性封装的对象
   */
  private static IRealViewWrapper.IViewInfo createShowingViewInfo(float alpha,
      @NonNull UIModel.Size size, @NonNull Rect absolutePosition, @Nullable UIModel.Shadow shadow) {
    return new IRealViewWrapper.IViewInfo() {
      @NonNull
      @Override
      public UIModel.Size getRealViewSize() {
        return formatSize(size, shadow);
      }

      @Override
      public float getRealViewAlpha() {
        return alpha;
      }

      @NonNull
      @Override
      public Rect getRealViewPosition() {
        return formatAbsolutePosition(absolutePosition, shadow);
      }
    };
  }

  /**
   * 根据shadow，重新计算尺寸，可能会改变View的大小
   *
   * @param size   不加shadow的计算尺寸
   * @param shadow shadow属性，主要需要shadow的尺寸半径
   * @return 返回增加shadow属性之后的尺寸大小
   */
  @NonNull
  public static UIModel.Size formatSize(@NonNull UIModel.Size size,
      @Nullable UIModel.Shadow shadow) {
    UIModel.Size resultSize = new UIModel.Size();
    resultSize.width = size.width;
    resultSize.height = size.height;
    if (shadow != null) {
      resultSize.width += ShadowView.getXPadding(shadow) * 2;
      resultSize.height += ShadowView.getYPadding(shadow) * 2;
    }
    return resultSize;
  }

  /**
   * 根据shadow，重新计算坐标，增加shadow，需要View偏移
   *
   * @param absolutePosition 不加shadow计算出来的绝对坐标
   * @param shadow           shadow属性，主要需要shadow的尺寸半径
   * @return 返回加上shadow之后的绝对坐标
   */
  @NonNull
  public static Rect formatAbsolutePosition(@NonNull Rect absolutePosition,
      @Nullable UIModel.Shadow shadow) {
    Rect resultPosition = new Rect();
    resultPosition.top = absolutePosition.top;
    resultPosition.left = absolutePosition.left;
    resultPosition.right = absolutePosition.right;
    resultPosition.bottom = absolutePosition.bottom;
    if (shadow != null) {
      int xOffset = ShadowView.getXPadding(shadow);
      int yOffset = ShadowView.getYPadding(shadow);
      int width = absolutePosition.right - absolutePosition.left;
      int height = absolutePosition.bottom - absolutePosition.top;
      resultPosition.top -= yOffset;
      resultPosition.left -= xOffset;
      resultPosition.right = resultPosition.left + width + 2 * xOffset;
      resultPosition.bottom = resultPosition.top + height + 2 * yOffset;
    }
    return resultPosition;
  }

  /**
   * 获取自定义的字体
   *
   * @param context  context
   * @param fontName 字体名称
   * @return 返回获取到的自定义内置字体
   */
  @NonNull
  public static Typeface createTypeFace(@NonNull Context context, @Nullable String fontName,
      @NonNull Typeface defaultTypeface) {
    Typeface result = null;
    if (!TextUtils.isEmpty(fontName)) {
      try {
        result = Typeface.createFromAsset(context.getAssets(), fontName);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return result != null ? result : defaultTypeface;
  }

  /**
   * 统计上报指标时长
   *
   * @param eventKey 事件指标的key
   * @param service  上报的service能力对象
   * @param duration 时长
   */
  public static void reportStandardDuration(@NonNull String eventKey,
      @Nullable IRIAIDLogReportService service, long duration) {
    if (service != null) {
      service.riaidLogEvent(eventKey, createDurationParams(duration));
    }
  }

  /**
   * 耗时参数封装，暂时放在这里，Browser和Render都要用，保证逻辑统一
   *
   * @param duration 耗时，但是是ms
   * @return 返回封装的结构体
   */
  public static JsonObject createDurationParams(long duration) {
    JsonObject params = new JsonObject();
    params.addProperty("duration_ms", duration);
    return params;
  }


  /**
   * 把key用MD5加密
   */
  public static String toMd5Key(String key) {
    String cacheKey;
    try {
      final MessageDigest mDigest = MessageDigest.getInstance("MD5");
      mDigest.update(key.getBytes());
      cacheKey = bytesToHexString(mDigest.digest());
    } catch (NoSuchAlgorithmException e) {
      cacheKey = String.valueOf(key.hashCode());
    }
    return cacheKey;
  }

  private static String bytesToHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte aByte : bytes) {
      String hex = Integer.toHexString(0xFF & aByte);
      if (hex.length() == 1) {
        sb.append('0');
      }
      sb.append(hex);
    }
    return sb.toString();
  }

  @Nullable
  public static byte[] base64Decode(String str) {
    return str == null ? null : Base64.decode(str, 0);
  }


  /**
   * 将base64的字符串转为RIAIDModel
   */
  @Nullable
  public static RiaidModel base64TransRiadModel(String base64) {
    if (TextUtils.isEmpty(base64)) {
      return null;
    }
    try {
      byte[] decode = base64Decode(base64);
      if (decode != null) {
        return RiaidModel.parseFrom(decode);
      }
    } catch (Exception e) {
      RiaidLogger.e("base64TransRiadModel", "转换失败", e);
    }
    return null;
  }

  /**
   * 两种判断规则
   */
  public static boolean isDataBindingEqual(CharSequence databinding, CharSequence holder) {
    if (TextUtils.equals(databinding, holder)) {
      return true;
    }
    // 服务的模板引擎，需要加上?json_string
    return TextUtils.equals(databinding + "?json_string", holder);
  }

  /**
   * 用来统计总时长
   */
  public static void renderTotalDuration(@Nullable AbsObjectNode<?> rootNode, long duration) {
    if (rootNode != null) {
      NodeDurationService durationService =
          rootNode.mNodeInfo.serviceContainer.getService(NodeDurationService.class);
      if (durationService != null) {
        durationService.durationAdd(duration);
      }
    }
  }

  /**
   * 获取统计好的总时长
   */
  public static long getRenderTotalDuration(@Nullable AbsObjectNode<?> rootNode) {
    if (rootNode != null) {
      NodeDurationService durationService =
          rootNode.mNodeInfo.serviceContainer.getService(NodeDurationService.class);
      if (durationService != null) {
        return durationService.getTotalDuration();
      }
    }
    return 0;
  }

  /**
   * 传入指定的id和指定的View,给对应的View绑定传入的Id，必须是debug环境并且resourceId的值大于0才能走入相应的逻辑
   */
  public static void bindViewId(@NonNull View view, int resourceId) {
    if (Riaid.getInstance().isDebug() && resourceId > 0 && view != null) {
      RiaidLogger.i(TAG, "开始设置id了");
      view.setContentDescription(String.valueOf(resourceId));
    }
  }

}
