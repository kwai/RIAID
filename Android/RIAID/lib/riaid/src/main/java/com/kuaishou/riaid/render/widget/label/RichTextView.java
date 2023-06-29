package com.kuaishou.riaid.render.widget.label;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.kuaishou.riaid.render.config.DSLRenderCore;
import com.kuaishou.riaid.render.impl.touch.CommonHandlerImpl;
import com.kuaishou.riaid.render.interf.IServiceContainer;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.base.AbsObjectNode;
import com.kuaishou.riaid.render.service.base.IResumeActionService;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 富文本的{@link ClickableSpan}的点击事件最终
 * 由{@link com.kuaishou.riaid.render.impl.touch.CustomGestureImpl}来处理
 */
public class RichTextView extends AppCompatTextView {
  private static final String TAG = "RichTextView";
  public RichTextView(@NonNull Context context) {
    super(context);
  }

  public RichTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public RichTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  {
    getPaint().setAntiAlias(true);
  }

  private static final String ELLIPSIS_TEXT = "\u2026"; // HORIZONTAL ELLIPSIS (…)

  /**
   * 这是最后一个要被替换成富文本的特殊标记位
   */
  private static final String SPECIAL_FLAG = "$";

  /**
   * 富文本的拼接Spannable
   */
  @Nullable
  private Spannable mSpannable;

  /**
   * @return 如果是富文本，获取其拼接好的Spannable
   */
  @Nullable
  public Spannable getSpannable() {
    return mSpannable;
  }

