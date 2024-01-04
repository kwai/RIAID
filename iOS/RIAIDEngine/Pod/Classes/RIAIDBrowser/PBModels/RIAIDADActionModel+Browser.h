//
//  RIAIDADActionModel+Browser.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/20.
//

#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// Action类型枚举
typedef NS_ENUM(NSUInteger, RIAIDADActionType) {
    /// unknown
    RIAIDADActionTypeUnknown,
    /// 转场
    RIAIDADActionTypeTransition,
    /// 埋点
    RIAIDADActionTypeTrack,
    /// 广告播放器操作
    RIAIDADActionTypeVideo,
    /// URL操作
    RIAIDADActionTypeURL,
    /// 条件变化
    RIAIDADActionTypeConditionChange,
    /// 变量变化
    RIAIDADActionTypeVariableChange,
    /// 取消Timer Controller
    RIAIDADActionTypeCancelTimer,
    /// 自定义操作
    RIAIDADActionTypeCustom,
    /// Trigger操作
    RIAIDADActionTypeTrigger,
    /// 转化操作
    RIAIDADActionTypeConversion,
    /// 分步执行操作
    RIAIDADActionTypeStep,
    /// 设备震动
    RIAIDADActionTypeVibrator,
    /// 取消设备运动监听
    RIAIDADActionTypeCancelDeviceMotion
};

/// 给RIAIDADActionModel添加一些便利方法
@interface RIAIDADActionModel (Browser)

/// 获取RIAIDADActionModel对应的action类型
- (RIAIDADActionType)actionType;

@end

NS_ASSUME_NONNULL_END
