//
//  KCADRenderParser.h
//  KCADRender
//
//  Created by simon on 2021/11/10.
//

#import <Foundation/Foundation.h>

#import "KCADRenderNode.h"
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// 渲染树节点解析父类，定义一系列抽象接口
/// @discussion 节点解析的主要作用是通过下发的数据，子类生成对应的 render，并将其添加到最终的 render tree 中
@interface KCADRenderParser : NSObject

#pragma mark - 抽象父类无默认实现，子类需要重写的方法
/// 用于解析的 key，
- (NSString *)getParseKey;

/// 创建具体的 render
/// @param renderInfo render 的信息
- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo;

#pragma mark - 抽象父类有默认实现的方法
/// 当前节点数据是否可以进行解析
/// @param parseKey 当前 render 节点的类型
- (BOOL)canParseWithKey:(NSString *)parseKey;

/// 利用多态特性，解析对应节点类型的 render 数据
/// @param renderInfo render 节点数据
/// /// @param delegate 节点代理
- (KCADRenderNode *)parseWithRenderInfo:(RIAIDNode *)renderInfo
                           nodeDelegate:(id<KCADRenderNodeDelegate>)delegate
                                context:(RIAIDRenderContext *)context;


@end

NS_ASSUME_NONNULL_END
