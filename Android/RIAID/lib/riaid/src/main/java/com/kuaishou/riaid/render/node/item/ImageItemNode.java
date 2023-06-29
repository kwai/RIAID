package com.kuaishou.riaid.render.node.item;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.ButtonAttributes;
import com.kuaishou.riaid.render.impl.touch.CommonHandlerImpl;
import com.kuaishou.riaid.render.impl.touch.CommonPressImpl;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.item.base.AbsItemNode;
import com.kuaishou.riaid.render.preload.RIAIDPreloadResourceOperator;
import com.kuaishou.riaid.render.service.base.ILoadImageService;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.util.Pb2Model;
import com.kuaishou.riaid.render.util.ToolHelper;
import com.kuaishou.riaid.render.widget.CornerImageView;

/**
 * 这个是用来渲染图片的
 */
public class ImageItemNode extends AbsItemNode<ImageItemNode.ImageAttrs> {
  @NonNull
  private final CornerImageView mImageView = new CornerImageView(mNodeInfo.context.realContext);

  public ImageItemNode(@NonNull NodeInfo<ImageAttrs> nodeInfo) {
    super(nodeInfo);
  }

  @NonNull
  @Override
  protected View getItemRealView() {
    return mImageView;
  }

  @Nullable
  @Override
  public View getGestureView() {
    return mImageView;
  }

  @Override
  public void loadAttributes() {
    ToolHelper.bindViewId(mImageView, mNodeInfo.context.key);
    refreshUI(mNodeInfo.attrs);
    // 设置按压态监听
    String highlightUrl = mNodeInfo.attrs.highlightUrl;
    boolean handlerValid = mNodeInfo.handler != null;
    boolean highlightUrlValid = !TextUtils.isEmpty(highlightUrl);
    if (handlerValid || highlightUrlValid) {
      GestureDetector wrapper = new GestureDetector(mNodeInfo.context);
      // 绑定按压态
      if (highlightUrlValid) {
        wrapper.setPressListener(new CommonPressImpl(mNodeInfo.context, this));
      }
      // 绑定单击，双击，长按
      if (handlerValid) {
        IResumeActionService service = getService(IResumeActionService.class);
        wrapper.setHandlerListener(
            new CommonHandlerImpl(mNodeInfo.handler, mNodeInfo.context, service));
      }
      wrapper.initGestureDetector(mImageView);
    }
  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {
    // 通过测量模式获取到的尺寸
    size.width = LayoutPerformer
        .getSideValueByMode(mNodeInfo.layout.width, mNodeInfo.layout.width, widthSpec);
    size.height = LayoutPerformer
        .getSideValueByMode(mNodeInfo.layout.height, mNodeInfo.layout.height, heightSpec);
    // 再通过最大尺寸来约束一下
    size.width = LayoutPerformer.getSizeByMax(size.width, mNodeInfo.layout.maxWidth);
    size.height = LayoutPerformer.getSizeByMax(size.height, mNodeInfo.layout.maxHeight);
  }

  @Override
  public void inflatePressAttrs(@NonNull List<ButtonAttributes.HighlightState> pressStateList) {
    int key = mNodeInfo.context.key;
    if (ToolHelper.isListValid(pressStateList)) {
      for (ButtonAttributes.HighlightState highlightState : pressStateList) {
        if (highlightState != null && highlightState.key == key) {
          if (highlightState.attributes != null) {
            Context realContext = mNodeInfo.context.realContext;
            mNodeInfo.pressAttrs = Pb2Model
                .pressImageAttrs(realContext, mNodeInfo.serviceContainer, mNodeInfo.attrs,
                    highlightState.attributes);
          }
          break;
        }
      }
    }
  }

  /**
   * 刷新UI
   */
  private void refreshUI(@Nullable ImageAttrs attrs) {
    if (attrs != null) {
      loadImage(attrs.imageUrl);
      mImageView.setScaleType(attrs.scaleType);
      mImageView.setAlpha(attrs.alpha);
      if (attrs.cornerRadius != null) {
        mImageView.setRoundRadius(attrs.cornerRadius);
      }
      if (attrs.backgroundDrawable != null) {
        mImageView.setBackground(attrs.backgroundDrawable);
      }
      if (attrs.colorFilter != 0) {
        mImageView.setColorFilter(attrs.colorFilter);
      }
    }
  }

  private void loadImage(@Nullable String url) {
    String realUrl = RIAIDPreloadResourceOperator.getRealUrl(url);
    // 绑定图片
    if (!TextUtils.isEmpty(realUrl)) {
      // 绑定网络数据url
      ILoadImageService loadImageService = getService(ILoadImageService.class);
      if (loadImageService != null) {
        loadImageService.loadImage(realUrl, mImageView,mNodeInfo.layout.width,mNodeInfo.layout.height);
      }
    }
  }

  @Override
  public void onPressStart(boolean fromOutside) {
    if (fromOutside) {
      // 不是主动触发，外界触发的
      refreshUI(mNodeInfo.pressAttrs);
    } else {
      // 主动触发的
      loadImage(mNodeInfo.attrs.highlightUrl);
    }
  }

  @Override
  public boolean dispatchEvent(@NonNull String eventType, @Nullable List<Integer> keyList,
      @Nullable Attributes attributes) {
    if (canMatchRenderKey(keyList)) {
      Context realContext = mNodeInfo.context.realContext;
      ImageAttrs imageAttrs = Pb2Model.pressImageAttrs(realContext,
          mNodeInfo.serviceContainer, mNodeInfo.attrs, attributes);
      if (imageAttrs != null) {
        refreshUI(imageAttrs);
        mNodeInfo.attrs = imageAttrs;
        return true;
      }
    }
    return false;
  }

  @Override
  public void onPressEnd(boolean fromOutside) {
    refreshUI(mNodeInfo.attrs);
  }

  public static final class ImageAttrs extends UIModel.Attrs {
    public String imageUrl;
    public String highlightUrl;
    public ImageView.ScaleType scaleType;
    public int colorFilter;
  }
}
