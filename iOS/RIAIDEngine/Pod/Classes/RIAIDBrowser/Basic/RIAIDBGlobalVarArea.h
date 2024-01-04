//
//  RIAIDBGlobalVarArea.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/18.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

@interface RIAIDBGlobalVarArea : NSObject

#pragma mark - Condition
/// conditionMap哈希表，以conditionName->conditionValue的形式存储
@property (nonatomic, strong, readonly) NSMutableDictionary<NSString*, NSString*> *conditionMap;

/// 注册所有条件
/// @param conditions RIAIDADConditionModel数组
- (void)registerConditions:(NSArray<RIAIDADConditionModel*>*)conditions;

/// 更新Condition
/// @param condition 目标condition对象
- (void)updateCondition:(RIAIDADConditionModel*)condition;

#pragma mark - Variable

/// varMap哈希表，以conditionName->conditionValue的形式存储
@property (nonatomic, strong, readonly) NSMutableDictionary<NSString*, RIAIDBasicVariable*> *varMap;

/// 注册所有变量
/// @param variables RIAIDBasicVariable数组
- (void)registerVariables:(NSArray<RIAIDBasicVariable*>*)variables;

/// 更新变量
/// @param variable 目标变量
- (void)updateVariable:(RIAIDBasicVariable*)variable;

@end

NS_ASSUME_NONNULL_END
