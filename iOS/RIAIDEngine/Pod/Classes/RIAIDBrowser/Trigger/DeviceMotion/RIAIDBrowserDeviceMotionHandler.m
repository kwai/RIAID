//
//  RIAIDBrowserDeviceMotionHandler.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/6/9.
//

#import "RIAIDBrowserDeviceMotionHandler.h"
#import <CoreMotion/CoreMotion.h>
#import "RIAIDOperatorMatcher.h"

@interface RIAIDBrowserDeviceMotionHandler ()

@property (nonatomic, strong) NSMutableDictionary<NSNumber*, RIAIDADDeviceMotionTriggerModel*> *deviceMotionMap;

@property (nonatomic, strong) CMMotionManager *motionManager;

@end

@implementation RIAIDBrowserDeviceMotionHandler

- (void)addDeviceMotion:(RIAIDADDeviceMotionTriggerModel*)deviceMotion {
    [self.deviceMotionMap setObject:deviceMotion forKey:@(deviceMotion.key)];
    [self checkDeviceMotion];
}

- (void)cancelDeviceMotion:(int32_t)key {
    [self.deviceMotionMap removeObjectForKey:@(key)];
    [self checkDeviceMotion];
}

#pragma mark - Private
- (void)checkDeviceMotion {
    self.deviceMotionMap.count > 0 ? [self startUpdateIfNeed] : [self stopUpdateIfNeed];
}

- (void)stopUpdateIfNeed {
    if (_motionManager && _motionManager.isDeviceMotionActive) {
        [_motionManager stopDeviceMotionUpdates];
    }
}

- (void)startUpdateIfNeed {
    if (!_motionManager) {
        _motionManager = [CMMotionManager new];
        [_motionManager setDeviceMotionUpdateInterval:0.1];
    }
    
    if (_motionManager.isDeviceMotionAvailable && !_motionManager.isDeviceMotionActive) {
        __weak typeof(self) weakSelf = self;
        [_motionManager startDeviceMotionUpdatesToQueue:[NSOperationQueue mainQueue] withHandler:^(CMDeviceMotion * _Nullable motion, NSError * _Nullable error) {
            __strong typeof(self) strongSelf = weakSelf;
            [strongSelf checkTriggers:motion];
        }];
    }
}

- (void)checkTriggers:(CMDeviceMotion*)deviceMotion {
    for (RIAIDADDeviceMotionTriggerModel *motionTrigger in self.deviceMotionMap.allValues) {
        if ([self meetCondition:motionTrigger.condition deviceMotion:deviceMotion motionType:motionTrigger.motionType]) {
            // 执行Actions
            if (self.delegate && [self.delegate respondsToSelector:@selector(deviceMotionHandler:execute:)]) {
                [self.delegate deviceMotionHandler:self execute:motionTrigger.actionsArray];
            }
        }
    }
}

- (void)dealloc {
    [self.motionManager stopDeviceMotionUpdates];
}

#pragma mark - Condition
/// 条件判断方法
- (BOOL)meetCondition:(RIAIDDeviceAxisConditionModel*)conditionLogic
         deviceMotion:(CMDeviceMotion*)deviceMotion
           motionType:(RIAIDADDeviceMotionTriggerModel_DeviceMotionType)motionType {
    switch (conditionLogic.operator_p) {
        case RIAIDLogicOperator_LogicOperatorOr:
            return [self orUnits:conditionLogic.unitsArray deviceMotion:deviceMotion motionType:motionType];
        case RIAIDLogicOperator_LogicOperatorAnd:
            return [self andUnits:conditionLogic.unitsArray deviceMotion:deviceMotion motionType:motionType];
        case RIAIDLogicOperator_LogicOperatorNot:
            /// logic为非的含义：第一步：先将所有unit结果做与运算。第二步：得到的结果取反。
            return ![self andUnits:conditionLogic.unitsArray deviceMotion:deviceMotion motionType:motionType];
        default:
            break;
    }

    return NO;
}


