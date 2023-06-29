package com.kuaishou.riaid.render.node.item;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.ButtonAttributes;
import com.kuaishou.riaid.render.impl.touch.CommonHandlerImpl;
import com.kuaishou.riaid.render.impl.touch.CommonPressImpl;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureDetector;
import com.kuaishou.riaid.render.impl.touch.gesture.GestureInterceptorImpl;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.item.base.AbsItemNode;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.util.LayoutPerformer;
import com.kuaishou.riaid.render.util.Pb2Model;
import com.kuaishou.riaid.render.util.ToolHelper;
import com.kuaishou.riaid.render.widget.label.RichTextView;

/**
 * 这个是用来渲染文本的
 */
public class TextItemNode extends AbsItemNode<TextItemNode.TextAttrs> implements
    GestureDetector.IPressListener {

  @NonNull
  private final RichTextView mTextView = new RichTextView(mNodeInfo.context.realContext);

  public TextItemNode(@NonNull NodeInfo<TextAttrs> nodeInfo) {
    super(nodeInfo);
  }

  @NonNull
  @Override
  protected View getItemRealView() {
    return mTextView;
  }

  @Nullable
  @Override
  public View getGestureView() {
    return mTextView;
  }

  @Override
  public void loadAttributes() {
    ToolHelper.bindViewId(mTextView, mNodeInfo.context.key);
    refreshUI(mNodeInfo.attrs);
    LayoutPerformer.setWrapLayoutParams(mTextView);
    boolean handlerValid = mNodeInfo.handler != null;
    boolean highlightColorValid = mNodeInfo.attrs.highlightColor != null;
    if (handlerValid || highlightColorValid) {
      GestureDetector wrapper = new GestureDetector(mNodeInfo.context);
      // 绑定按压态
      if (highlightColorValid) {
        wrapper.setPressListener(new CommonPressImpl(mNodeInfo.context, this));
      }
      // 绑定单击，双击，长按
      if (handlerValid) {
        IResumeActionService service = getService(IResumeActionService.class);
        wrapper.setHandlerListener(
            new CommonHandlerImpl(mNodeInfo.handler, mNodeInfo.context, service));
      }
      wrapper.initGestureDetector(mTextView, new GestureInterceptorImpl(mTextView));
    }
  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {
    // 测量一下下啦啦，这里就开始使用了约束
    mTextView.setMaxWidth(LayoutPerformer.getSizeByMax(widthSpec, mNodeInfo.layout.maxWidth));
    mTextView.setMaxHeight(LayoutPerformer.getSizeByMax(heightSpec, mNodeInfo.layout.maxHeight));
    // 需要注意这个地方，应为setMaxHeight的问题，导致省略号不显示
    setMaxLines(mNodeInfo.attrs.maxLines);
    mTextView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    size.width = LayoutPerformer.
        getSideValueByMode(mNodeInfo.layout.width, mTextView.getMeasuredWidth(), widthSpec);
    size.height = LayoutPerformer.
        getSideValueByMode(mNodeInfo.layout.height, mTextView.getMeasuredHeight(), heightSpec);
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
            UIModel.Size decorSize = mNodeInfo.context.decorSize;
            mNodeInfo.pressAttrs = Pb2Model.pressTextAttrs(realContext, mNodeInfo.serviceContainer,
                decorSize, mNodeInfo.attrs, highlightState.attributes);
          }
          break;
        }
      }
    }
  }

  @Override
  public void onPressStart(boolean fromOutside) {
    if (fromOutside) {
      if (mNodeInfo.pressAttrs != null) {
        refreshUI(mNodeInfo.pressAttrs);
      }
    } else {
      if (mNodeInfo.attrs.highlightColor != null) {
        mTextView.setTextColor(mNodeInfo.attrs.highlightColor);
      }
    }
  }

  @Override
  public void onPressEnd(boolean fromOutside) {
    refreshUI(mNodeInfo.attrs);
  }

  /**
   * 刷新UI
   */
  public void refreshUI(@NonNull TextAttrs attrs) {
    mTextView.getPaint().setFlags(attrs.lineMode);
    setMaxLines(attrs.maxLines);
    mTextView.setAlpha(attrs.alpha);
    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, attrs.size);
    mTextView.setTextColor(attrs.color);
    mTextView.setGravity(attrs.alignMode);
    typefaceStyle(attrs.fontName, attrs.isBold, attrs.isTilt);
    mTextView.setText(attrs.text);
    mTextView.setRichText(mNodeInfo.context, attrs.richTextList,
        mNodeInfo.serviceContainer, mNodeInfo.context.decorSize);
    mTextView.setTextDirection(View.TEXT_DIRECTION_LOCALE);
    mTextView.setLineSpacing(attrs.lineSpaceExtra, 1);
    if (attrs.lineHeight > 0) {
      mTextView.setLineHeight(attrs.lineHeight);
    }
    if (attrs.ellipsizeMode != null) {
      mTextView.setEllipsize(attrs.ellipsizeMode);
    }
    if (attrs.backgroundDrawable != null) {
      mTextView.setBackground(attrs.backgroundDrawable);
    }
  }

  @Override
  public boolean dispatchEvent(@NonNull String eventType, @Nullable List<Integer> keyList,
      @Nullable Attributes attributes) {
    if (canMatchRenderKey(keyList)) {
      Context realContext = mNodeInfo.context.realContext;
      UIModel.Size decorSize = mNodeInfo.context.decorSize;
      TextAttrs textAttrs = Pb2Model.pressTextAttrs(realContext,
          mNodeInfo.serviceContainer, decorSize, mNodeInfo.attrs, attributes);
      if (textAttrs != null) {
        refreshUI(textAttrs);
        mNodeInfo.attrs = textAttrs;
        return true;
      }
    }
    return false;
  }

  /**
   * 这是TextView的最大行数
   */
  private void setMaxLines(int maxLines) {
    if (maxLines <= 1) {
      mTextView.setMaxLines(1);
      mTextView.setSingleLine(true);
    } else {
      mTextView.setMaxLines(maxLines);
    }
  }

  /**
   * 文字粗体，斜体
   */
  private void typefaceStyle(@Nullable String fontName, boolean isBold, boolean isTilt) {
    Context realContext = mNodeInfo.context.realContext;
    Typeface typeFace = ToolHelper.createTypeFace(realContext, fontName, Typeface.DEFAULT);
    if (isBold && isTilt) {
      mTextView.setTypeface(typeFace, Typeface.BOLD_ITALIC);
    } else if (isBold) {
      mTextView.setTypeface(typeFace, Typeface.BOLD);
    } else if (isTilt) {
      mTextView.setTypeface(typeFace, Typeface.ITALIC);
    } else {
      mTextView.setTypeface(typeFace, Typeface.NORMAL);
    }
  }

  public static class TextAttrs extends UIModel.Attrs {
    public int color;
    public float size;
    public String text;
    public int maxLines;
    public int lineMode;
    public int alignMode;
    public boolean isBold;
    public boolean isTilt;
    public String fontName;
    public int lineSpaceExtra;
    // 行高
    public int lineHeight;
    public Integer highlightColor;
    public TextUtils.TruncateAt ellipsizeMode;
    public List<UIModel.RichText> richTextList;
  }
}
