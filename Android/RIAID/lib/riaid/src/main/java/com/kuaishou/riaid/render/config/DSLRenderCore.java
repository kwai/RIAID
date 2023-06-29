package com.kuaishou.riaid.render.config;

import static com.kuaishou.riaid.render.util.ToolHelper.renderTotalDuration;

import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.pb.PbParser;
import com.kuaishou.riaid.render.service.base.IRIAIDLogReportService;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 这里是真正的渲染流程
 * 用一个单例吧，没有必要每次渲染重新创建，不会持有任何的context
 */
public class DSLRenderCore {

  private DSLRenderCore() {}

  @NonNull
  public static DSLRenderCore createInstance() {
    return new DSLRenderCore();
  }

  /**
   * 这里负责把服务端下发的二进制流转换成具体的独享
   *
   * @param context          context
   * @param serviceContainer 这个是外界提供的service能力，这里是一个包装类容器
   * @param decorSize        这个是外界传递的约束，也就是当前Render的画布的大小
   * @param data             这个还是PB解析好的model书的根节点
   * @param nodeCacheMap     这个map是用来映射key和node的
   * @return 返回值是一个虚拟的ui-render-tree的，根节点
   */
  @Nullable
  public AbsObjectNode<?> parsePbSourceData(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @Nullable Node data, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    // 开始真正的解析
    return PbParser.getInstance()
        .parsePbModel(context, serviceContainer, decorSize, data, nodeCacheMap);
  }

  /**
   * 开始展示
   * 正常的流程  bindData -- onMeasure -- onLayout -- onLayoutParams -- onDraw
   * bindData -- 很好理解，需要把组件和数据源绑定，有了数据填充才可以获取尺寸和位置
   * loadLayout -- 这个是给组件打上padding等通用布局属性
   * onMeasure -- 计算出每一个组件的具体宽高
   * onLayout -- 这个时候子View开始自己的排放，也就是计算出绝对坐标
   * onLayoutParams -- 这个时候是绑定布局参数，确保添加到画布的时候，不用重新绑定布局参数
   * onDraw -- 绘制，这里的绘制是把自己放到容器之中，展示出来
   *
   * @param context  这个context用来创建View的
   * @param rootNode 这个是虚拟树的根节点
   * @return 返回一个View，供宿主使用展示
   */
  @Nullable
  public View renderRootView(@NonNull Context context, @Nullable AbsObjectNode<?> rootNode) {
    ADRenderLogger.i("解析数据，创建Render的View");
    if (rootNode != null) {
      // 创建顶层容器
      FrameLayout decorView = new FrameLayout(context);
      int matchParent = ViewGroup.LayoutParams.WRAP_CONTENT;
      decorView.setLayoutParams(new ViewGroup.LayoutParams(matchParent, matchParent));
      // 绑定属性
      bindAttributes(rootNode);
      // 测量
      onMeasure(rootNode);
      // 布局
      onLayout(rootNode);
      // 参数绑定
      onLayoutParams(rootNode);
      // 绘制
      onDraw(rootNode, decorView);
      // 渲染总时长
      ToolHelper.reportStandardDuration(
          RIAIDConstants.Standard.RENDER_TOTAL_DURATION,
          rootNode.mNodeInfo.serviceContainer.getService(IRIAIDLogReportService.class),
          ToolHelper.getRenderTotalDuration(rootNode));
      return decorView;
    } else {
      ADRenderLogger.e("PB的数据转换Render的tree，解析失败");
    }
    return null;
  }

  /**
   * 修改布局参数，重新布局
   *
   * @param rootNode 这个是根节点
   */
  public void requestLayout(@NonNull AbsObjectNode<?> rootNode) {
    // 重新测量
    onMeasure(rootNode);
    // 重新布局
    onLayout(rootNode);
    // 修改布局参数
    onLayoutParams(rootNode);
  }

