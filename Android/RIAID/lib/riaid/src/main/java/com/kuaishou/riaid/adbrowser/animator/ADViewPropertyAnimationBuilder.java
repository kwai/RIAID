package com.kuaishou.riaid.adbrowser.animator;

import java.util.Collections;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserConstants;
import com.kuaishou.riaid.adbrowser.logger.ADBrowserLogger;
import com.kuaishou.riaid.proto.nano.ADAnimationModel;
import com.kuaishou.riaid.proto.nano.Attributes;
import com.kuaishou.riaid.proto.nano.CommonAttributes;
import com.kuaishou.riaid.proto.nano.FloatValue;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;
import com.kuaishou.riaid.render.config.DSLRenderCreator;
import com.kuaishou.riaid.render.logger.ADRenderLogger;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * 视图的属性动画的构建器
 * 仅用于场景内动画
 */
public class ADViewPropertyAnimationBuilder {
  private ADViewPropertyAnimationBuilder() {}

  /**
   * @param view             要执行动画的view
   * @param renderCreator
   * @param adAnimationModel 执行动画的配置
   * @return {@link AnimatorSet}
   */
  @Nullable
  public static AnimatorSet build(int viewKey, @NonNull View view,
      @Nullable DSLRenderCreator renderCreator,
      @NonNull ADAnimationModel adAnimationModel) {
    ADRenderLogger
        .i("ADViewPropertyAnimationBuilder adAnimationModel:" + adAnimationModel.toString());
    AnimatorSet animatorSet = new AnimatorSet();
    String viewProperty;
    // 真正的values，当属性是dp相关的单位时，需要转换成px
    float[] propertyValues = adAnimationModel.values;
    if (propertyValues == null || propertyValues.length <= 0) {
      ADBrowserLogger.e("ADViewPropertyAnimationBuilder adAnimationModel.value不合法");
      return null;
    }
    switch (adAnimationModel.propertyType) {
      case ADAnimationModel.ALPHA:
        viewProperty = View.ALPHA.getName();
        ADBrowserLogger.i("ADViewPropertyAnimationBuilder 透明属性变化");
        buildNormalAnimator(viewKey, view, renderCreator, adAnimationModel, animatorSet,
            viewProperty,
            propertyValues);
        break;
      case ADAnimationModel.ROTATION:
        viewProperty = View.ROTATION.getName();
        ADBrowserLogger.i("ADViewPropertyAnimationBuilder 旋转属性变化");
        buildNormalAnimator(0, view, null, adAnimationModel, animatorSet, viewProperty,
            propertyValues);
        break;
      case ADAnimationModel.WIDTH:
      case ADAnimationModel.HEIGHT:
        // 宽高的dp单位需要在逻辑处理时转成像素
        ADBrowserLogger.i("ADViewPropertyAnimationBuilder 宽高属性变化");
        if (buildSizeAnimator(view, adAnimationModel, animatorSet, propertyValues)) {
          return null;
        }
        break;
      case ADAnimationModel.SCALE:
        ADBrowserLogger.i("ADViewPropertyAnimationBuilder 缩放属性变化");
        // 如果有设置轴心点，需要给其赋值。
        if (adAnimationModel.pivotX != null) {
          view.setPivotX(adAnimationModel.pivotX.value);
        }
        if (adAnimationModel.pivotY != null) {
          view.setPivotY(adAnimationModel.pivotY.value);
        }
        buildScaleAnimator(view, adAnimationModel, animatorSet, propertyValues);
        break;
      case ADAnimationModel.HIDDEN:
        ADBrowserLogger.i("ADViewPropertyAnimationBuilder 可见属性变化");
        buildVisibilityAnimator(view, animatorSet, propertyValues);
        break;
      default:
        ADBrowserLogger.e("ADViewPropertyAnimationBuilder 不支持该属性的动画 propertyType：" +
            adAnimationModel.propertyType);
        return null;
    }
    return animatorSet;
  }

