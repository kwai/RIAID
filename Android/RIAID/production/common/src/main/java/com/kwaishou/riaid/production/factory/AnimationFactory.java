package com.kwaishou.riaid.production.factory;

import com.kuaishou.riaid.proto.ADAnimationModel;
import com.kuaishou.riaid.proto.ADInSceneAnimationTransitionModel;
import com.kuaishou.riaid.proto.nano.RIAIDConstants;

public class AnimationFactory {
    public static ADInSceneAnimationTransitionModel createAlphaAnim(int viewKey, float startAlpha, float endAlpha, long duration) {
        return ADInSceneAnimationTransitionModel.newBuilder()
                .setViewKey(viewKey)
                .setAnimation(ADAnimationModel.newBuilder()
                        .setPropertyType(ADAnimationModel.ViewPropertyType.ALPHA)
                        .setDuration(duration)
                        .addValues(startAlpha)
                        .addValues(endAlpha)
                        .build())
                .build();
    }

    public static ADInSceneAnimationTransitionModel createVisibilityAnim(int viewKey, boolean hidden) {
        return ADInSceneAnimationTransitionModel.newBuilder()
                .setViewKey(viewKey)
                .setAnimation(ADAnimationModel.newBuilder()
                        .setPropertyType(ADAnimationModel.ViewPropertyType.HIDDEN)
                        .setDuration(0)
                        .addValues(hidden ? RIAIDConstants.Animation.VIEW_INVISIBILITY : RIAIDConstants.Animation.VIEW_VISIBLE)
                        .build())
                .build();
    }
}
