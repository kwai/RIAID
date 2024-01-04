//
//  KCADRenderAbsoluteBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderAbsoluteBoxParser.h"

#import "KCADRenderAbsoluteNode.h"

@implementation KCADRenderAbsoluteBoxParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderAbsoluteNode *absoluteNode = [[KCADRenderAbsoluteNode alloc] initWithRenderInfo:renderInfo];
    return absoluteNode;
}

@end
