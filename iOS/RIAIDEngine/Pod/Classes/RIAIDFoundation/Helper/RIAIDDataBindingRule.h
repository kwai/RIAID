//
//  RIAIDDataBindingRule.h
//  RIAIDEngine
//
//  Created by simon on 2023/9/1.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// RIAID 数据绑定规则
@interface RIAIDDataBindingRule : NSObject

/// 是否需要数据绑定
/// - Parameters:
///   - holder: 原始 holder 数据
///   - dataBinding: 需要进行数据绑定的数据
+ (BOOL)needDataBindingWithHolder:(NSString *)holder dataBinding:(NSString *)dataBinding;

/// 获取完整的富文本文案
/// - Parameters:
///   - text: 原始文本
///   - holder: 原始 holder 数据
+ (NSString *)appendingRichTagWithText:(NSString *)text holder:(NSString *)holder;

@end

NS_ASSUME_NONNULL_END
