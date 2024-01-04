//
//  RIAIDBrowserVideoTimerController.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/8.
//

#import <Foundation/Foundation.h>
#import "RIAIDBrowserTimerControllerProtocol.h"
#import "RIAIDBrowserDirector.h"

NS_ASSUME_NONNULL_BEGIN

@interface RIAIDBrowserVideoTimerController : NSObject<RIAIDBrowserTimerControllerProtocol>

/// 当前Browser所持有的director
@property (nonatomic, strong) RIAIDBrowserDirector *director;

@end

NS_ASSUME_NONNULL_END
