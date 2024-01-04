//
//  RIAIDRHighlightAttributesGenerator.h
//  KCADRender
//
//  Created by simon on 2022/1/3.
//

#import <Foundation/Foundation.h>

#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// 高亮状态属性生成装置
@interface RIAIDRHighlightAttributesGenerator : NSObject

/// diff 属性和原始属性作比较，生成一个新的包含变化的新属性
/// @param originAttributes 原始属性
/// @param diffAttributes 有改变的属性
+ (nullable RIAIDAttributes *)getNewAttributesWithOrigin:(nullable RIAIDAttributes *)originAttributes
                                          diffAttributes:(nullable RIAIDAttributes *)diffAttributes;

/// 文本 diff 属性和原始文本属性作比较，生成一个新的包含变化的新文本属性
/// @param originText 原始文本属性
/// @param diffText 有改变的文本属性
+ (nullable RIAIDTextAttributes *)getNewTextAttributes:(nullable RIAIDTextAttributes *)originText
                                        diffAttributes:(nullable RIAIDTextAttributes *)diffText;

@end

NS_ASSUME_NONNULL_END
