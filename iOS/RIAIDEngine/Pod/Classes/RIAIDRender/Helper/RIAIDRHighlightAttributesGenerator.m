//
//  RIAIDRHighlightAttributesGenerator.m
//  KCADRender
//
//  Created by simon on 2022/1/3.
//

#import "RIAIDRHighlightAttributesGenerator.h"

#import "KCADRenderKBase.h"

@implementation RIAIDRHighlightAttributesGenerator

+ (nullable RIAIDAttributes *)getNewAttributesWithOrigin:(nullable RIAIDAttributes *)originAttributes
                                          diffAttributes:(nullable RIAIDAttributes *)diffAttributes {
    // copy 一份属性，用于生成最终的高亮属性
    RIAIDAttributes *attributes = nil;
    if (diffAttributes
        && (diffAttributes.common || diffAttributes.text)) {
        attributes = originAttributes.copy;
        // common 属性的处理
        if (diffAttributes.hasCommon && diffAttributes.common) {
            attributes.common = [self getNewCommonAttributes:originAttributes.common
                                              diffAttributes:diffAttributes.common];
        }
        // text 属性的处理
        if (diffAttributes.hasText && diffAttributes.text) {
            attributes.text = [self getNewTextAttributes:originAttributes.text
                                          diffAttributes:diffAttributes.text];
        }
        // image 属性的处理
        if (diffAttributes.hasImage && diffAttributes.image) {
            attributes.image = [self getNewImageAttributes:originAttributes.image
                                            diffAttributes:diffAttributes.image];
        }
        // lottie 属性的处理
        if (diffAttributes.hasLottie && diffAttributes.lottie) {
            attributes.lottie = [self getNewLottieAttributes:originAttributes.lottie
                                              diffAttributes:diffAttributes.lottie];
        }
    }
    
    return attributes;
}

+ (nullable RIAIDCommonAttributes *)getNewCommonAttributes:(nullable RIAIDCommonAttributes *)originCommon
                                            diffAttributes:(nullable RIAIDCommonAttributes *)diffCommon {
    RIAIDCommonAttributes *common = nil;
    if (diffCommon) {
        common = originCommon.copy;
        if (diffCommon.shapeType != RIAIDCommonAttributes_ShapeType_ShapeTypeUnknown) {
            common.shapeType = [KCADRenderKBase renderShapeType:diffCommon.shapeType];
        }
        if (diffCommon.hasAlpha && diffCommon.alpha) {
            common.alpha = diffCommon.alpha;
        }
        if (diffCommon.hasShadow && diffCommon.shadow) {
            common.shadow = diffCommon.shadow;
        }
        if (diffCommon.hasCornerRadius && diffCommon.cornerRadius) {
            common.cornerRadius = diffCommon.cornerRadius;
        }
        if (diffCommon.backgroundColor.length > 0) {
            common.backgroundColor = [KCADRenderKBase renderColorString:diffCommon.backgroundColor];
        }
        if (diffCommon.hasStroke && diffCommon.stroke) {
            common.stroke = diffCommon.stroke;
        }
        if (diffCommon.hasGradient && diffCommon.gradient) {
            common.gradient = diffCommon.gradient;
        }
        if (diffCommon.hasHidden && diffCommon.hidden) {
            common.hidden = diffCommon.hidden;
        }
    }
    return common;
}

+ (nullable RIAIDTextAttributes *)getNewTextAttributes:(nullable RIAIDTextAttributes *)originText
                                        diffAttributes:(nullable RIAIDTextAttributes *)diffText {
    RIAIDTextAttributes *text = nil;
    if (diffText) {
        text = originText.copy;
        if (diffText.text) {
            text.text = diffText.text;
        }
        if (diffText.hasFontSize && diffText.fontSize) {
            text.fontSize = diffText.fontSize;
        }
        if (diffText.fontName) {
            text.fontName = diffText.fontName;
        }
        if (diffText.fontColor.length > 0) {
            text.fontColor = diffText.fontColor;
        }
        if (diffText.hasMaxLines && diffText.maxLines) {
            text.maxLines = diffText.maxLines;
        }
        if (diffText.ellipsize != RIAIDTextAttributes_Ellipsize_EllipsizeUnknown) {
            text.ellipsize = diffText.ellipsize;
        }
        if (diffText.hasAlign && diffText.align) {
            text.align = diffText.align;
        }
        if (diffText.hasBold && diffText.bold) {
            text.bold = diffText.bold;
        }
        if (diffText.hasTilt && diffText.tilt) {
            text.tilt = diffText.tilt;
        }
        if (diffText.lineMode != RIAIDTextAttributes_LineMode_LineModeUnknown) {
            text.lineMode = diffText.lineMode;
        }
        if (diffText.hasLineSpace && diffText.lineSpace) {
            text.lineSpace = diffText.lineSpace;
        }
        if (diffText.highlightColor.length > 0) {
            text.highlightColor = diffText.highlightColor;
        }
        if (diffText.richListArray.count > 0) {
            text.richListArray = diffText.richListArray;
        }
    }
    return text;
}

+ (nullable RIAIDImageAttributes *)getNewImageAttributes:(nullable RIAIDImageAttributes *)originImage
                                          diffAttributes:(nullable RIAIDImageAttributes *)diffImage {
    RIAIDImageAttributes *image = nil;
    if (originImage) {
        image = originImage.copy;
        if (diffImage.URL.length > 0) {
            image.URL = diffImage.URL;
        }
        if (diffImage.highlightURL.length > 0) {
            image.highlightURL = diffImage.highlightURL;
        }
        if (diffImage.scaleType != RIAIDImageAttributes_ScaleType_ScaleTypeUnknown) {
            image.scaleType = diffImage.scaleType;
        }
    }
    return image;
}

+ (nullable RIAIDLottieAttributes *)getNewLottieAttributes:(nullable RIAIDLottieAttributes *)originLottie
                                            diffAttributes:(nullable RIAIDLottieAttributes *)diffLottie {
    RIAIDLottieAttributes *lottie = nil;
    if (diffLottie) {
        lottie = originLottie.copy;
        if (diffLottie.URL.length > 0) {
            lottie.URL = diffLottie.URL;
        }
        if (diffLottie.hasSpeed && diffLottie.speed) {
            lottie.speed = diffLottie.speed;
        }
        if (diffLottie.hasProgress && diffLottie.progress) {
            lottie.progress = diffLottie.progress;
        }
        if (diffLottie.hasRepeat && diffLottie.repeat) {
            lottie.repeat = diffLottie.repeat;
        }
        if (diffLottie.repeatMode != RIAIDLottieAttributes_RepeatMode_RepeatModeUnknown) {
            lottie.repeatMode = diffLottie.repeatMode;
        }
        if (diffLottie.hasAutoPlay && diffLottie.autoPlay) {
            lottie.autoPlay = diffLottie.autoPlay;
        }
        if (diffLottie.replaceTextListArray.count > 0) {
            lottie.replaceTextListArray = diffLottie.replaceTextListArray;
        }
    }
    return lottie;
}

@end
