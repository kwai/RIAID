//
//  RIAIDCustomEventProtocol.h
//  RIAIDEngine
//
//  Created by simon on 2023/10/9.
//

#import <Foundation/Foundation.h>

#import "RIAIDCustomEvent.h"

NS_ASSUME_NONNULL_BEGIN

@protocol RIAIDCustomEventProtocol <NSObject>

- (void)sendCustomEvent:(RIAIDCustomEvent *)event;

@end

NS_ASSUME_NONNULL_END
