//
//  RIAIDADTriggerModel+Browser.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/18.
//

#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// Trigger类型枚举
typedef NS_ENUM(NSUInteger, RIAIDADTriggerType) {
    /// unknown
    RIAIDADTriggerTypeUnknown,
    /// 延时操作触发器
    RIAIDADTriggerTypeTimeout,
    /// 定时重复触发器
    RIAIDADTriggerTypeHeartbeat,
    /// 普通触发器
    RIAIDADTriggerTypeGeneral,
    /// 条件判断触发器
    RIAIDADTriggerTypeCondition,
    /// TimeSource为Video的延时操作触发器
    RIAIDADTriggerTypeVideoTimeout,
    /// 设备运动触发器
    RIAIDADTriggerTypeDeviceMotion
};

/// 给RIAIDADTriggerModel添加一些便利方法
@interface RIAIDADTriggerModel (Browser)

/// 获取实际对应Trigger的key
- (int32_t)key;

/// 获取RIAIDADTriggerModel对应的Trigger类型
- (RIAIDADTriggerType)triggerType;

@end

NS_ASSUME_NONNULL_END
