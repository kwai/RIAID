//
//  KCADRenderItemNode.h
//  KCADRender
//
//  Created by simon on 2021/11/11.
//

#import "KCADRenderNode.h"

NS_ASSUME_NONNULL_BEGIN

/// 组件节点父类
/// @discussion 参与计算，参与实际渲染。不会再挂载子节点
@interface KCADRenderItemNode : KCADRenderNode

@end

NS_ASSUME_NONNULL_END
