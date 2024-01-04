//
//  KCADRender.h
//  KCADRender
//
//  Created by simon on 2021/11/9.
//

#import <Foundation/Foundation.h>

#pragma mark - pb && context
#import "RIAID.h"
#import "RIAIDRenderContext.h"

NS_ASSUME_NONNULL_BEGIN
@class KCADRenderNode, RIAIDDecorViewWidget;

/// AdRender 布局引擎入口
///
/// 负责样式控制、视图布局及渲染
/// - Render 中的 decorView 可以通过外部注入，将外部视图变为 Render 的根视图
@interface KCADRender : NSObject

/// 渲染出的最终根视图
@property (nonatomic, strong, readonly) UIView *decorView;

/// 根节点数据
@property (nonatomic, strong, readonly) KCADRenderNode *rootNode;

#pragma mark - class method

/// 外部注入 decorView
/// - Parameter injectView: 需要注入的外部视图
///
/// 该方法是通过外部注入一个视图，代替渲染的跟视图，从而改变 Render 挂载的根视图。该功能可用于外部视图注入
+ (instancetype)createRenderWithView:(UIView *)injectView;

#pragma mark - instance method
- (instancetype)init NS_UNAVAILABLE;

- (instancetype)initWithRenderInfo:(nullable RIAIDNode *)renderInfo context:(nullable RIAIDRenderContext *)context;

/// 计算尺寸
/// @param renderInfo render绘制信息
/// @param estimateWidth 外部给定的约束宽度
/// @param estimateHeight 外部给定的约束高度
/// @param context Render绘制需要的上下文
+ (CGSize)sizeWithRenderInfo:(RIAIDNode *)renderInfo
               estimateWidth:(CGFloat)estimateWidth
              estimateHeight:(CGFloat)estimateHeight
                     context:(nullable RIAIDRenderContext *)context;

/// 开始渲染
/// @param estimateWidth 外部给定的约束宽度
/// @param estimateHeight 外部给定的约束高度
- (void)renderWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight;

/// 当 Render 的数据发生变化时，更新 Render 的内部布局
/// @discussion 当 Render 内部数据发生变化，需要更新布局时，调用该方法。该方法不会改变画布的大小
- (void)layoutIfNeeded;

/// 根据新的预估尺寸，更改布局
/// @param estimateSize 新的预估尺寸
/// @discussion 当画布尺寸发生变化时，需要调用此方法，更改 Render 的布局
- (void)reRenderWithEstimateSize:(CGSize)estimateSize;

/// 通过 renderKey 查找渲染树中对应的 render
/// @param renderKey render 节点的 key
/// @return KCADRenderNode 为目标 Render
- (KCADRenderNode *)findRenderNodeByKey:(NSInteger)renderKey;

/// 通过 renderKey 查找渲染树中对应的 view
/// @param renderKey render 节点的 key
/// @return RIAIDDecorViewWidget 为 view 的包装类，可通过其 getDecorView 方法，获取最外层的 view 用于位置的移动
- (RIAIDDecorViewWidget *)findViewByKey:(NSInteger)renderKey;

/// 和传入的 render 做比较，若有差异，则将有改动的所有 renderView 抛出，供 Browser 做动画
/// @param newNodeInfo 新的 render 数据
/// @param estimateSize 外部给定的约束
- (NSArray<KCADRenderNode *> *)diffWithRender:(RIAIDNode *)newNodeInfo estimateSize:(CGSize)estimateSize;
@end

NS_ASSUME_NONNULL_END
