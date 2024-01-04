//
//  RIAIDBrowserConditionChecker.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/18.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// 条件判断工具类
@interface RIAIDBrowserConditionChecker : NSObject

/// 处理条件触发器相关的判断
/// @param conditionTriggerModel 条件触发器模型
/// @param conditionMap 当前的条件数据
/// @param varMap 当前的变量数据
/// @return  条件触发器在当前条件下，需要进行的操作
+ (nullable NSArray<RIAIDADActionModel*>*)getActions:(RIAIDADConditionTriggerModel*)conditionTriggerModel
                                        conditionMap:(NSMutableDictionary<NSString*, NSString*>*)conditionMap
                                              varMap:(NSMutableDictionary<NSString*, RIAIDBasicVariable*>*)varMap;


@end

NS_ASSUME_NONNULL_END
