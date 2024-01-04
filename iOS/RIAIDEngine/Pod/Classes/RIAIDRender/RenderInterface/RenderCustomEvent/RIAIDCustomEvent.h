//
//  RIAIDCustomEvent.h
//  RIAIDEngine
//
//  Created by simon on 2023/10/9.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface RIAIDCustomEvent : NSObject

/// 自定义事件的 key
@property(nonatomic, copy) NSString *key;
/// 自定义事件的value
@property(nonatomic, copy) NSString *value;

@end

NS_ASSUME_NONNULL_END
