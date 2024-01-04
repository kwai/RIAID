//
//  RIAIDDebugConfig.h
//  Aegon_iOS
//
//  Created by liweipeng on 2021/12/27.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM (NSUInteger, RIAIDDebugType) {
    RIAIDDebugTypeUnknown,
    /// Render绘制View上，添加信息蒙层
    RIAIDDebugTypeRenderMask
};

/// Debug功能配置类
@interface RIAIDDebugConfig : NSObject

+ (BOOL)allow:(RIAIDDebugType)type;

@end

NS_ASSUME_NONNULL_END