/// 与运算结果
- (BOOL)andUnits:(NSArray<RIAIDDeviceAxisUnitModel*>*)unitsArray
    deviceMotion:(CMDeviceMotion*)deviceMotion
      motionType:(RIAIDADDeviceMotionTriggerModel_DeviceMotionType)motionType {
    __block BOOL result;
    [unitsArray enumerateObjectsUsingBlock:^(RIAIDDeviceAxisUnitModel * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (idx == 0) {
            result = [self meet:obj deviceMotion:deviceMotion motionType:motionType];
        } else {
            result = result && [self meet:obj deviceMotion:deviceMotion motionType:motionType];
        }
    }];
    return result;
}

/// 或运算结果
- (BOOL)orUnits:(NSArray<RIAIDDeviceAxisUnitModel*>*)unitsArray
   deviceMotion:(CMDeviceMotion*)deviceMotion
     motionType:(RIAIDADDeviceMotionTriggerModel_DeviceMotionType)motionType {
    __block BOOL result;
    [unitsArray enumerateObjectsUsingBlock:^(RIAIDDeviceAxisUnitModel * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (idx == 0) {
            result = [self meet:obj deviceMotion:deviceMotion motionType:motionType];
        } else {
            result = result || [self meet:obj deviceMotion:deviceMotion motionType:motionType];
        }
    }];
    return result;
}


- (BOOL)meet:(RIAIDDeviceAxisUnitModel*)axisUnitModel
deviceMotion:(CMDeviceMotion*)deviceMotion
  motionType:(RIAIDADDeviceMotionTriggerModel_DeviceMotionType)motionType {
    double value = 0;
    
    switch (motionType) {
        case RIAIDADDeviceMotionTriggerModel_DeviceMotionType_DeviceMotionTypeUseracceleration: {
            /// 设备加速度
            switch (axisUnitModel.type) {
                case RIAIDDeviceAxisType_DeviceAxisTypeX: {
                    value = deviceMotion.userAcceleration.x;
                } break;
                case RIAIDDeviceAxisType_DeviceAxisTypeY: {
                    value = deviceMotion.userAcceleration.y;
                } break;
                case RIAIDDeviceAxisType_DeviceAxisTypeZ: {
                    value = deviceMotion.userAcceleration.z;
                } break;
                default: {
                    return NO;
                } break;
            }
        } break;
        case RIAIDADDeviceMotionTriggerModel_DeviceMotionType_DeviceMotionTypeRotationrate: {
            /// 旋转加速度
            switch (axisUnitModel.type) {
                case RIAIDDeviceAxisType_DeviceAxisTypeX: {
                    value = deviceMotion.rotationRate.x;
                } break;
                case RIAIDDeviceAxisType_DeviceAxisTypeY: {
                    value = deviceMotion.rotationRate.y;
                } break;
                case RIAIDDeviceAxisType_DeviceAxisTypeZ: {
                    value = deviceMotion.rotationRate.z;
                } break;
                default: {
                    return NO;
                } break;
            }
        } break;
        default: {
            return NO;
        } break;
    }
    
    double threshold = axisUnitModel.threshold;
    
    NSComparisonResult comparision;
    if (value > threshold) {
        comparision = NSOrderedDescending;
    } else  if (value < threshold) {
        comparision = NSOrderedAscending;
    } else {
        comparision = NSOrderedSame;
    }
    return [RIAIDOperatorMatcher isMatch:comparision operator:axisUnitModel.compare];;
}

#pragma mark - Lazy init
- (NSMutableDictionary<NSNumber *,RIAIDADDeviceMotionTriggerModel *> *)deviceMotionMap {
    if (!_deviceMotionMap) {
        _deviceMotionMap = [NSMutableDictionary dictionary];
    }
    return _deviceMotionMap;
}

@end
