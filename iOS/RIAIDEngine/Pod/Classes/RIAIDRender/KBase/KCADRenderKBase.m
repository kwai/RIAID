//
//  KCADRenderKBase.m
//  KCADRender
//
//  Created by simon on 2021/12/9.
//

#import "KCADRenderKBase.h"

#pragma mark - default value
/// 默认矩形
RIAIDCommonAttributes_ShapeType kKCADRDefaultShapeType = RIAIDCommonAttributes_ShapeType_ShapeTypeRectangle;

/// 默认动画重复次数
NSInteger kRIAIDDefaultLottieRepeatCount = 1;

@implementation KCADRenderKBase

// 判断是否有背景
+ (BOOL)hasBackground:(RIAIDAttributes *)attributes {
    // 需要背景的组件，根据属性判断有无背景
    return attributes.hasCommon
            && [self renderShapeType:attributes.common.shapeType] != RIAIDCommonAttributes_ShapeType_ShapeTypeUnknown;
}

// 根据角度，获取渐变的开始及结束坐标
+ (void)getStartPoint:(NSValue * _Nullable __autoreleasing *)startPointValue
             endPoint:(NSValue * _Nullable __autoreleasing *)endPointValue
              byAngle:(RIAIDGradient_GradientAngle)angle {
    // TODO: 达豪澄清了安卓角度，后续使用三角函数更改，暂时先手动改
    CGPoint startPoint = CGPointMake(0.5f, 0.5f);
    CGPoint endPoint = CGPointZero;
    switch (angle) {
        case RIAIDGradient_GradientAngle_Angle0: {
            endPoint = CGPointMake(1.f, 0.5f);
        } break;
        case RIAIDGradient_GradientAngle_Angle45: {
            endPoint = CGPointMake(1.f, 0.f);
        } break;
        case RIAIDGradient_GradientAngle_Angle90: {
            endPoint = CGPointMake(0.5f, 0.f);
        } break;
        case RIAIDGradient_GradientAngle_Angle135: {
            endPoint = CGPointMake(0.f, 0.f);
        } break;
        case RIAIDGradient_GradientAngle_Angle180: {
            endPoint = CGPointMake(0.f, 0.5f);
        } break;
        case RIAIDGradient_GradientAngle_Angle225: {
            endPoint = CGPointMake(0.f, 1.f);
        } break;
        case RIAIDGradient_GradientAngle_Angle270: {
            endPoint = CGPointMake(0.5f, 1.f);
        } break;
        case RIAIDGradient_GradientAngle_Angle315: {
            endPoint = CGPointMake(1.f, 1.f);
        } break;
        default:
            break;
    }
    *startPointValue = [NSValue valueWithCGPoint:startPoint];
    *endPointValue = [NSValue valueWithCGPoint:endPoint];
}

/// 根据传入的宽高大小，得到经过默认值处理的最终宽高
/// @disscussion 内部会进行默认值处理，默认值为 -2
+ (CGFloat)renderWidth:(RIAIDNode *)node {
    CGFloat width = RIAIDRenderWrapContent;
    if (node.hasLayout
        && (node.layout.width >= 0
            || node.layout.width == RIAIDRenderWrapContent
            || node.layout.width == RIAIDRenderMatchParent)) {
        width = node.layout.width;
    }
    return width;
}

+ (CGFloat)renderHeight:(RIAIDNode *)node {
    CGFloat height = RIAIDRenderWrapContent;
    if (node.hasLayout
        && (node.layout.height >= 0
            || node.layout.height == RIAIDRenderWrapContent
            || node.layout.height == RIAIDRenderMatchParent)) {
        height = node.layout.height;
    }
    return height;
}

/// 根据传入色值，得到最终渲染上屏的色值
/// @disscussion 内部会进行默认值处理，若传空，则返回默认色值，默认为 #00FFFFFF
+ (NSString *)renderColorString:(nullable NSString *)colorString {
    return colorString.length > 0 ? colorString : RIAIDRenderDefaultColor;
}

/// 根据传入的文本色值，得到最终渲染上屏的文本色值
/// @disscussion 内部会进行默认值处理，若传空，则返回默认颜色色值，默认为 #00000000
+ (NSString *)renderTextColorString:(nullable NSString *)textColorString {
    return textColorString.length > 0 ? textColorString : RIAIDRenderDefaultFontColor;
}

