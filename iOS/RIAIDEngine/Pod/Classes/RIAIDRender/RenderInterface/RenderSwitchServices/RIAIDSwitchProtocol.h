//
//  RIAIDSwitchProtocol.h
//  RIAIDEngine
//
//  Created by simon on 2023/10/9.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 开关获取协议
@protocol RIAIDSwitchProtocol <NSObject>

- (id)switchValueForKey:(NSString *)key;

@end

NS_ASSUME_NONNULL_END