  private static void buildNormalAnimator(int viewKey, @NonNull View view,
      @Nullable DSLRenderCreator renderCreator,
      @NonNull ADAnimationModel adAnimationModel,
      AnimatorSet animatorSet,
      String viewProperty,
      float[] propertyValues) {
    ADBrowserLogger.i("ADViewPropertyAnimationBuilder 执行的是单属性动画 viewProperty：" + viewProperty);
    ObjectAnimator objectAnimator =
        ObjectAnimator.ofFloat(view, viewProperty, propertyValues);
    objectAnimator.setDuration(adAnimationModel.duration);
    if (adAnimationModel.repeatCount == RIAIDConstants.Animation.REPEAT_INFINITE) {
      // 支持无限循环
      objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
      objectAnimator.setRepeatMode(ValueAnimator.RESTART);
    } else if (adAnimationModel.repeatCount > 1) {
      objectAnimator.setRepeatCount(adAnimationModel.repeatCount);
    }
    objectAnimator.addListener(new ADAnimatorListener() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (renderCreator != null && renderCreator.rootRender != null
            && propertyValues != null && propertyValues.length > 0) {
          // 如果是透明的属性改动，需要将其同步给render
          if (TextUtils.equals(View.ALPHA.getName(), viewProperty)) {
            Attributes attributes = new Attributes();
            attributes.common = new CommonAttributes();
            attributes.common.alpha = new FloatValue();
            attributes.common.alpha.value = propertyValues[propertyValues.length - 1];
            renderCreator.rootRender
                .dispatchEvent(ADBrowserConstants.ATTRIBUTE, Collections.singletonList(viewKey),
                    attributes);
          }
        }
      }
    });
    animatorSet.playTogether(objectAnimator);
  }

  private static boolean buildSizeAnimator(@NonNull View view,
      @NonNull ADAnimationModel adAnimationModel, AnimatorSet animatorSet, float[] propertyValues) {
    ADBrowserLogger.i("ADViewPropertyAnimationBuilder 执行的是宽高改变的动画");
    // 宽高属性只有起始值和结束值，长度只能为2
    if (propertyValues.length != 2) {
      ADRenderLogger
          .e("ADObjectAnimationBuilder 动画配置错误 value.length!=2 adAnimationModel：" +
              adAnimationModel.toString());
      return true;
    }
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

    if (layoutParams == null) {
      ADBrowserLogger.e("ADObjectAnimationBuilder view.getLayoutParams()为空");
      return true;
    }
    // 这个是执行动画的真正value
    int[] values = new int[2];
    for (int i = 0; i < propertyValues.length; i++) {
      if (propertyValues[i] == RIAIDConstants.Animation.VIEW_MEASURE_VALUE) {
        values[i] = getMeasureSize(view, adAnimationModel.propertyType);
      } else {
        values[i] = ToolHelper.dip2px(view.getContext(), propertyValues[i]);
      }
    }
    ValueAnimator valueAnimator = ValueAnimator.ofInt(values);
    valueAnimator.setDuration(adAnimationModel.duration);
    if (adAnimationModel.repeatCount == RIAIDConstants.Animation.REPEAT_INFINITE) {
      // 支持无限循环
      valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
      valueAnimator.setRepeatMode(ValueAnimator.RESTART);
    } else if (adAnimationModel.repeatCount > 1) {
      valueAnimator.setRepeatCount(adAnimationModel.repeatCount);
    }
    valueAnimator.addUpdateListener(animation -> {
      if (adAnimationModel.propertyType == ADAnimationModel.WIDTH) {
        layoutParams.width = (int) animation.getAnimatedValue();
      } else {
        layoutParams.height = (int) animation.getAnimatedValue();
      }
      view.setLayoutParams(layoutParams);
    });
    animatorSet.playTogether(valueAnimator);
    return false;
  }

  private static void buildScaleAnimator(@NonNull View view,
      @NonNull ADAnimationModel adAnimationModel, AnimatorSet animatorSet, float[] propertyValues) {
    ADBrowserLogger.i("ADViewPropertyAnimationBuilder 执行的是缩放动画");
    ObjectAnimator scaleX =
        ObjectAnimator.ofFloat(view, View.SCALE_X, propertyValues);
    ObjectAnimator scaleY =
        ObjectAnimator.ofFloat(view, View.SCALE_Y, propertyValues);
    scaleX.setDuration(adAnimationModel.duration);
    scaleY.setDuration(adAnimationModel.duration);
    if (adAnimationModel.repeatCount == RIAIDConstants.Animation.REPEAT_INFINITE) {
      scaleX.setRepeatCount(ValueAnimator.INFINITE);
      scaleX.setRepeatMode(ValueAnimator.RESTART);
      scaleY.setRepeatCount(ValueAnimator.INFINITE);
      scaleY.setRepeatMode(ValueAnimator.RESTART);
    } else if (adAnimationModel.repeatCount > 1) {
      scaleX.setRepeatCount(adAnimationModel.repeatCount);
      scaleY.setRepeatCount(adAnimationModel.repeatCount);
    }
    animatorSet.playTogether(scaleX, scaleY);
  }

  /**
   * 仅仅是可见性变化，不需要有延迟操作，直接执行即可。
   */
  private static void buildVisibilityAnimator(@NonNull View view, AnimatorSet animatorSet,
      float[] propertyValues) {
    if (propertyValues == null || propertyValues.length != 1) {
      return;
    }
    ADBrowserLogger.i("ADViewPropertyAnimationBuilder 执行的是可见性动画");
    ValueAnimator animator = ValueAnimator.ofFloat(propertyValues);
    animator.addListener(new ADAnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        // 直接执行
        view.setVisibility(propertyValues[0] == RIAIDConstants.Animation.VIEW_VISIBLE ? View.VISIBLE
            : View.INVISIBLE);
      }
    });
    animatorSet.playTogether(animator);
  }

  /**
   * 将测量的宽高存放到value中
   *
   * @param view         要测量的view
   * @param propertyType 要测量的属性，宽或者是高
   */
  private static int getMeasureSize(@NonNull View view,
      int propertyType) {
    // 对于render来说，渲染的view宽高是固定的，所以使用View.MeasureSpec.EXACTLY模式。
    if (propertyType == ADAnimationModel.WIDTH) {
      return view.getMeasuredWidth();
    } else {
      return view.getMeasuredHeight();
    }
  }
}