/// 根据传入的文本对齐属性，得到水平对齐状态
/// @disscussion 内部会进行默认处理，默认为 start
+ (RIAIDTextAttributes_Align_Horizontal)renderTextHorizontalAlign:(nullable RIAIDTextAttributes_Align *)textAlign {
    RIAIDTextAttributes_Align_Horizontal alignH = (int32_t)RIAIDRenderDefaultTextHorizontalMode;
    if (textAlign
        && textAlign.horizontal != RIAIDTextAttributes_Align_Horizontal_HorizontalUnknown) {
        alignH = textAlign.horizontal;
    }
    return alignH;
}

/// 根据传入的文本对齐属性，得到垂直对齐状态
/// @disscussion 内部会进行默认处理，默认为 top
+ (RIAIDTextAttributes_Align_Vertical)renderTextVerticalAlign:(nullable RIAIDTextAttributes_Align *)textAlign {
    RIAIDTextAttributes_Align_Vertical alignV = (int32_t)RIAIDRenderDefaultTextVerticalMode;
    if (textAlign
        && textAlign.vertical != RIAIDTextAttributes_Align_Vertical_VerticalUnknown) {
        alignV = textAlign.vertical;
    }
    return alignV;
}

/// 根据传入的组件形状类型，得到最终渲染上屏的形状
/// @disscussion 默认为 rectangle
+ (RIAIDCommonAttributes_ShapeType)renderShapeType:(RIAIDCommonAttributes_ShapeType)shapeType {
    return shapeType != RIAIDCommonAttributes_ShapeType_ShapeTypeUnknown ? shapeType : kKCADRDefaultShapeType;
}

/// 根据传入的图片展示类型，得到最终渲染上屏的展示类型
/// @disscussion 默认为 center_crop
+ (RIAIDImageAttributes_ScaleType)renderImageScaleType:(RIAIDImageAttributes_ScaleType)scaleType {
    return scaleType != RIAIDImageAttributes_ScaleType_ScaleTypeUnknown ? scaleType : (int32_t)RIAIDRenderDefaultImageScaleType;
}

/// 根据传入的字号，经过默认值处理，得到最终字号
/// @disscussion 默认为 15
+ (CGFloat)renderFontSize:(CGFloat)fontSize {
    return fontSize > 0 ? fontSize : (CGFloat)RIAIDRenderDefaultFontSize;
}

/// 根据传入的 lottie 动画重复次数，得到经过默认值筛选的最终动画次数
/// @disscussion 默认动画重复次数为 1
+ (NSInteger)renderLottieRepeatCount:(NSInteger)repeatCount {
    return repeatCount > 0 ? repeatCount : kRIAIDDefaultLottieRepeatCount;
}

/// 根据传入的 lottie 动画播放速度，得到经过默认值筛选的最终动画速度
/// @disscussion 默认动画速度为 1
+ (CGFloat)renderLottieSpeed:(CGFloat)speed {
    return speed > 0 ? speed : RIAIDRenderDefaultLottieSpeed;
}

/// 根据传入的 lottie 重播类型，判断是否需要进行 lottie 反转重播
/// @disscussion 默认从头开始播放
+ (BOOL)renderLottieRepeatMode:(RIAIDLottieAttributes_RepeatMode)repeatMode {
    return repeatMode != (int32_t)RIAIDRenderDefaultLottieRepeatMode;
}

/// 根据传入的属性，判断是否需要显示滚动条
/// @disscussion 默认不显示滚动条
+ (BOOL)renderShowScrollBar:(RIAIDAttributes *)attributes {
    BOOL showScrollBar = NO;
    if (attributes.hasScroll) {
        showScrollBar = attributes.scroll.showScrollbar.value;
    }
    return showScrollBar;
}

/// 根据传入的属性，拿到渐变角度
/// @param gradient 默认为 0°
+ (RIAIDGradient_GradientAngle)renderGradientAngle:(RIAIDGradient *)gradient {
    return gradient.angle == RIAIDGradient_GradientAngle_AngleUnknown
                             ? (int32_t)RIAIDRenderDefaultGradientAngle
                             : gradient.angle;
}

/// 获取视频组件的尺寸视频类型
+ (UIViewContentMode)renderVideoScaleType {
    return UIViewContentModeScaleAspectFill;
}

+ (RIAIDTextAttributes_RichText_RichAlign)richTextAlign:(RIAIDTextAttributes_RichText *)richText {
    RIAIDTextAttributes_RichText_RichAlign richTextAlign = RIAIDTextAttributes_RichText_RichAlign_RichAlignCenter;
    if (richText != nil) {
        richTextAlign = richText.richAlign;
    }
    return richTextAlign;
}

@end
