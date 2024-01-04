//
//  RIAIDBrowserFunctionHandler.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/8.
//

#import "RIAIDBrowserFunctionHandler.h"
#import "NSNumber+RIAIDBToString.h"
#import "RIAIDADFunctionModel+Browser.h"

#import "KCADRenderNode.h"
#import "RIAIDRenderVideoNode.h"
#import "RIAIDRVideoStateProtocol.h"
#import "RIAIDRServiceContainer.h"

@interface RIAIDBrowserFunctionHandler()

/// Function哈希表，以TriggerKey(字符串)->RIAIDADFunctionModel 的形式存储
@property (nonatomic, strong) NSMutableDictionary<NSString*, RIAIDADFunctionModel*> *functionMap;

@end

@implementation RIAIDBrowserFunctionHandler

- (void)registerFunctions:(NSArray<RIAIDADFunctionModel*> *)functions {
    for (RIAIDADFunctionModel *function in functions) {
        [self.functionMap setObject:function forKey:@(function.key).riaidIntString];
    }
}

- (BOOL)isReadAttributeFunction:(int32_t)functionKey {
    RIAIDADFunctionModel *functionModel = [self.functionMap objectForKey:@(functionKey).riaidIntString];
    return functionModel.functionType == RIAIDADFunctionTypeReadAttribute;
}

- (nullable NSString*)readAttribute:(int32_t)functionKey {
    RIAIDADFunctionModel *functionModel = [self.functionMap objectForKey:@(functionKey).riaidIntString];
    RIAIDADReadAttributeFunctionModel *readAttributeModel = functionModel.readAttribute;
    
    if (readAttributeModel) {
        int32_t viewKey = readAttributeModel.viewKey;
        KCADRender *targetRender = [self.director findRenderByViewKey:viewKey];
        KCADRenderNode *findResultRender = [targetRender findRenderNodeByKey:viewKey];
        if ([findResultRender isKindOfClass:[RIAIDRenderVideoNode class]]) {
            id<RIAIDRVideoStateProtocol> videoStateInfo = [(RIAIDRenderVideoNode *)findResultRender getVideoStateInfo];
            switch (readAttributeModel.attributeType) {
                /// 视频的当前播放位置，单位：ms
                case RIAIDAttributes_AttributeType_AttributeVideoPosition: {
                    if ([videoStateInfo respondsToSelector:@selector(getCurrentPosition)]) {
                        return @([videoStateInfo getCurrentPosition] * 1000.f).stringValue;
                    }
                }   break;
                /// 视频的总播放时长，累加，单位：ms
                case RIAIDAttributes_AttributeType_AttributeVideoTotalDuration: {
                    if ([videoStateInfo respondsToSelector:@selector(getTotalDuration)]) {
                        return @([videoStateInfo getTotalDuration] * 1000.f).stringValue;
                    }
                }   break;
                case RIAIDAttributes_AttributeType_GPBUnrecognizedEnumeratorValue:
                case RIAIDAttributes_AttributeType_AttributeUnknown:
                    break;
            }
        }
    }
    
    return nil;
}

- (NSMutableDictionary<NSString *,RIAIDADFunctionModel *> *)functionMap {
    if (nil == _functionMap) {
        _functionMap = [NSMutableDictionary dictionary];
    }
    return _functionMap;
}

@end
