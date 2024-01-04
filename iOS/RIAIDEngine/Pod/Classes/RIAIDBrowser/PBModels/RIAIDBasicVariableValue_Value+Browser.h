//
//  RIAIDBasicVariableValue_Value+Browser.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/19.
//

#import <RIAID/Riaid.pbobjc.h>

NS_ASSUME_NONNULL_BEGIN

@interface RIAIDBasicVariableValue_Value (Browser)

/// 不论本身Value是什么类型，都转为String输出
- (NSString*)riaidStringValue;

@end

NS_ASSUME_NONNULL_END
