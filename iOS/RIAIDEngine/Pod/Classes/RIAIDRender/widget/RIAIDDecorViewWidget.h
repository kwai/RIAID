//
//  RIAIDDecorViewWidget.h
//  KCADRender
//
//  Created by simon on 2021/12/28.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "RIAID.h"
#import "RIAIDTouchViewDelegate.h"
#import "RIAIDRenderContext.h"

NS_ASSUME_NONNULL_BEGIN

typedef void(^RIAIDRShareAnimationBlock)(BOOL isComplete);

/// 装饰 View 组件，负责包裹阴影视图、背景视图、真实渲染视图、前景
/// @discussion 层级中的特殊情况：
/// 1. 有阴影时：阴影包裹 --> (阴影 && (真实渲染视图 || 背景)) --> 前景，按照层级逐级包裹。阴影和背景同级，所以用阴影包裹作为
/// 他们的父视图，若无阴影，则不需要添加阴影包裹
/// 2. 背景：
///     a. 盒子节点：背景为单独的视图进行绘制
///         I. button、scroll 盒子有一个容器视图，会挂载到 realView 上
///         II. scroll 盒子当有背景是，也有添加背景视图
///     b. 组件节点：背景在真实的视图上进行绘制
@interface RIAIDDecorViewWidget : NSObject

@property (nonatomic, strong) RIAIDRenderContext *renderContext;

/// 传入是否为盒子的布尔，创建一个装饰组件
/// @param isBox 是否为盒子，YES 为盒子节点，NO 为非盒子节点
/// @param nodeInfo 节点对应的属性
/// @discussion 盒子布尔值的目的是为了区分盒子和非盒子，盒子节点背景绘制在新的视图上，组件节点背景绘制在真实视图上
- (instancetype)initWithIsBox:(BOOL)isBox
                   attributes:(nullable RIAIDNode *)nodeInfo
                     realView:(nullable UIView *)realView
               actionDelegate:(nullable id<RIAIDTouchViewDelegate>)actionDelegate;

/// 装饰视图的坐标及尺寸
@property (nonatomic, assign) CGRect frame;

/// 透明度快捷设置，方便动画的快速调用
@property (nonatomic, assign) CGFloat alpha;

/// view 事件的相关代理
@property (nonatomic, weak) id<RIAIDTouchViewDelegate> actionDelegate;

@property (nonatomic, assign) BOOL userInteractionEnabled;
@property (nonatomic, strong, readonly) UIView *realView;

/// 获取最终包裹的视图
- (UIView *)getDecorView;

/// 获得最终渲染出来的真实视图，比如 TextNode 的是 YYLabel，Video 的是 PlayerView
/// @discussion 值得注意的是，scrollView 将直接返回 scrollView，而不会再次拆解
- (UIView *)getRenderView;

/// 获取 widget 上的前景视图
- (UIView *)getForegroundView;

/// 当前的 widget 是否是 button
- (BOOL)isButton;

/// 配置 UI 自动化测试
- (void)configViewKey:(NSInteger)viewKey;

/// 配置真实视图
/// @param realView 最终需要显示的 view
- (void)setupRealView:(UIView *)realView;

/// 设置前景
- (void)setupForeground;

/// 配置背景
- (void)setupBackground;

/// 配置阴影
- (void)setupShadow;

/// 设置隐藏
- (void)setViewHidden:(BOOL)hidden;

/// 刷新属性
- (void)refreshAttributes;

/// 最终渲染图层是否是包裹起来的容器视图
/// @discussion scrollView、有单独的背景视图、有阴影。都可以理解为是包括起来的容器视图
- (BOOL)isContainer;

/// 执行场景内动画
/// @param newFrame 新的尺寸，动画的终态
/// @param newAlpha 新的透明度，动画的终态
/// @param duration 动画执行的时长
/// @param animationBlock 动画完成的回调
- (void)executeShareAnimationWithNewFrame:(CGRect)newFrame
                                 newAlpha:(CGFloat)newAlpha
                                 duration:(NSTimeInterval)duration
                            completeBlock:(RIAIDRShareAnimationBlock)animationBlock;

@end

NS_ASSUME_NONNULL_END
