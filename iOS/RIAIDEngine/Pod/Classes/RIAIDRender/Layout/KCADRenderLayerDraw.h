//
//  KCADRenderLayerDraw.h
//  KCADRender
//
//  Created by simon on 2021/11/30.
//

#import <Foundation/Foundation.h>

#import <UIKit/UIKit.h>
#import "RIAID.h"
#import "RIAIDRenderContext.h"

NS_ASSUME_NONNULL_BEGIN

/// shape 绘画类，负责绘画 view 的附带效果，例如渐变、形状等
@interface KCADRenderLayerDraw : NSObject

/// 根据属性绘画渐变色
/// @param gradient 渐变色
+ (CAGradientLayer *)drawGradientLayer:(RIAIDGradient *)gradient frame:(CGRect)frame context:(RIAIDRenderContext *)context;

@end

NS_ASSUME_NONNULL_END
