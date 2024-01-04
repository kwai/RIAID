//
//  RIAIDADFunctionModel+Browser.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/8.
//

#import <RIAID/Riaid.pbobjc.h>

NS_ASSUME_NONNULL_BEGIN

/// Function类型枚举
typedef NS_ENUM(NSUInteger, RIAIDADFunctionType) {
    /// unknown
    RIAIDADFunctionTypeUnknown,
    /// 读取Node属性
    RIAIDADFunctionTypeReadAttribute
};

/// 给RIAIDADFunctionModel添加一些便利方法
@interface RIAIDADFunctionModel (Browser)

/// 获取实际对应Trigger的key
- (int32_t)key;

/// 获取RIAIDADFunctionType对应的Function类型
- (RIAIDADFunctionType)functionType;

@end

NS_ASSUME_NONNULL_END
