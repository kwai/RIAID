//
//  KCADRenderGeometryCalculate.h
//  KCADRender
//
//  Created by simon on 2021/11/16.
//

#import <Foundation/Foundation.h>

#import "RIAID.h"

@class KCADRenderNode;

NS_ASSUME_NONNULL_BEGIN

/// 布局测算类，主要负责布局的简单测算。包括宽高的计算、坐标的转换
@interface KCADRenderGeometryCalculate : NSObject

/// 根据模式、测算数值以及最大数值以及预估尺寸计算出最终的尺寸值
/// @param mode 下发的测量模式，包含 -1/-2/>0，可参考 render 信息里的宽高
/// @param measureVale 计算得出的推荐值
/// @param maxValue 允许的最大值
/// @param estimateValue render 的预估尺寸，会与最大尺寸比较，若预估尺寸在最大尺寸的范围内，则预估尺寸代替最大尺寸进行计算
+ (CGFloat)getSizeValueByMode:(CGFloat)mode
                  measureVale:(CGFloat)measureVale
                     maxValue:(RIAIDFloatValue *)maxValue
                estimateValue:(CGFloat)estimateValue;


/// 绝对坐标映射计算
/// @param render 需要映射的节点
/// @param size 当前节点的的尺寸
+ (CGRect)transformPosition:(KCADRenderNode *)render size:(CGSize)size;

@end

NS_ASSUME_NONNULL_END
