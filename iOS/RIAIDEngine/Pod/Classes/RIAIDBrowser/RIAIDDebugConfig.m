//
//  RIAIDDebugConfig.m
//  Aegon_iOS
//
//  Created by liweipeng on 2021/12/27.
//

#import "RIAIDDebugConfig.h"

@implementation RIAIDDebugConfig

+ (BOOL)allow:(RIAIDDebugType)type {
#ifdef DEBUG
    return [self debugConfig:type];
#else
    return NO;
#endif
}

+ (BOOL)debugConfig:(RIAIDDebugType)type {
    switch (type) {
        case RIAIDDebugTypeRenderMask:
        {
            return NO;
        }
            break;
        default:
            break;
    }
    return NO;
}

@end
