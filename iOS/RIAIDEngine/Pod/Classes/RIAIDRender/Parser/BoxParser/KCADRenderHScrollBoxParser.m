//
//  KCADRenderHScrollBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderHScrollBoxParser.h"

#import "KCADRenderHScrollNode.h"

@implementation KCADRenderHScrollBoxParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderHScrollNode *hScrollNode = [[KCADRenderHScrollNode alloc] initWithRenderInfo:renderInfo];
    return hScrollNode;
}

@end
