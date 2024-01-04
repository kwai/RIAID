//
//  KCADRenderButtonNode.h
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderBoxNode.h"

NS_ASSUME_NONNULL_BEGIN

/// 按钮盒子节点
@interface KCADRenderButtonNode : KCADRenderBoxNode

/// button 上的渲染内容
/// @discussion button 数据结构上会挂载一个 ADRenderNode 的数据，用来表述按钮上的内容
@property (nonatomic, strong) KCADRenderNode *contentRender;

@end

NS_ASSUME_NONNULL_END
