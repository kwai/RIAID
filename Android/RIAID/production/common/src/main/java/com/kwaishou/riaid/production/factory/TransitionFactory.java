package com.kwaishou.riaid.production.factory;

import com.kuaishou.riaid.proto.ADTemplateTransitionModel;
import com.kuaishou.riaid.proto.ADTransitionModel;
import com.kuaishou.riaid.proto.ADVisibilityTransitionModel;

public class TransitionFactory {
    public static ADTransitionModel createVisibilityTransitionParent(int sceneKey, long duration, float startAlpha, float endAlpha, boolean hidden) {
       return ADTransitionModel.newBuilder()
               .setVisibility(createVisibilityTransition(sceneKey, duration, startAlpha, endAlpha, hidden))
               .build();
    }
    public static ADVisibilityTransitionModel createVisibilityTransition(int sceneKey, long duration, float startAlpha, float endAlpha, boolean hidden) {
        return ADVisibilityTransitionModel.newBuilder()
                .setSceneKey(sceneKey)
                .setDuration(duration)
                .setStartAlpha(startAlpha)
                .setEndAlpha(endAlpha)
                .setHidden(hidden)
                .build();
    }

    public static ADTemplateTransitionModel createADTemplateTransition(int sceneKey, ADTemplateTransitionModel.TemplateType template, long duration) {
        return ADTemplateTransitionModel.newBuilder()
                .setDuration(duration)
                .setTemplate(template)
                .setSceneKey(sceneKey)
                .build();
    }

}
