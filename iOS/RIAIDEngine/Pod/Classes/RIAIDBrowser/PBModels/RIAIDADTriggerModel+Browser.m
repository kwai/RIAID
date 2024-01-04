//
//  RIAIDADTriggerModel+Browser.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/18.
//

#import "RIAIDADTriggerModel+Browser.h"

@implementation RIAIDADTriggerModel (Browser)

- (int32_t)key {
    switch (self.triggerType) {
        case RIAIDADTriggerTypeTimeout:
            return self.timeout.key;
        case RIAIDADTriggerTypeHeartbeat:
            return self.heartbeat.key;
        case RIAIDADTriggerTypeGeneral:
            return self.general.key;
        case RIAIDADTriggerTypeCondition:
            return self.condition.key;
        case RIAIDADTriggerTypeVideoTimeout:
            return self.videoDuration.key;
        case RIAIDADTriggerTypeDeviceMotion:
            return self.deviceMotion.key;
        default:
            return -1;
    }
}

- (RIAIDADTriggerType)triggerType {
    if (self.hasTimeout) {
        return RIAIDADTriggerTypeTimeout;
    }
    
    if (self.hasHeartbeat) {
        return RIAIDADTriggerTypeHeartbeat;
    }
    
    if (self.hasGeneral) {
        return RIAIDADTriggerTypeGeneral;
    }
    
    if (self.hasCondition) {
        return RIAIDADTriggerTypeCondition;
    }
    
    if (self.hasVideoDuration) {
        return RIAIDADTriggerTypeVideoTimeout;
    }
    
    if (self.hasDeviceMotion) {
        return RIAIDADTriggerTypeDeviceMotion;
    }
    
    return RIAIDADTriggerTypeUnknown;
}

@end
