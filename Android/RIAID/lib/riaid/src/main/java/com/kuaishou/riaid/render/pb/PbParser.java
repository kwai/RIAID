package com.kuaishou.riaid.render.pb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.pb.base.AbsObjectPbParser;
import com.kuaishou.riaid.render.pb.item.ImageItemPbParser;
import com.kuaishou.riaid.render.pb.item.LottieItemPbParser;
import com.kuaishou.riaid.render.pb.item.SpaceItemPbParser;
import com.kuaishou.riaid.render.pb.item.TextItemPbParser;
import com.kuaishou.riaid.render.pb.item.VideoItemPbParser;
import com.kuaishou.riaid.render.pb.layout.AbsoluteLayoutPbParser;
import com.kuaishou.riaid.render.pb.layout.ButtonLayoutPbParser;
import com.kuaishou.riaid.render.pb.layout.HScrollLayoutPbParser;
import com.kuaishou.riaid.render.pb.layout.HorizontalLayoutPbParser;
import com.kuaishou.riaid.render.pb.layout.SquareLayoutPbParser;
import com.kuaishou.riaid.render.pb.layout.VScrollLayoutPbParser;
import com.kuaishou.riaid.render.pb.layout.VerticalLayoutPbParser;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 这个是PB解析的容器，把所有解析的Parser存放在这里，选取匹配的Parse去解析
 */
public class PbParser {

  /**
   * 私有化构造函数，只保留一个入口
   */
  private PbParser() {}

  /**
   * 外界获取实例对象，统一收口
   */
  @NonNull
  public static PbParser getInstance() {return new PbParser();}

  /**
   * 这个是解析的容器
   */
  private final List<AbsObjectPbParser<?, ?>> mPbParserList = new ArrayList<>();

  {
    // 子元素注册
    ADRenderLogger.i("关闭使用Fresco，使用默认imageView");
    mPbParserList.add(new ImageItemPbParser());
    mPbParserList.add(new LottieItemPbParser());
    mPbParserList.add(new SpaceItemPbParser());
    mPbParserList.add(new TextItemPbParser());
    mPbParserList.add(new VideoItemPbParser());
    // 盒子元素注册
    mPbParserList.add(new AbsoluteLayoutPbParser());
    mPbParserList.add(new ButtonLayoutPbParser());
    mPbParserList.add(new HorizontalLayoutPbParser());
    mPbParserList.add(new HScrollLayoutPbParser());
    mPbParserList.add(new SquareLayoutPbParser());
    mPbParserList.add(new VerticalLayoutPbParser());
    mPbParserList.add(new VScrollLayoutPbParser());
  }

  /**
   * 解析单一节点
   *
   * @param context          context
   * @param serviceContainer 外界提供的service能力容器
   * @param nodePb           这个PB解析器生成的model树的节点
   * @param nodeCacheMap     这个map是用来映射key和node的
   * @return 返回解析装换好的ui-render节点
   */
  @Nullable
  public AbsObjectNode<?> parsePbModel(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @Nullable Node nodePb, @NonNull Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    AbsObjectNode<?> result = null;
    if (nodePb != null) {
      // 如果class都匹配不上，感觉也没有必要解析下去了
      if (nodePb.classType != Node.CLASS_TYPE_UNKNOWN) {
        for (AbsObjectPbParser<?, ?> parser : mPbParserList) {
          if (parser.canParse(nodePb.classType)) {
            result = parser.transformUIModelNode(context,
                serviceContainer, decorSize, nodePb, nodeCacheMap);
            break;
          }
        }
      }
    } else {
      ADRenderLogger.w("解析的PB数据源异常，为空 null");
    }
    return result;
  }

  /**
   * 解析子组件，并返回一个list
   *
   * @param context          context
   * @param serviceContainer 外界提供的service能力容器
   * @param decorSize        外界对于render渲染的View的尺寸的约束
   * @param childrenListPb   这个是pb的model树的子节点，针对盒子容器
   * @param nodeCacheMap     这个map是用来映射key和node的
   * @return 返回解析装换好的ui-render集合
   */
  @NonNull
  public List<AbsObjectNode<?>> parseChildrenPbModel(@NonNull Context context,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize,
      @Nullable List<Node> childrenListPb,
      Map<Integer, AbsObjectNode<?>> nodeCacheMap) {
    List<AbsObjectNode<?>> resultList = new ArrayList<>();
    // 解析有意义，才会去解析
    if (ToolHelper.isListValid(childrenListPb)) {
      AbsObjectNode<?> childRender;
      for (Node childPb : childrenListPb) {
        childRender = parsePbModel(context, serviceContainer, decorSize, childPb, nodeCacheMap);
        if (childRender != null) {
          resultList.add(childRender);
        } else {
          ADRenderLogger.w("当前节点解析无效，为空 null");
        }
      }
    }
    return resultList;
  }
}
