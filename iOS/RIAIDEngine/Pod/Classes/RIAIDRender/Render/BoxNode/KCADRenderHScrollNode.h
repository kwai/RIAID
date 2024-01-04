//
//  KCADRenderHScrollNode.h
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderBoxNode.h"

NS_ASSUME_NONNULL_BEGIN

/// 水平滚动视图盒子节点
@interface KCADRenderHScrollNode : KCADRenderBoxNode

/// 获取水平滚动的内容大小
- (CGSize)getContainerSize;

@end

NS_ASSUME_NONNULL_END