  public void setRichText(@NonNull UIModel.NodeContext context,
      @Nullable List<UIModel.RichText> richTextList,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize) {
    final String sourceText = getText().toString();
    // 数据有效
    if (!TextUtils.isEmpty(sourceText) && ToolHelper.isListValid(richTextList)) {
      getViewTreeObserver().addOnGlobalLayoutListener(
          new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              // 有一个日志，看看这个地方是不是多次走进去
              ADRenderLogger.i("onGlobalLayout method is invoked, ready to set rich text");
              // 为了在展示之后，保证能够获取到省略号
              getViewTreeObserver().removeOnGlobalLayoutListener(this);

              ADRenderLogger.i("removeOnGlobalLayoutListener method is invoked");
              IResumeActionService service =
                  serviceContainer.getService(IResumeActionService.class);
              Map<UIModel.RichText, Bitmap> bitmapMap = new HashMap<>();
              UIModel.RichText lastRichText = hasLastOne(richTextList, context.realContext,
                  sourceText, bitmapMap, serviceContainer, decorSize);
              String newSourceText = lastRichText != null ?
                  format(sourceText, lastRichText, bitmapMap.get(lastRichText)) : sourceText;
              // 真正要展示的Span
              SpannableStringBuilder builder = new SpannableStringBuilder(newSourceText);

              if (ToolHelper.isMapValid(bitmapMap)) {
                Bitmap bitmap;
                UIModel.RichText richText;
                // 这个是时候的map都是有意义的，不用判空，直接用即可
                for (Map.Entry<UIModel.RichText, Bitmap> bitmapEntry : bitmapMap.entrySet()) {
                  bitmap = bitmapEntry.getValue();
                  richText = bitmapEntry.getKey();
                  // 安全校验
                  boolean isMatched = newSourceText.contains(richText.placeHolder);
                  if (isMatched && bitmap != null) {
                    if (TextUtils.equals(SPECIAL_FLAG, richText.placeHolder)) {
                      // 这个比较特殊，标记一下，稍后处理
                      setLastRichText(richText, bitmap, builder, context, newSourceText, service);
                    } else {
                      // 替换普通的文本
                      setCommonRichText(richText, bitmap, builder, context, newSourceText,
                          service);
                    }
                  }
                }
              }
              mSpannable = builder;
              setText(mSpannable);
            }
          });
    }
  }

  /**
   * 有没有特殊标记,并提前创建好bitmap
   */
  private UIModel.RichText hasLastOne(List<UIModel.RichText> richTextList,
      @NonNull Context context, @NonNull String sourceText,
      @NonNull Map<UIModel.RichText, Bitmap> bitmapMap,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize) {
    UIModel.RichText resultRichText = null;
    if (ToolHelper.isListValid(richTextList)) {
      for (UIModel.RichText richText : richTextList) {
        // 安全校验，富文本各方面都是有效的
        if (richText != null && !TextUtils.isEmpty(richText.placeHolder)) {
          boolean isMatched = sourceText.contains(richText.placeHolder);
          if (isMatched && richText.richContent != null) {
            // 这个时候才是有意义的，可能会有真正有意义的bitmap
            bitmapMap.put(richText, createBitmap(context, richText, serviceContainer, decorSize));
            int pos = sourceText.indexOf(richText.placeHolder);
            boolean isLast = pos + richText.placeHolder.length() >= sourceText.length();
            if (isLast) {
              // 证明有特殊标记
              resultRichText = richText;
            }
          }
        }
      }
    }
    return resultRichText;
  }


  /**
   * 获取string
   */
  private String format(@NonNull String sourceText,
      @NonNull UIModel.RichText lastRichText, @Nullable Bitmap bitmap) {
    StringBuilder contentBuffer = new StringBuilder();
    contentBuffer.append(sourceText);
    // 拿到Layout
    Layout layout = getLayout();
    // 获取文字行数
    int line = getLineCount();
    // 增加一个安全校验
    boolean isEllipsisValid = false;
    if (line > 0) {
      int lineEnd = 0;
      if (line >= 2) {
        lineEnd = layout.getLineEnd(line - 2);
      }
      int ellipsis = layout.getEllipsisStart(line - 1);
      Log.i(TAG, "format: lineEnd:" + lineEnd + " ellipsis:" + ellipsis);
      // ellipsis > 0时或者最后一行最后字符的偏移量小于本身字符串的长度，说明省略生效
      if (ellipsis > 0 || layout.getLineEnd(line - 1) < contentBuffer.length()) {
        isEllipsisValid = true;
        // 获取bitmap的宽度
        int bitmapWidth = bitmap == null ? 0 : bitmap.getWidth();
        // 获取宽度相对字符的长度
        int bitmapCharWidth =
            getCharCountByBitmap(lineEnd + ellipsis, bitmapWidth, contentBuffer);
        // 重新计算开始截断的起始坐标
        int start = lineEnd + ellipsis - bitmapCharWidth;
        // 安全校验
        start = Math.max(start, 0);
        contentBuffer.replace(start, contentBuffer.length(), ELLIPSIS_TEXT);
      }
    }
    // 没有省略号，但是同样需要把文本替换掉
    if (!isEllipsisValid && sourceText.contains(lastRichText.placeHolder)) {
      int start = sourceText.indexOf(lastRichText.placeHolder);
      // 同样需要替换掉
      contentBuffer.replace(start, start + contentBuffer.length(), "");
    }
    // holder也需要改变
    lastRichText.placeHolder = SPECIAL_FLAG;
    return contentBuffer.append(SPECIAL_FLAG).toString();
  }

  /**
   * 把render渲染的View，转成图片
   *
   * @param context          context
   * @param richText         富文本的数据源
   * @param serviceContainer 外界注入的service能力的容器
   * @param decorSize        外界约束的边界尺寸
   */
  private Bitmap createBitmap(@NonNull Context context, @Nullable UIModel.RichText richText,
      @NonNull IServiceContainer serviceContainer, @NonNull UIModel.Size decorSize) {
    if (richText != null && richText.richContent != null) {
      // 每次重新创建Render的Tree，保证View是全新的
      AbsObjectNode<?> richRender = DSLRenderCore.createInstance()
          .parsePbSourceData(context, serviceContainer, decorSize, richText.richContent,
              new HashMap<>());
      View richView = DSLRenderCore.createInstance()
          .renderRootView(context, richRender);
      return ToolHelper.convertViewToBitmap(richView);
    }
    return null;
  }

  /**
   * 通过循环算出tag的宽度对应的字节长度。需要将这些字节replace掉，用tag生成的drawable来替代。
   *
   * @param end           开始省略的位置
   * @param bitmapWidth   目标tag的宽度
   * @param contentBuffer 原始的字符串
   * @return targetWidth的宽度对应的字节长度
   */
  private int getCharCountByBitmap(int end, float bitmapWidth, StringBuilder contentBuffer) {
    int start = end;
    while (--start >= 0) {
      String substring = contentBuffer.substring(start, end);
      float subWidth = getPaint().measureText(substring);
      if (subWidth > bitmapWidth) {
        break;
      }
    }
    // 这里要把那些影响省略换行的字段给移除掉。
    while (start >= 0) {
      if (contentBuffer.charAt(start) == '\n' ||
          contentBuffer.charAt(start) == ' ' ||
          contentBuffer.charAt(start) == '\t') {
        Log.i(TAG, "getCharCountByBitmap: 末尾有换行符等");
        start--;
      } else {
        break;
      }
    }
    return Math.max(end - start, 0);
  }


  /**
   * 这个是普通处理
   */
  public void setCommonRichText(@NonNull UIModel.RichText richText, @NonNull Bitmap bitmap,
      @NonNull SpannableStringBuilder builder, @NonNull UIModel.NodeContext context,
      @NonNull String sourceText, @Nullable IResumeActionService service) {
    int start = sourceText.indexOf(richText.placeHolder),
        end = start + richText.placeHolder.length();
    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
    drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
    builder.setSpan(new RichImageSpan(drawable, richText.richAlignMode), start, end,
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    if (richText.handler != null) {
      builder.setSpan(new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
          new CommonHandlerImpl(richText.handler, context, service).onClick();
        }
      }, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }
  }

  /**
   * 这个是特殊处理
   */
  public void setLastRichText(@NonNull UIModel.RichText richText, @NonNull Bitmap bitmap,
      @NonNull SpannableStringBuilder builder, @NonNull UIModel.NodeContext context,
      @NonNull String sourceText, @Nullable IResumeActionService service) {
    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
    drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
    ImageSpan span = new RichImageSpan(drawable, richText.richAlignMode);
    int start = sourceText.length() - 1, end = sourceText.length();
    builder.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    if (richText.handler != null && richText.handler.click != null) {
      builder.setSpan(new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
          new CommonHandlerImpl(richText.handler, context, service).onClick();
        }
      }, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }
  }


}
