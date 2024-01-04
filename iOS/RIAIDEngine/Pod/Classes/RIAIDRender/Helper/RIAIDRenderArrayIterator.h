//
//  RIAIDRenderArrayIterator.h
//  KCADRender
//
//  Created by simon on 2021/12/22.
//

#import <Foundation/Foundation.h>

#import "RIAIDRenderContext.h"

@class KCADRenderNode;

NS_ASSUME_NONNULL_BEGIN

/// 数组迭代器，用于迭代 render 的子节点
/// @disscussion 内部使用栈进行数组的获取
@interface RIAIDRenderArrayIterator : NSObject

/// 通过数组初始化一个迭代器
- (instancetype)initWithArray:(nullable NSArray *)array context:(RIAIDRenderContext *)context;

/// 获取数组内的下一个元素
- (id)next;

@end

NS_ASSUME_NONNULL_END
