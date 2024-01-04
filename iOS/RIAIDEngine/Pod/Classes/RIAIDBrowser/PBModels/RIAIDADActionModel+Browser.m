//
//  RIAIDADActionModel+Browser.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/20.
//

#import "RIAIDADActionModel+Browser.h"

@implementation RIAIDADActionModel (Browser)

- (RIAIDADActionType)actionType {
    if (self.hasTransition) {
        return RIAIDADActionTypeTransition;
    }
    
    if (self.hasTrack) {
        return RIAIDADActionTypeTrack;
    }
    
    if (self.hasVideo) {
        return RIAIDADActionTypeVideo;
    }
    
    if (self.hasURL) {
        return RIAIDADActionTypeURL;
    }
    
    if (self.hasConditionChange) {
        return RIAIDADActionTypeConditionChange;
    }
    
    if (self.hasVariableChange) {
        return RIAIDADActionTypeVariableChange;
    }
    
    if (self.hasCancelTimer) {
        return RIAIDADActionTypeCancelTimer;
    }
    
    if (self.hasCustom) {
        return RIAIDADActionTypeCustom;
    }
    
    if (self.hasTrigger) {
        return RIAIDADActionTypeTrigger;
    }
    
    if (self.hasConversion) {
        return RIAIDADActionTypeConversion;
    }
    
    if (self.hasStep) {
        return RIAIDADActionTypeStep;
    }
    
    if (self.hasVibrator) {
        return RIAIDADActionTypeVibrator;
    }
    
    if (self.hasCancelDeviceMotion) {
        return RIAIDADActionTypeCancelDeviceMotion;
    }
    
    return RIAIDADActionTypeUnknown;
}

@end
