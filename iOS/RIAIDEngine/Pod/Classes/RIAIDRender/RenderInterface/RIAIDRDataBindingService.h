//
//  RIAIDRDataBindingService.h
//  KCADRender
//
//  Created by simon on 2021/12/19.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 匹配服务端下发的 key，需要调用方实现
/// @discussion 服务端可以直接下发一个 key，外部实现通过 key 找到对应 url
@protocol RIAIDRDataBindingService <NSObject>

/// 解析占位符，获取占位符对应的字符串资源
/// @param dataHolder 占位符
- (NSString *)parseHolderData:(NSString *)dataHolder;

@end

NS_ASSUME_NONNULL_END
