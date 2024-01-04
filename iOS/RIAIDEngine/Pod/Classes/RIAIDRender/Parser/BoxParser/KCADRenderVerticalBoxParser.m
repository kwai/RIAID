//
//  KCADRenderVerticalBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderVerticalBoxParser.h"

#import "KCADRenderVerticalNode.h"

@implementation KCADRenderVerticalBoxParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderVerticalNode *verticalNode = [[KCADRenderVerticalNode alloc] initWithRenderInfo:renderInfo];
    return verticalNode;
}

@end
