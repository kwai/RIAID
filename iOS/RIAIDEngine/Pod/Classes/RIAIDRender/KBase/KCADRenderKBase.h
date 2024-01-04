//
//  KCADRenderKBase.h
//  KCADRender
//
//  Created by simon on 2021/12/9.
//

#import <Foundation/Foundation.h>

#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

#pragma mark - default value
/// 默认矩形
FOUNDATION_EXPORT RIAIDCommonAttributes_ShapeType kKCADRDefaultShapeType;

/// 默认动画重复次数
FOUNDATION_EXPORT NSInteger kRIAIDDefaultLottieRepeatCount;

#pragma mark - KCADRenderKBase
/// ADRender 知识库，通过方法和常量定义 Render 的通用知识
/// @discussion eg: 高度为 -1 代表撑满布局，-2 代表自适应尺寸
@interface KCADRenderKBase : NSObject

/// 通过通用属性，判断是否有背景
/// @param attributes 属性
+ (BOOL)hasBackground:(RIAIDAttributes *)attributes;

/// 根据角度获取渐变色的开始位置和结束位置
/// @param startPoint 开始位置指针，用 NSValue 封装的 CGPoint
/// @param endPoint 结束位置指针，用 NSValue 封装的 CGPoint
/// @param angle 需要测算的枚举
+ (void)getStartPoint:(NSValue * _Nullable __autoreleasing * _Nullable)startPoint
             endPoint:(NSValue * _Nullable __autoreleasing * _Nullable)endPoint
              byAngle:(RIAIDGradient_GradientAngle)angle;

/// 根据传入的 layout 属性，得到经过默认值处理的最终宽高
/// @disscussion 内部会进行默认值处理，默认值为 -2
+ (CGFloat)renderWidth:(RIAIDNode *)node;
+ (CGFloat)renderHeight:(RIAIDNode *)node;

/// 根据传入色值，得到最终渲染上屏的色值
/// @disscussion 内部会进行默认值处理，若传空，则返回默认色值，默认为 #00FFFFFF
+ (NSString *)renderColorString:(nullable NSString *)colorString;

/// 根据传入的文本色值，得到最终渲染上屏的文本色值
/// @disscussion 内部会进行默认值处理，若传空，则返回默认颜色色值，默认为 #00000000
+ (NSString *)renderTextColorString:(nullable NSString *)textColorString;

/// 根据传入的文本对齐属性，得到水平对齐状态
/// @disscussion 内部会进行默认处理，默认为 start
+ (RIAIDTextAttributes_Align_Horizontal)renderTextHorizontalAlign:(nullable RIAIDTextAttributes_Align *)textAlign;

/// 根据传入的文本对齐属性，得到垂直对齐状态
/// @disscussion 内部会进行默认处理，默认为 top
+ (RIAIDTextAttributes_Align_Vertical)renderTextVerticalAlign:(nullable RIAIDTextAttributes_Align *)textAlign;

/// 根据传入的组件形状类型，得到最终渲染上屏的形状
/// @disscussion 默认为 rectangle
+ (RIAIDCommonAttributes_ShapeType)renderShapeType:(RIAIDCommonAttributes_ShapeType)shapeType;

/// 根据传入的图片展示类型，得到最终渲染上屏的展示类型
/// @disscussion 默认为 center_crop
+ (RIAIDImageAttributes_ScaleType)renderImageScaleType:(RIAIDImageAttributes_ScaleType)scaleType;

/// 根据传入的字号，经过默认值处理，得到最终字号
/// @disscussion 默认为 15
+ (CGFloat)renderFontSize:(CGFloat)fontSize;

/// 根据传入的 lottie 动画重复次数，得到经过默认值筛选的最终动画次数
/// @disscussion 默认动画重复次数为 1
+ (NSInteger)renderLottieRepeatCount:(NSInteger)repeatCount;

/// 根据传入的 lottie 动画播放速度，得到经过默认值筛选的最终动画速度
/// @disscussion 默认动画速度为 1
+ (CGFloat)renderLottieSpeed:(CGFloat)speed;

/// 根据传入的 lottie 重播类型，判断是否需要进行 lottie 反转重播
/// @disscussion 默认从头开始播放
+ (BOOL)renderLottieRepeatMode:(RIAIDLottieAttributes_RepeatMode)repeatMode;

/// 根据传入的属性，判断是否需要显示滚动条
/// @disscussion 默认不显示滚动条
+ (BOOL)renderShowScrollBar:(RIAIDAttributes *)attributes;

/// 根据传入的属性，拿到渐变角度
/// @param gradient 默认为 0°
+ (RIAIDGradient_GradientAngle)renderGradientAngle:(RIAIDGradient *)gradient;

/// 获取视频组件的尺寸视频类型
+ (UIViewContentMode)renderVideoScaleType;

/// 根据富文本的属性，获取富文本内容的对齐方式
/// @param richText 富文本信息
/// @discussion 默认为居中对齐
+ (RIAIDTextAttributes_RichText_RichAlign)richTextAlign:(RIAIDTextAttributes_RichText *)richText;

@end

NS_ASSUME_NONNULL_END
