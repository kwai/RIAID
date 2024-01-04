//
//  UIColor+KCADRenderHex.m
//  KCADRender
//
//  Created by simon on 2021/11/30.
//

#import "UIColor+KCADRenderHex.h"

@implementation UIColor (KCADRenderHex)

typedef NS_ENUM(NSUInteger, RIAIDColorHexType) {
    RIAIDColorHexTypeRGB       = 3,
    RIAIDColorHexTypeARGB      = 4,
    RIAIDColorHexTypeRRGGBB    = 6,
    RIAIDColorHexTypeAARRGGBB  = 8,
};

NSString * const kRIAIDHexLengthKey = @"length";

+ (CGFloat)riaid_getAlphaValueWithHexString:(NSString *)hexString {
    NSString *cString = [[hexString stringByTrimmingCharactersInSet:[NSCharacterSet
                                                                     whitespaceAndNewlineCharacterSet]]
                         uppercaseString];
    if ([cString hasPrefix:@"0X"]) {
        cString = [cString substringFromIndex:2];
    }
    if ([cString hasPrefix:@"#"]) {
        cString = [cString substringFromIndex:1];
    }
    CGFloat alpha = 1.f;
    switch (cString.length) {
        case 4: {
            // #ARGB
            alpha = [self _colorTransFormWithString:cString start:0 length:1];
        } break;
        case 8: {
            // #AARRGGBB
            alpha = [self _colorTransFormWithString:cString start:0 length:2];
        } break;
    }
    return alpha;
}

+ (UIColor *)riaid_colorWithHexString:(NSString *)hexString {
    CGFloat alpha = [self riaid_getColorType:RIAIDColorTypeA fromHexString:hexString];
    CGFloat red = [self riaid_getColorType:RIAIDColorTypeR fromHexString:hexString];
    CGFloat green = [self riaid_getColorType:RIAIDColorTypeG fromHexString:hexString];
    CGFloat blue = [self riaid_getColorType:RIAIDColorTypeB fromHexString:hexString];
    return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
}

+ (CGFloat)riaid_getColorType:(RIAIDColorType)colorType fromHexString:(NSString *)hexString {
    NSString *cString = [[hexString stringByTrimmingCharactersInSet:[NSCharacterSet
                                                                     whitespaceAndNewlineCharacterSet]]
                         uppercaseString];
    if ([cString hasPrefix:@"0X"]) {
        cString = [cString substringFromIndex:2];
    }
    if ([cString hasPrefix:@"#"]) {
        cString = [cString substringFromIndex:1];
    }
    NSDictionary *paramMap = @{
        @(RIAIDColorHexTypeRGB): @{
            kRIAIDHexLengthKey: @(1),
            @(RIAIDColorTypeR): @(0),
            @(RIAIDColorTypeG): @(1),
            @(RIAIDColorTypeB): @(2),
        },
        @(RIAIDColorHexTypeARGB): @{
            kRIAIDHexLengthKey: @(1),
            @(RIAIDColorTypeA): @(0),
            @(RIAIDColorTypeR): @(1),
            @(RIAIDColorTypeG): @(2),
            @(RIAIDColorTypeB): @(3),
        },
        @(RIAIDColorHexTypeRRGGBB): @{
            kRIAIDHexLengthKey: @(2),
            @(RIAIDColorTypeR): @(0),
            @(RIAIDColorTypeG): @(2),
            @(RIAIDColorTypeB): @(4),
        },
        @(RIAIDColorHexTypeAARRGGBB): @{
            kRIAIDHexLengthKey: @(2),
            @(RIAIDColorTypeA): @(0),
            @(RIAIDColorTypeR): @(2),
            @(RIAIDColorTypeG): @(4),
            @(RIAIDColorTypeB): @(6),
        },
    };
    if (nil == paramMap[@(cString.length)][@(colorType)]) {
        // 如果取不出来值，则返回一个 1.f，比如 alpha = 1.f
        CGFloat const kDefaultValue = 1.f;
        return kDefaultValue;
    }
    NSUInteger start = [(NSNumber *)paramMap[@(cString.length)][@(colorType)] unsignedIntegerValue];
    NSUInteger length = [(NSNumber *)paramMap[@(cString.length)][kRIAIDHexLengthKey] unsignedIntegerValue];
    return [self _colorTransFormWithString:cString start:start length:length];
}

+ (CGFloat)_colorTransFormWithString:(NSString *)string start:(NSUInteger)start length:(NSUInteger)length {
    NSString *substring = [string substringWithRange:NSMakeRange(start, length)];
    NSString *fullHex = length == 2 ? substring :[NSString stringWithFormat:@"%@%@", substring, substring];
    unsigned hexValue;
    [[NSScanner scannerWithString:fullHex] scanHexInt:&hexValue];
    return hexValue / 255.0;
}

@end
