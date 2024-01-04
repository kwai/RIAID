//
//  RIAIDBrowserWeakProxy.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/19.
//

#import "RIAIDBrowserWeakProxy.h"

@interface RIAIDBrowserWeakProxy()

@property (nonatomic, weak) id target;

@end

@implementation RIAIDBrowserWeakProxy

- (instancetype)initWithTarget:(id)target {
    _target = target;
    return self;
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)sel {
    NSMethodSignature *signature = nil;
    if ([self.target respondsToSelector:sel]) {
        signature = [self.target methodSignatureForSelector:sel];
    } else {
        // 动态造一个 void object selector arg 函数签名。
        // 目的是返回有效signature，不要因为找不到而crash
        signature = [NSMethodSignature signatureWithObjCTypes:"v@:@"];
    }
    return signature;
}

- (void)forwardInvocation:(NSInvocation *)invocation {
    if ([self.target respondsToSelector:invocation.selector]) {
        [invocation invokeWithTarget:self.target];
    }
}

@end
