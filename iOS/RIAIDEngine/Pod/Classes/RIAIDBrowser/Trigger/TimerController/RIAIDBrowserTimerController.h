//
//  RIAIDBrowserTimerController.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/18.
//

#import <Foundation/Foundation.h>
#import "RIAIDBrowserTimerControllerProtocol.h"

NS_ASSUME_NONNULL_BEGIN

/// Timer-Controller，对于Timer-Trigger具体操作的实例化
/// @discussion: 一个Timer-Trigger，同一时间，最多只有一个Timer-Controller
@interface RIAIDBrowserTimerController : NSObject<RIAIDBrowserTimerControllerProtocol>
@end

NS_ASSUME_NONNULL_END
