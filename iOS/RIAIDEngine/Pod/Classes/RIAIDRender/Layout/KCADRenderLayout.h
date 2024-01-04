//
//  KCADRenderLayout.h
//  KCADRender
//
//  Created by simon on 2021/11/29.
//

#import <UIKit/UIKit.h>

#import "RIAIDDecorViewWidget.h"

NS_ASSUME_NONNULL_BEGIN

/// ADRender 布局类，负责视图的装配工作
@interface KCADRenderLayout : NSObject

/// 将目标视图添加到跟视图中
/// @param targetView 目标视图
/// @param decorView 根视图
/// @param frame 目标视图计算出的绝对坐标
+ (void)addTargetView:(nullable RIAIDDecorViewWidget *)targetView
          toDecorView:(nullable UIView *)decorView
    withAbsoluteFrame:(CGRect)frame;

/// 父视图添加子视图，该方法不会造成二次添加的问题
/// @param parentView 父视图
/// @param subView 子视图
+ (void)parentView:(UIView *)parentView addSubView:(UIView *)subView;

@end

NS_ASSUME_NONNULL_END
