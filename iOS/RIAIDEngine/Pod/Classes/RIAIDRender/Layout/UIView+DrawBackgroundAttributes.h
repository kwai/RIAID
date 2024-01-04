//
//  UIView+DrawBackgroundAttributes.h
//  KCADRender
//
//  Created by simon on 2021/12/12.
//

#import <UIKit/UIKit.h>

#import "RIAID.h"
#import "UIView+RIAIDCornerShadow.h"
#import "RIAIDRenderContext.h"

NS_ASSUME_NONNULL_BEGIN

/// 背景绘制器
@interface UIView (DrawBackgroundAttributes)

@property (nonatomic, strong, readonly) CAGradientLayer *gradientLayer;

/// 给 view 添加背景属性
/// @param commonAttributes 通用背景属性
/// @param size 视图尺寸
- (void)drawWithCommonAttributes:(RIAIDCommonAttributes *)commonAttributes
                            size:(CGSize)size
                         context:(RIAIDRenderContext *)context;

/// 给 view 添加阴影
/// @param shadow Riaid 阴影属性
- (void)drawShadow:(RIAIDShadow *)shadow
           context:(RIAIDRenderContext *)context;

+ (nullable UIBezierPath *)getNewPathWithCommonAttributes:(RIAIDCommonAttributes *)commonAttributes
                                                     size:(CGSize)size;

+ (nullable UIBezierPath *)getNewShadowPathWithCommonAttributes:(RIAIDShadow *)shadow size:(CGSize)size;

@end

NS_ASSUME_NONNULL_END
