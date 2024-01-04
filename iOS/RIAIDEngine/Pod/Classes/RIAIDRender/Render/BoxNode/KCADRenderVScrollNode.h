//
//  KCADRenderVScrollNode.h
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderBoxNode.h"

NS_ASSUME_NONNULL_BEGIN

/// 横向滚动尺寸
@interface KCADRenderVScrollNode : KCADRenderBoxNode

/// 获取垂直滚动节点的内容大小
- (CGSize)getContainerSize;

@end

NS_ASSUME_NONNULL_END
