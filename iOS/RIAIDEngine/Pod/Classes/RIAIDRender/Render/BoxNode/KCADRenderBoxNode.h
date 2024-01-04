//
//  KCADRenderBoxNode.h
//  KCADRender
//
//  Created by simon on 2021/11/11.
//

#import "KCADRenderNode.h"

NS_ASSUME_NONNULL_BEGIN

/// 盒子节点父类
/// @discussion 参与计算，不参与实际渲染，会挂载子节点
@interface KCADRenderBoxNode : KCADRenderNode

/// 挂载子节点，用于挂载单个子节点
/// @param renderNode 子节点实例对象
- (void)addNode:(KCADRenderNode *)renderNode;

/// 挂载子节点，用于挂载子节点数组
/// @param renderNodeArray 子节点实例对象数组
- (void)addNodeArray:(NSArray<KCADRenderNode *> *)renderNodeArray;

/// 根据属性刷新 UI
/// @param attributes 具体的属性值
- (void)refreshPressUI:(nullable RIAIDAttributes *)attributes;

@end

NS_ASSUME_NONNULL_END
