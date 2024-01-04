//
//  KCADRenderNode.h
//  KCADRender
//
//  Created by simon on 2021/11/11.
//

#import <Foundation/Foundation.h>

#import "RIAID.h"
#import "RIAIDRenderContext.h"
#import "RIAIDDecorViewWidget.h"
@class KCADRenderNode;
@protocol RIAIDTouchViewDelegate;

NS_ASSUME_NONNULL_BEGIN

@protocol RIAIDNodeDiffViewInfoInterface <NSObject>

/// 新的透明度，用于动画进行变换使用，外部赋值
@property (nonatomic, assign) CGFloat newAlpha;

/// 新的 frame，用于动画进行变换使用，外部赋值
@property (nonatomic, assign) CGRect newFrame;

@end

@protocol RIAIDNodeViewInfoInterface <NSObject>

/// 视图的透明度
@property (nonatomic, assign) CGFloat alpha;

/// 该节点下对应 view 的尺寸，若当前节点拥有子节点，则会根据子节点尺寸进行自适应
/// @discussion 这里之所以会和 frame 区分开，是因为会进行 size 计算，后进行位置计算
@property (nonatomic, assign) CGSize size;

/// 该节点下 view 最终渲染时的绝对坐标
@property (nonatomic, assign) CGRect frame;

@end

@protocol RIAIDWrapperViewInterface <RIAIDNodeViewInfoInterface, RIAIDNodeDiffViewInfoInterface>

/// 更新 view 的透明度和 frame，当 Browser 进行完一次动画后，需要将新的 frame 和 透明度给到节点，便于下次的动画变换
/// @param alpha 新的透明度
/// @param frame 新的 frame
- (void)updateViewWithAlpha:(CGFloat)alpha frame:(CGRect)frame;

/// 获取真实的 view
- (UIView *)getRealView;

@end

@protocol KCADRenderNodeDelegate <NSObject>

/// 将生成 Node 的能力通过代理让外部实现，让 Node 内部拥有将 RenderModel 转化为 Node 的能力
/// @param renderInfo 渲染数据模型
- (KCADRenderNode *)generateNodeWithRenderInfo:(RIAIDNode *)renderInfo context:(RIAIDRenderContext *)context;

@end

/// 渲染视图树中的节点 父类
@interface KCADRenderNode : NSObject<RIAIDWrapperViewInterface, RIAIDTouchViewDelegate>

/// ADRender 所需要的上下文信息，包括事件绑定、数据绑定服务
@property (nonatomic, strong) RIAIDRenderContext *context;

/// 节点中对应的 Render 基础信息
@property (nonatomic, strong) RIAIDNode *renderInfo;

/// 组件节点下进行渲染的真实 view 组件
@property (nonatomic, strong, readonly) RIAIDDecorViewWidget *nodeView;

/// 父节点，用于形成树形结构
@property (nonatomic, weak) KCADRenderNode *parentNode;

/// 节点代理，用于子类委托过来的节点解析
@property (nonatomic, strong) id<KCADRenderNodeDelegate> delegate;

/// 子节点相对于自己的偏移量，用于子节点的绝对坐标计算
/// @discussion key 为子节点对象，value 为 CGPoint 类型的偏移量
@property (nonatomic, strong) NSMapTable<KCADRenderNode *, NSValue *> *childDeltaMap;
/// 子节点数组，未排序，下发什么顺序，这就什么顺序
/// @disscussion 只有盒子节点会使用该属性
@property (nonatomic, strong) NSMutableArray<KCADRenderNode *> *childList;
/// 按照降序排完的子节点数组
/// @disscussion 只有盒子节点会使用该属性
@property (nonatomic, strong) NSMutableArray<KCADRenderNode *> *childSortList;

/// 当前节点对于其子节点而言，是否是顶层节点
/// @discuss 若当前节点为顶层节点，则自己下面的所有子节点，只需要在本节点下拍平排放即可。
/// 例如 Scroll 节点，子节点只需要在其节点上拍平摆放即可。
@property (nonatomic, assign, readonly) BOOL isRoot;

