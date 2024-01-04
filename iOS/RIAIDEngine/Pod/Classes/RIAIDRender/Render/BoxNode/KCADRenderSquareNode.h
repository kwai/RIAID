//
//  KCADRenderSquareNode.h
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderBoxNode.h"

NS_ASSUME_NONNULL_BEGIN

/// 正方形盒子节点
/// 只针对宽高相等的组件，内部只允许存在一个子组件
@interface KCADRenderSquareNode : KCADRenderBoxNode

@end

NS_ASSUME_NONNULL_END
