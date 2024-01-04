//
//  RIAIDBBindingService.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import "RIAIDBDataBindingService.h"
#import "RIAIDBasicVariableValue_Value+Browser.h"

@interface RIAIDBDataBindingService()

/// 外部传入的DataBinding服务
@property (nonatomic, strong) id<RIAIDRDataBindingService> externalService;
@end

@implementation RIAIDBDataBindingService

- (NSString *)parseHolderData:(NSString *)dataHolder {
    /// 匹配出需要替换的字符串
    NSArray *arr = [self getStringArrayWithRegularExpression:@"(\\$\\{[^(\\{|\\})]{1,}\\})" checkString:dataHolder];
    
    for (NSString *holder in arr) {
        NSString *targetStr = [holder substringWithRange:(NSRange){2, holder.length - 3}];
        /// 尝试进行函数替换
        if ([self.functionHandler isReadAttributeFunction:targetStr.intValue]) {
            NSString *result = [self.functionHandler readAttribute:targetStr.intValue];
            if (nil != result) {
                dataHolder = [dataHolder stringByReplacingOccurrencesOfString:holder withString:result];
            }
        }
        /// 尝试进行变量替换
        if ([self.globalVarArea.varMap.allKeys containsObject:targetStr]) {
            RIAIDBasicVariable *var = [self.globalVarArea.varMap objectForKey:targetStr];
            NSString *result = [var.value riaidStringValue];
            if (result) {
                dataHolder = [dataHolder stringByReplacingOccurrencesOfString:holder withString:result];
            }
        }
    }
    
    /// 尝试外部处理
    NSString *externalResult = [self.externalService parseHolderData:dataHolder];
    if (nil != externalResult) {
        return externalResult;
    }
    
    return dataHolder;
}

- (void)addExternalService:(NSObject *)serviceInstance {
    if ([serviceInstance conformsToProtocol:@protocol(RIAIDRDataBindingService)]) {
        self.externalService = (id<RIAIDRDataBindingService>)serviceInstance;
    }
}

- (NSArray<NSString *> *)getStringArrayWithRegularExpression:(NSString *)regex checkString:(NSString *)checkString {
    if (!checkString) {
        return nil;
    }
    NSError *error = NULL;
    NSRegularExpression *regularExpression = [NSRegularExpression regularExpressionWithPattern:regex options:NSRegularExpressionCaseInsensitive | NSRegularExpressionDotMatchesLineSeparators error:&error];

    NSArray *resultArray = [regularExpression matchesInString:checkString options:NSMatchingReportProgress range:NSMakeRange(0, [checkString length])];
    NSMutableArray *arr = [[NSMutableArray alloc] initWithCapacity:0];

    for (NSTextCheckingResult *result in resultArray) {
        for (NSInteger i = 1; i < [result numberOfRanges]; i++) {
            NSString *matchString;

            NSRange range = [result rangeAtIndex:i];

            if (range.location != NSNotFound) {
                matchString = [checkString substringWithRange:[result rangeAtIndex:i]];
            } else {
                matchString = @"";
            }
            [arr addObject:matchString];
        }
    }

    return [arr copy];
}
@end
