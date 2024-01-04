//
//  KCADRenderSpaceNode.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderSpaceNode.h"

#pragma mark - utils
#import "KCADRenderGeometryCalculate.h"
#import "KCADRenderKBase.h"

@implementation KCADRenderSpaceNode

- (CGSize)measureWithEstimateWidth:(CGFloat)estimateWidth estimateHeight:(CGFloat)estimateHeight {
    [super measureWithEstimateWidth:estimateWidth estimateHeight:estimateHeight];
    if (nil == self.renderInfo) {
        NSAssert(NO, @"节点对应的数据不应该为空");
        return CGSizeZero;
    }
    CGSize renderSize = CGSizeZero;
    renderSize.width = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderWidth:self.renderInfo]
                                                           measureVale:self.renderInfo.layout.width
                                                              maxValue:self.renderInfo.layout.maxWidth
                                                         estimateValue:estimateWidth];
    renderSize.height = [KCADRenderGeometryCalculate getSizeValueByMode:[KCADRenderKBase renderHeight:self.renderInfo]
                                                            measureVale:self.renderInfo.layout.height
                                                               maxValue:self.renderInfo.layout.maxHeight
                                                          estimateValue:estimateHeight];
    return renderSize;
}

@end
