//
//  RIAIDBrowserConditionChecker.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/18.
//

#import "RIAIDBrowserConditionChecker.h"
#import "RIAIDBasicVariableValue_Value+Browser.h"
#import "RIAIDOperatorMatcher.h"

@implementation RIAIDBrowserConditionChecker

/// 抛出第一个满足条件的Actions
+ (nullable NSArray<RIAIDADActionModel*>*)getActions:(RIAIDADConditionTriggerModel*)conditionTriggerModel
                                        conditionMap:(NSMutableDictionary<NSString*, NSString*>*)conditionMap
                                              varMap:(NSMutableDictionary<NSString*, RIAIDBasicVariable*>*)varMap {
    for (RIAIDADConditionLogicModel *conditionLogicModel in conditionTriggerModel.logicsArray) {
        if ([RIAIDBrowserConditionChecker meetCondition:conditionLogicModel conditionMap:conditionMap varMap:varMap]) {
            return conditionLogicModel.actionsArray;
        }
    }
    return nil;
}

/// 条件判断方法
+ (BOOL)meetCondition:(RIAIDADConditionLogicModel*)conditionLogic
         conditionMap:(NSMutableDictionary<NSString*, NSString*>*)conditionMap
               varMap:(NSMutableDictionary<NSString*, RIAIDBasicVariable*>*)varMap {
    switch (conditionLogic.operator_p) {
        case RIAIDLogicOperator_LogicOperatorOr:
            return [RIAIDBrowserConditionChecker orUnits:conditionLogic.unitsArray conditionMap:conditionMap varMap:varMap];
        case RIAIDLogicOperator_LogicOperatorAnd:
            return [RIAIDBrowserConditionChecker andUnits:conditionLogic.unitsArray conditionMap:conditionMap varMap:varMap];
        case RIAIDLogicOperator_LogicOperatorNot:
            /// logic为非的含义：第一步：先将所有unit结果做与运算。第二步：得到的结果取反。
            return ![RIAIDBrowserConditionChecker andUnits:conditionLogic.unitsArray conditionMap:conditionMap varMap:varMap];
        default:
            break;
    }
    
    return NO;
}


/// RIAIDADLogicUnitModel数组---与运算结果
+ (BOOL)andUnits:(NSArray<RIAIDADLogicUnitModel*>*)unitsArray
    conditionMap:(NSMutableDictionary<NSString*, NSString*>*)conditionMap
          varMap:(NSMutableDictionary<NSString*, RIAIDBasicVariable*>*)varMap {
    __block BOOL result;
    [unitsArray enumerateObjectsUsingBlock:^(RIAIDADLogicUnitModel * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (idx == 0) {
            result = [RIAIDBrowserConditionChecker isMeet:obj conditionMap:conditionMap varMap:varMap];
        } else {
            result = result && [RIAIDBrowserConditionChecker isMeet:obj conditionMap:conditionMap varMap:varMap];
        }
    }];
    return result;
}

/// RIAIDADLogicUnitModel数组---或运算结果
+ (BOOL)orUnits:(NSArray<RIAIDADLogicUnitModel*>*)unitsArray
   conditionMap:(NSMutableDictionary<NSString*, NSString*>*)conditionMap
         varMap:(NSMutableDictionary<NSString*, RIAIDBasicVariable*>*)varMap {
    __block BOOL result;
    [unitsArray enumerateObjectsUsingBlock:^(RIAIDADLogicUnitModel * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (idx == 0) {
            result = [RIAIDBrowserConditionChecker isMeet:obj conditionMap:conditionMap varMap:varMap];
        } else {
            result = result || [RIAIDBrowserConditionChecker isMeet:obj conditionMap:conditionMap varMap:varMap];
        }
    }];
    return result;
}

#pragma mark - Unit Check
+ (BOOL)isMeet:(RIAIDADLogicUnitModel*)logicUnit
  conditionMap:(NSMutableDictionary<NSString*, NSString*>*)conditionMap
        varMap:(NSMutableDictionary<NSString*, RIAIDBasicVariable*>*)varMap {
    NSString *currentValue = nil;
    NSString *targetValue = nil;
    
    if (logicUnit.condition.conditionName.length > 0) {
        /// 条件逻辑判断
        currentValue = [conditionMap objectForKey:logicUnit.condition.conditionName];
        targetValue = logicUnit.condition.conditionValue;
    } else {
        /// 变量逻辑判断
        currentValue = [[varMap objectForKey:@(logicUnit.variable.key).stringValue].value riaidStringValue];
        targetValue = [logicUnit.variable.value riaidStringValue];
    }
    
    NSComparisonResult comparision = [currentValue compare:targetValue];
    return [RIAIDOperatorMatcher isMatch:comparision operator:logicUnit.compare];
}

@end
