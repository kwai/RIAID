//
//  KCADRenderAbsoluteNode.h
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderBoxNode.h"

NS_ASSUME_NONNULL_BEGIN

/// 绝对布局盒子节点对象，负责绝对布局计算。不参与最终的渲染
/// @discussion 用于处理绝对布局，内部子节点叠加排放
@interface KCADRenderAbsoluteNode : KCADRenderBoxNode

@end

NS_ASSUME_NONNULL_END