- (instancetype)initWithRenderInfo:(RIAIDNode *)renderInfo;

#pragma mark - optional abstract api
/// 节点进行数据的绑定
/// @discussion 该节点会进行节点的属性绑定、事件绑定等操作，例如 button 节点根据属性生成内容 Render
- (void)bindData;

/// 摆放子节点，计算子节点的偏移量，子类重写需要在函数末尾调用 super
- (void)layout;

/// 开始进行绘制
/// @param decorView Root View，节点 View 最终的父视图
/// @discussion 具体流程如下：
/// 1. 坐标转换计算出绝对坐标
/// 2. 绘制背景，例如 button 组件
/// 3. 绘制节点及子节点
/// 3. 绘制前景
- (void)beganRendingWithDecorView:(UIView *)decorView;

/// 绘画背景，子类需要时，可以进行重写时，但注意要调用 super
/// @param decorView 装饰视图
- (void)drawBackGroundWithDecorView:(UIView *)decorView;

/// 绘画自身，子类需要时，可以进行重写时，但注意要调用 super
/// @param decorView 装饰视图
- (void)drawSelfWithDecorView:(UIView *)decorView;

/// 绘画前景，子类需要时，可以进行重写时，但注意要调用 super
/// @param decorView 装饰视图
/// @discussion 现阶段只有 button 需要贴一层前景，用于点击
- (void)drawForegroundWithDecorView:(UIView *)decorView;

/// 测算当前节点的宽高，可以进行重写时，但注意要调用 super
/// @param estimateWidth 父 view 给予的最大宽度边界
/// @param estimateHeight 父 view 给予的最大高度边界
/// @discussion 父节点给予的宽高为预估宽高，最终尺寸需要通过计算得出，可以是直接测算得出结果，也可能是子 view 反哺后得出的结果
- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight;

/// 事件分发
/// @param eventType 场景值，声明
/// @param keyList 对应响应 render 的 key 的集合
/// @param attributes 需要修改的属性
/// @return 是否有 render 响应了请求
- (BOOL)dispatchEvent:(NSString *)eventType
              keyList:(NSArray<NSNumber *> *)keyList
           attributes:(RIAIDAttributes *)attributes;

#pragma mark - private method && property
/// 按压态属性，bindData 时进行绑定
@property (nonatomic, strong) RIAIDAttributes *highlightAttributes;

/// Render 内部使用属性，外部不可调用。需要子类实现
/// @return 获取最终渲染的视图
/// @discussion 内部使用懒加载的方式创建内容视图
- (UIView *)getContentView;

/// 获取当前节点下子节点的偏移量
/// @param childNode 子节点
/// @return 偏移量
- (CGPoint)getChileNodeDeltaPoint:(KCADRenderNode *)childNode;

/// 传递按压态，由拥有按压态的父节点向下传递
/// @param highlightStateArray 按压态数组
- (void)deliverHighlightAttributes:(nullable NSArray<RIAIDButtonAttributes_HighlightState *> *)highlightStateArray;


/// 开始触发按压
/// @param bySelf 为 NO 时，为外界触发（现阶段只有按钮定义会外界触发），需要改变背景等属性。YES 时为自己触发，只修改自己的属性即可，
/// 比如 TextNode 需要修改 Text 的高亮属性即可
- (void)onPressStart:(BOOL)bySelf;

/// 按压结束
/// @param bySelf 是否为自己触发
/// @discussion bySelf 为 NO 时，为外界触发（现阶段只有按钮定义会外界触发），需要改变背景等属性。YES 时为自己触发，只修改自己的属性即可，
/// 比如 TextNode 需要修改 Text 的高亮属性即可
- (void)onPressEnd:(BOOL)bySelf;

@end

NS_ASSUME_NONNULL_END

