//
//  RIAIDRConsumeActionService.h
//  KCADRender
//
//  Created by simon on 2021/12/19.
//

#import <Foundation/Foundation.h>

#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, RIAIDConsumeActionType) {
    RIAIDConsumeActionTypeClick,
    RIAIDConsumeActionTypeLongPress,
    RIAIDConsumeActionTypeDoubleClick,
    RIAIDConsumeActionTypeVideoImpress,
    RIAIDConsumeActionTypeVideoFinish,
    RIAIDConsumeActionTypeVideoPause,
    RIAIDConsumeActionTypeVideoStart,
    RIAIDConsumeActionTypeVideoResume,
};

/// 事件行为消费接口，需要调用方实现
@protocol RIAIDRConsumeActionService <NSObject>

- (void)consumeRenderAction:(RIAIDConsumeActionType)actionType
                  responder:(nullable RIAIDResponder *)responder;

@end

NS_ASSUME_NONNULL_END
