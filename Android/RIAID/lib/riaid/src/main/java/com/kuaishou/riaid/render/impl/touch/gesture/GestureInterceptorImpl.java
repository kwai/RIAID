package com.kuaishou.riaid.render.impl.touch.gesture;

import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.widget.label.RichTextView;


/**
 * 用来解决TextView和富文本点击事件冲突的问题，如果单独设没有问题，如果富文本和TextView同时设置点击事件，
 * 富文本点击事件会被TextView的点击事件消费掉
 */
public class GestureInterceptorImpl implements GestureInterceptor {

  @NonNull
  private final RichTextView mTextView;

  public GestureInterceptorImpl(@NonNull RichTextView textView) {
    this.mTextView = textView;
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
    return tryClickSpan(e, mTextView);
  }

  /**
   * 先尝试去触发富文本的点击事件
   *
   * @return 如果消费成功，返回true，否则返回false
   */
  private boolean tryClickSpan(MotionEvent event, @Nullable RichTextView widget) {
    if (widget == null) {
      return false;
    }
    Spannable buffer = widget.getSpannable();
    if (buffer == null || event == null) {
      return false;
    }
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= widget.getTotalPaddingLeft();
    y -= widget.getTotalPaddingTop();

    x += widget.getScrollX();
    y += widget.getScrollY();

    Layout layout = widget.getLayout();
    int line = layout.getLineForVertical(y);
    int off = layout.getOffsetForHorizontal(line, x);

    // 从点击的坐标，找到匹配的ClickableSpan
    ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

    // 如果匹配到了，则执行点击事件
    if (links.length != 0) {
      ClickableSpan link = links[0];
      link.onClick(widget);
      return true;
    }
    return false;
  }

}
