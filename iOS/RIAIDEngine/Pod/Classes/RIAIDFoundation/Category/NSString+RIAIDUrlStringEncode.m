//
//  NSString+RIAIDUrlStringEncode.m
//  KCADRender
//
//  Created by simon on 2022/1/12.
//

#import "NSString+RIAIDUrlStringEncode.h"

@implementation NSString (RIAIDUrlStringEncode)

- (NSString *)urlStringEncode {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    NSString *encodedString = (NSString*)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes(kCFAllocatorDefault, (CFStringRef)self, (CFStringRef)@"!$&'()*+,-./:;=?@_~%#[]", NULL, kCFStringEncodingUTF8));
#pragma clang diagnostic pop
    return encodedString;
}

@end
