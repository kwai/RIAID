//
//  TimeConversion.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/20.
//

#import "RIAIDTimeConversion.h"

@implementation RIAIDTimeConversion

+ (NSTimeInterval)millisecondToSecond:(int64_t)second {
    return second/1000.f;
}

@end
