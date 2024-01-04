//
//  KCADRenderSpaceItemParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderSpaceItemParser.h"

#import "KCADRenderSpaceNode.h"

@implementation KCADRenderSpaceItemParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderSpaceNode *spaceNode = [[KCADRenderSpaceNode alloc] initWithRenderInfo:renderInfo];
    return spaceNode;
}

@end
