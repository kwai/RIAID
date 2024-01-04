//
//  RIAIDADFunctionModel+Browser.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/8.
//

#import "RIAIDADFunctionModel+Browser.h"

@implementation RIAIDADFunctionModel (Browser)

/// 获取实际对应Trigger的key
- (int32_t)key {
    switch (self.functionType) {
        case RIAIDADFunctionTypeReadAttribute:
            return self.readAttribute.key;
        default:
            return 0;
    }
}

/// 获取RIAIDADFunctionType对应的Function类型
- (RIAIDADFunctionType)functionType {
    if (self.hasReadAttribute) {
        return RIAIDADFunctionTypeReadAttribute;
    }
    
    return RIAIDADFunctionTypeUnknown;
}

@end
