//
//  UIView+RIAIDCornerShadow.h
//  KCADRender
//
//  Created by simon on 2021/12/26.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/// 描述圆角大小
typedef struct {
    CGFloat topLeft;
    CGFloat topRight;
    CGFloat bottomLeft;
    CGFloat bottomRight;
} RIAIDCornerRadii;
RIAIDCornerRadii RIAIDCornerRadiiMake(CGFloat topLeft, CGFloat topRight, CGFloat bottomLeft, CGFloat bottomRight);

/// 阴影/圆角/边框/绘制 分类
@interface UIView (RIAIDCornerShadow)

@property (nonatomic, assign) CGSize layerDrawSize;
@property (nonatomic, strong, readonly) CAShapeLayer *borderLayer;
@property (nonatomic, strong, readonly) CAShapeLayer *maskLayer;
@property (nonatomic, strong, readonly) UIView *shadowBackgroundView;

/// 绘画圆角
/// @param cornerRadii 圆角信息
- (void)addCornersWithCornerRadii:(RIAIDCornerRadii)cornerRadii;

/// 设置阴影，阴影会新建一个视图，视图会插入目标视图下
/// @param shadowLayerBlock block 内的 shadowLayer 为新生成的阴影视图对应的 layer
- (void)addShadowLayerWithShadowRadius:(CGFloat)radius
                      shadowLayerBlock:(nullable void (^)(CALayer * shadowLayer))shadowLayerBlock;

/// 设置实线边框
/// @param borderColor 边框颜色
/// @param borderWidth 边框宽度
- (void)addBorderColor:(nullable UIColor *)borderColor
           borderWidth:(CGFloat)borderWidth
           cornerRadii:(RIAIDCornerRadii)cornerRadii;

/// 设置虚线边框
/// @param borderColor 边框颜色
/// @param borderWidth 边框宽度
/// @param dashGap 边框间隙长度
/// @param dashWidth 边框虚线长度
- (void)addDashBorderColor:(nullable UIColor *)borderColor
               borderWidth:(CGFloat)borderWidth
                   dashGap:(CGFloat)dashGap
                 dashWidth:(CGFloat)dashWidth
               cornerRadii:(RIAIDCornerRadii)cornerRadii;

/// 根据目标尺寸和圆角，创建贝塞尔绘制路径
/// @param size 目标尺寸
/// @param cornerRadii 目标圆角
+ (UIBezierPath *)createCornersPathWithSize:(CGSize)size cornerRadii:(RIAIDCornerRadii)cornerRadii;

@end

NS_ASSUME_NONNULL_END
