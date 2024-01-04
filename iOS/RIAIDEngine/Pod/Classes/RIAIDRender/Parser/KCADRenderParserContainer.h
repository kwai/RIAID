//
//  KCADRenderParserContainer.h
//  KCADRender
//
//  Created by simon on 2021/11/12.
//

#import <Foundation/Foundation.h>

#import "KCADRenderNode.h"
#import "RIAID.h"
#import "RIAIDRenderContext.h"

NS_ASSUME_NONNULL_BEGIN

/// 解析容器
/// @discussion 注册所有的解析器，并根据 render 信息选择合适的解析器进行数据节点解析
@interface KCADRenderParserContainer : NSObject

/// 解析 AdRender 信息，用于入口处解析全量 AdRender 信息
/// @param renderInfo pb 类型的 AdRender 信息
/// @param context ADRender 所需要的上下文信息
/// @return 返回 AdRender 的根节点对象
- (nullable KCADRenderNode *)parseAdRenderInfo:(RIAIDNode *)renderInfo context:(RIAIDRenderContext *)context;

/// 解析 AdRender 的子节点信息，用于 box 解析器解析子节点
/// @param childRenderInfoArray 子节点对象数组
/// @param context ADRender 所需要的上下文信息
/// @return 返回子节点对象数组
- (nullable NSArray<KCADRenderNode *> *)parseChildNodeInfo:(NSArray<RIAIDNode *> *)childRenderInfoArray
                                                   context:(RIAIDRenderContext *)context;

@end

NS_ASSUME_NONNULL_END
