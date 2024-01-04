//
//  RIAIDBGlobalVarArea.m
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/18.
//

#import "RIAIDBGlobalVarArea.h"
#import "NSNumber+RIAIDBToString.h"

@interface RIAIDBGlobalVarArea()

/// conditionMap哈希表，以conditionName->conditionValue的形式存储
@property (nonatomic, strong) NSMutableDictionary<NSString*, NSString*> *conditionMap;

/// varMap哈希表，以conditionName->conditionValue的形式存储
@property (nonatomic, strong) NSMutableDictionary<NSString*, RIAIDBasicVariable*> *varMap;
@end

@implementation RIAIDBGlobalVarArea

#pragma mark - Condition
- (void)registerConditions:(NSArray<RIAIDADConditionModel*>*)conditions {
    for (RIAIDADConditionModel *condition in conditions) {
        [self.conditionMap setObject:condition.conditionValue forKey:condition.conditionName];
    }
}

- (void)updateCondition:(RIAIDADConditionModel*)condition {
    if (condition.conditionName.length > 0 && condition.conditionValue.length > 0) {
        [self.conditionMap setObject:condition.conditionValue forKey:condition.conditionName];
    }
}

/// 注册所有变量
/// @param variables RIAIDBasicVariable数组
- (void)registerVariables:(NSArray<RIAIDBasicVariable*>*)variables {
    for (RIAIDBasicVariable *var in variables) {
        [self.varMap setObject:var forKey:@(var.key).riaidIntString];
    }
}

/// 更新变量
/// @param variable 目标变量
- (void)updateVariable:(RIAIDBasicVariable*)variable {
    RIAIDBasicVariable *current = [self.varMap objectForKey:@(variable.key).riaidIntString];
    if (current) {
        current.value.i = variable.value.i;
        current.value.s = variable.value.s;
        current.value.d = variable.value.d;
        current.value.b = variable.value.b;
        
        [self.varMap setObject:current forKey:@(variable.key).riaidIntString];
    } else {
        [self.varMap setObject:variable forKey:@(variable.key).riaidIntString];
    }
}

#pragma mark - Variable


#pragma mark - Lazy init
- (NSMutableDictionary<NSString *,NSString *> *)conditionMap {
    if (!_conditionMap) {
        _conditionMap = [NSMutableDictionary dictionary];
    }
    return _conditionMap;
}

- (NSMutableDictionary<NSString *,RIAIDBasicVariable *> *)varMap {
    if (!_varMap) {
        _varMap = [NSMutableDictionary dictionary];
    }
    return _varMap;
}
@end
