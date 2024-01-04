//
//  NSNumber+RIAIDBToString.m
//  RIAIDBToString
//
//  Created by liweipeng on 2021/12/16.
//

#import "NSNumber+RIAIDBToString.h"

@implementation NSNumber (RIAIDBToString)

- (NSString*)riaidIntString {
    return [NSString stringWithFormat:@"%d", self.intValue];
}

@end
