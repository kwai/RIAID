//
//  RIAIDOperatorTool.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/6/9.
//

#import "RIAIDOperatorMatcher.h"

@implementation RIAIDOperatorMatcher

+ (BOOL)isMatch:(NSComparisonResult)comparision operator:(RIAIDCompareOperator)operator {
    switch (operator) {
        case RIAIDCompareOperator_CompareOperatorEqual: {
            if (comparision == NSOrderedSame) {
                return YES;
            }
        } break;
        case RIAIDCompareOperator_CompareOperatorNotEqual: {
            if (comparision != NSOrderedSame) {
                return YES;
            }
        } break;
        case RIAIDCompareOperator_CompareOperatorLessThan: {
            if (comparision == NSOrderedAscending) {
                return YES;
            }
        } break;
        case RIAIDCompareOperator_CompareOperatorGreaterThan: {
            if (comparision == NSOrderedDescending) {
                return YES;
            }
        } break;
        case RIAIDCompareOperator_CompareOperatorLessThanOrEqual: {
            if (comparision == NSOrderedSame || comparision == NSOrderedAscending) {
                return YES;
            }
        } break;
        case RIAIDCompareOperator_CompareOperatorGreaterThanOrEqual: {
            if (comparision == NSOrderedSame || comparision == NSOrderedDescending) {
                return YES;
            }
        } break;
        default:
            break;
    }
    return NO;
}

@end
