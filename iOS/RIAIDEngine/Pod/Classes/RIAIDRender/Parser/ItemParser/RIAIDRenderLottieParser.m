//
//  KCADRenderLottieParser.m
//  KCADRender
//
//  Created by simon on 2021/12/20.
//

#import "RIAIDRenderLottieParser.h"

#import "RIAIDRenderLottieNode.h"

@implementation RIAIDRenderLottieParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    RIAIDRenderLottieNode *lottieNode = [[RIAIDRenderLottieNode alloc] initWithRenderInfo:renderInfo];
    return lottieNode;
}

@end
