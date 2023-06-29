package com.kwaishou.riaid.production.factory;

import com.kuaishou.riaid.proto.*;

import java.util.List;


public class ActionFactory {


    public static ADActionModel.Builder createVisibility(int sceneKey, long duration, float startAlpha, float endAlpha, boolean hidden) {
        return ADActionModel.newBuilder()
                .setTransition(
                        ADTransitionActionModel.newBuilder()
                                .addTransitions(TransitionFactory.createVisibilityTransitionParent(
                                        sceneKey, duration, startAlpha, endAlpha, hidden))
                                .build()
                );
    }

    public static ADActionModel.Builder createJustVisibility(int sceneKey, boolean hidden) {
        float startAlpha;
        float endAlpha;
        if (hidden) {
            startAlpha = 1f;
            endAlpha = 0f;
        } else {
            startAlpha = 0f;
            endAlpha = 1f;
        }
        return ADActionModel.newBuilder()
                .setTransition(
                        ADTransitionActionModel.newBuilder()
                                .addTransitions(TransitionFactory.createVisibilityTransitionParent(
                                        sceneKey, 0, startAlpha, endAlpha, hidden))
                                .build()
                );
    }

    public static ADActionModel createADConversion() {
        return ADActionModel.newBuilder()
                .setConversion(ADConversionActionModel.newBuilder()
                        .build())
                .build();
    }

    public static ADActionModel createADConversion(String url, String deepLink) {
        return ADActionModel.newBuilder()
                .setConversion(ADConversionActionModel.newBuilder()
                        .setUrl(VariableFactory.getVar(url))
                        .setDeepLink(VariableFactory.getVar(deepLink))
                        .build())
                .build();
    }

    public static ADActionModel createADConversionNull() {
        return ADActionModel.newBuilder()
                .setConversion(ADConversionActionModel.newBuilder()
                        .build())
                .build();
    }

    public static ADActionModel createCancelTimer(int triggerKey) {
        return ADActionModel.newBuilder()
                .setCancelTimer(
                        ADCancelTimerActionModel.newBuilder()
                                .setTriggerKey(triggerKey)
                                .build()
                ).build();
    }

    public static ADActionModel createCancelDeviceMotion(int triggerKey) {
        return ADActionModel.newBuilder()
                .setCancelDeviceMotion(
                        ADCancelDeviceMotionActionModel.newBuilder()
                                .setTriggerKey(triggerKey)
                                .build()
                ).build();
    }

    public static ADConversionActionModel createADConversionAction(String url, String deepLink) {
        return ADConversionActionModel.newBuilder()
                .setUrl(VariableFactory.getVar(url))
                .setDeepLink(VariableFactory.getVar(deepLink))
                .build();
    }

    public static ADConditionChangeActionModel createADConditionChangeAction(String name, String value) {
        return ADConditionChangeActionModel.newBuilder()
                .setCondition(ADConditionModel.newBuilder()
                        .setConditionName(name)
                        .setConditionValue(value)
                        .build())
                .build();
    }

    public static ADActionModel createVariableChangeBoolAction(int key, boolean value) {
        return ADActionModel.newBuilder()
                .setVariableChange(
                        ADVariableChangeActionModel.newBuilder()
                                .setVariable(BasicVariable.newBuilder()
                                        .setKey(key)
                                        .setValue(BasicVariableValue.Value.newBuilder()
                                                .setType(BasicVariableValue.Type.BOOL)
                                                .setB(value)
                                                .build())
                                        .build())
                                .build()
                )
                .build();
    }

    public static ADActionModel createVariableChangeIntAction(int key, long value) {
        return ADActionModel.newBuilder()
                .setVariableChange(
                        ADVariableChangeActionModel.newBuilder()
                                .setVariable(BasicVariable.newBuilder()
                                        .setKey(key)
                                        .setValue(BasicVariableValue.Value.newBuilder()
                                                .setType(BasicVariableValue.Type.INTEGER)
                                                .setI(value)
                                                .build())
                                        .build())
                                .build()
                )
                .build();
    }

    public static ADActionModel createInSceneVisibilityAnimAction(int viewKey, boolean hidden) {
        return ADActionModel.newBuilder()
                .setTransition(
                        ADTransitionActionModel.newBuilder()
                                .addTransitions(
                                        ADTransitionModel.newBuilder()
                                                .setInSceneAnimation(
                                                        AnimationFactory.createVisibilityAnim(
                                                                viewKey, hidden)
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    public static ADActionModel createTriggerAction(List<Integer> triggerKeys) {
        return ADActionModel.newBuilder()
                .setTrigger(ADTriggerActionModel.newBuilder()
                        .addAllTriggerKeys(triggerKeys)
                        .build())
                .build();
    }

    public static ADActionModel createTriggerAction(int triggerKey) {
        return ADActionModel.newBuilder()
                .setTrigger(ADTriggerActionModel.newBuilder()
                        .addTriggerKeys(triggerKey)
                        .build())
                .build();
    }

    public static ADActionModel createVideo(int viewKey, ADVideoActionModel.VideoControlType type) {
        return ADActionModel.newBuilder()
                .setVideo(ADVideoActionModel.newBuilder()
                        .setViewKey(viewKey)
                        .setType(type)
                        .build())
                .build();
    }

    public static ADActionModel createLottie(int viewKey, ADLottieActionModel.LottieControlType type) {
        return ADActionModel.newBuilder()
                .setLottie(ADLottieActionModel.newBuilder()
                        .setViewKey(viewKey)
                        .setType(type)
                        .build())
                .build();
    }

    public static ADActionModel createUrl(String key, String url) {
        return ADActionModel.newBuilder()
                .setUrl(ADUrlActionModel.newBuilder()
                        .putBundle(key, url)
                        .build())
                .build();
    }

    public static ADActionModel createCustomerAction(String key, String value) {
        return ADActionModel.newBuilder()
                .setCustom(
                        ADCustomActionModel.newBuilder()
                                .putParameters(key, value)
                                .build()
                )
                .build();
    }

    public static ADActionModel createClickableAction(int key, boolean clickable) {
        return ADActionModel.newBuilder()
                .setClickable(
                        ADClickableActionModel.newBuilder()
                                .setViewKey(key)
                                .setClickable(clickable)
                                .build()
                )
                .build();
    }

    public static ADActionModel createTransitionAction(ADTransitionModel transitionModel) {
        return ADActionModel.newBuilder()
                .setTransition(
                        ADTransitionActionModel.newBuilder()
                                .addTransitions(
                                        transitionModel
                                )
                                .build()
                )
                .build();
    }

    public static ADActionModel createTransitionAction(List<ADTransitionModel> transitionModels) {
        return ADActionModel.newBuilder()
                .setTransition(
                        ADTransitionActionModel.newBuilder()
                                .addAllTransitions(
                                        transitionModels
                                )
                                .build()
                )
                .build();
    }

}
