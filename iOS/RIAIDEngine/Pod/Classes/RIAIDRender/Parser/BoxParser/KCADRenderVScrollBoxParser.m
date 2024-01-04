//
//  KCADRenderVScrollBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderVScrollBoxParser.h"

#import "KCADRenderVScrollNode.h"

@implementation KCADRenderVScrollBoxParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderVScrollNode *vScrollNode = [[KCADRenderVScrollNode alloc] initWithRenderInfo:renderInfo];
    return vScrollNode;
}

@end
