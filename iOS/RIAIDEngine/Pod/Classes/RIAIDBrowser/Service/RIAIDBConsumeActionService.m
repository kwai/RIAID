//
//  RIAIDBConsumeActionService.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import "RIAIDBConsumeActionService.h"

@implementation RIAIDBConsumeActionService

- (void)consumeRenderAction:(RIAIDConsumeActionType)actionType
                  responder:(nullable RIAIDResponder *)responder {
    [responder.triggerKeysArray enumerateValuesWithBlock:^(int32_t value, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([self.triggerHandler respondsToSelector:@selector(handleTrigger:)]) {
            [self.triggerHandler handleTrigger:value];
        }
    }];
}

@end
