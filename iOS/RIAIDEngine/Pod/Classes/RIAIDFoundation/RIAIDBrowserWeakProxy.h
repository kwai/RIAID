//
//  RIAIDBrowserWeakProxy.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/19.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 用以进行消息转发（例如NSTimer防止循环引用时使用）
@interface RIAIDBrowserWeakProxy : NSProxy

/// 唯一初始化方法
/// @param target 消息真正的处理类
- (instancetype)initWithTarget:(id)target;

@end

NS_ASSUME_NONNULL_END
