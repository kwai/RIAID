//
//  UIColor+KCADRenderHex.h
//  KCADRender
//
//  Created by simon on 2021/11/30.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, RIAIDColorType) {
    RIAIDColorTypeA,
    RIAIDColorTypeR,
    RIAIDColorTypeG,
    RIAIDColorTypeB,
};

/// 十六进制颜色类别，用于加载十六进制的颜色
@interface UIColor (KCADRenderHex)

/// 十六进制
/// @param hexString 十六进制的颜色字符串
/// @discussion
/// - 支持 RGB/RRGGBB/ARGB/AARRGGBB 四种十六进制格式的转换
/// - 支持 0x/#/无前缀 三种十六进制写法
+ (UIColor *)riaid_colorWithHexString:(NSString *)hexString;

/// 获取十六进制的透明度
/// @param hexString 十六进制的颜色字符串
/// @discussion
/// - 支持 RGB/RRGGBB/ARGB/AARRGGBB 四种十六进制格式的转换
/// - 支持 0x/#/无前缀 三种十六进制写法
+ (CGFloat)riaid_getAlphaValueWithHexString:(NSString *)hexString;

/// 根据颜色类型和 HEX 格式的字符传，返回对应的色值
/// @param colorType 颜色类型，R/G/B/A 四种
/// @param hexString 十六进制的颜色字符串
/// @discussion
/// - 支持 RGB/RRGGBB/ARGB/AARRGGBB 四种十六进制格式的转换
/// - 支持 0x/#/无前缀 三种十六进制写法
+ (CGFloat)riaid_getColorType:(RIAIDColorType)colorType fromHexString:(NSString *)hexString;

@end

NS_ASSUME_NONNULL_END