  private void bindAttributes(@NonNull AbsObjectNode<?> rootNode) {
    // 上报的service
    IRIAIDLogReportService service =
        rootNode.mNodeInfo.serviceContainer.getService(IRIAIDLogReportService.class);
    long currentTimeMillis = System.currentTimeMillis();
    rootNode.loadAttributes();
    rootNode.loadLayout();
    long duration = System.currentTimeMillis() - currentTimeMillis;
    // 属性绑定时长
    ToolHelper.reportStandardDuration(
        RIAIDConstants.Standard.RENDER_LOAD_ATTRIBUTES_LAYOUT_DURATION,
        service, duration);
    renderTotalDuration(rootNode, duration);
  }

  /**
   * 测量，计算控件尺寸
   *
   * @param rootNode 根Node节点
   */
  private void onMeasure(@NonNull AbsObjectNode<?> rootNode) {
    long currentTimeMillis = System.currentTimeMillis();
    // 外界约束的最大尺寸
    UIModel.Size decorSize = rootNode.mNodeInfo.context.decorSize;
    // 上报的service
    IRIAIDLogReportService service =
        rootNode.mNodeInfo.serviceContainer.getService(IRIAIDLogReportService.class);
    rootNode.onMeasure(
        LayoutPerformer.edgeSize(rootNode.mNodeInfo.layout.width, decorSize.width),
        LayoutPerformer.edgeSize(rootNode.mNodeInfo.layout.height, decorSize.height));
    long duration = System.currentTimeMillis() - currentTimeMillis;
    renderTotalDuration(rootNode, duration);

    // 通知测量时长
    ToolHelper.reportStandardDuration(
        RIAIDConstants.Standard.RENDER_MEASURE_DURATION,
        service, duration);
  }

  /**
   * 布局，计算位置
   *
   * @param rootNode 根Node节点
   */
  private void onLayout(@NonNull AbsObjectNode<?> rootNode) {
    long currentTimeMillis = System.currentTimeMillis();
    // 上报的service
    IRIAIDLogReportService service =
        rootNode.mNodeInfo.serviceContainer.getService(IRIAIDLogReportService.class);
    rootNode.onLayout();
    long duration = System.currentTimeMillis() - currentTimeMillis;
    renderTotalDuration(rootNode, duration);
    ToolHelper.reportStandardDuration(
        RIAIDConstants.Standard.RENDER_LAYOUT_DURATION,
        service, duration);
  }

  /**
   * 绑定布局参数
   *
   * @param rootNode 根Node节点
   */
  private void onLayoutParams(@NonNull AbsObjectNode<?> rootNode) {
    long currentTimeMillis = System.currentTimeMillis();
    // 上报的service
    IRIAIDLogReportService service =
        rootNode.mNodeInfo.serviceContainer.getService(IRIAIDLogReportService.class);
    rootNode.onLayoutParams();
    long duration = System.currentTimeMillis() - currentTimeMillis;
    renderTotalDuration(rootNode, duration);
    ToolHelper.reportStandardDuration(
        RIAIDConstants.Standard.RENDER_LAYOUT_PARAMS_DURATION,
        service, duration);
  }

  /**
   * 绘制，把Node的真实View放置在画布上
   *
   * @param rootNode  根Node节点
   * @param decorView 返回给上层的容器
   */
  private void onDraw(@NonNull AbsObjectNode<?> rootNode, @NonNull FrameLayout decorView) {
    long currentTimeMillis = System.currentTimeMillis();
    // 上报的service
    IRIAIDLogReportService service =
        rootNode.mNodeInfo.serviceContainer.getService(IRIAIDLogReportService.class);
    rootNode.onDraw(decorView);
    long duration = System.currentTimeMillis() - currentTimeMillis;
    renderTotalDuration(rootNode, duration);
    ToolHelper.reportStandardDuration(
        RIAIDConstants.Standard.RENDER_DRAW_DURATION,
        service, duration);
  }

}
