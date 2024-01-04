//
//  RIAIDDataBindingRule.m
//  RIAIDEngine
//
//  Created by simon on 2023/9/1.
//

#import "RIAIDDataBindingRule.h"

@implementation RIAIDDataBindingRule

NSString * const kRIAIDServiceBindingKey = @"?json_string}";

+ (BOOL)needDataBindingWithHolder:(NSString *)holder dataBinding:(NSString *)dataBinding {
    if ([holder isEqualToString:dataBinding]) {
        return YES;
    }
    // 服务的模板引擎，需要判断 ?json_string
    NSString *textData = holder.copy;
    textData = [textData stringByReplacingOccurrencesOfString:@"${" withString:@""];
    textData = [textData stringByReplacingOccurrencesOfString:@"}" withString:@""];
    textData = [textData stringByReplacingOccurrencesOfString:@"?json_string" withString:@""];
    textData = [textData stringByReplacingOccurrencesOfString:[self _getRichTextTag:holder] withString:@""];
    return [textData isEqualToString:dataBinding];
}

+ (NSString *)appendingRichTagWithText:(NSString *)text holder:(NSString *)holder {
    NSString *richTextTag = [self _getRichTextTag:holder];
    if (nil != richTextTag) {
        return [NSString stringWithFormat:@"%@%@", text, richTextTag];
    }
    return text;
}

// 获取当前 holder 中富文本的标记
+ (nullable NSString *)_getRichTextTag:(NSString *)holder {
    NSString *richTextTag = nil;
    NSArray *textArray = [holder componentsSeparatedByString:kRIAIDServiceBindingKey];
    if (textArray.count > 0) {
        richTextTag = textArray.lastObject;
    }
    return richTextTag;
}

@end
