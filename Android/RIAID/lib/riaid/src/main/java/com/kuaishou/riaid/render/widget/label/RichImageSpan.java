package com.kuaishou.riaid.render.widget.label;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import androidx.annotation.NonNull;

import com.kuaishou.riaid.proto.nano.TextAttributes;


/**
 * 上下居中显示的ImageSpan
 */
public class RichImageSpan extends ImageSpan {

  private final int richAlignMode;

  public RichImageSpan(final Drawable drawable, int richAlignMode) {
    super(drawable);
    this.richAlignMode = richAlignMode;
  }

  @Override
  public int getSize(Paint paint, CharSequence text,
      int start, int end,
      Paint.FontMetricsInt fm) {
    Drawable d = getDrawable();
    if (d == null) {
      return 0;
    }
    Rect rect = d.getBounds();

    if (fm != null) {
      Paint.FontMetricsInt pfm = paint.getFontMetricsInt();
      // keep it the same as paint's fm
      fm.ascent = pfm.ascent;
      fm.descent = pfm.descent;
      fm.top = pfm.top;
      fm.bottom = pfm.bottom;
    }

    return rect.right;
  }

  @Override
  public void draw(@NonNull Canvas canvas, CharSequence text,
      int start, int end, float x,
      int top, int y, int bottom, @NonNull Paint paint) {
    Drawable b = getDrawable();
    if (b == null) {
      return;
    }
    canvas.save();
    int transY = centerAlignMode(b, paint, bottom);
    if (richAlignMode == TextAttributes.RichText.RICH_ALIGN_BOTTOM) {
      transY = bottomAlignMode(b, bottom);
    }
    canvas.translate(x, transY);
    b.draw(canvas);
    canvas.restore();
  }

  private int centerAlignMode(@NonNull Drawable b, @NonNull Paint paint, int bottom) {
    int drawableHeight = b.getIntrinsicHeight();
    int fontAscent = paint.getFontMetricsInt().ascent;
    int fontDescent = paint.getFontMetricsInt().descent;
    return bottom - b.getBounds().bottom +  // align bottom to bottom
        (drawableHeight - fontDescent + fontAscent) / 2;  // align center to center
  }

  private int bottomAlignMode(@NonNull Drawable b, int bottom) {
    return bottom - b.getBounds().bottom;
  }
}