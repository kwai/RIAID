//
//  KCADRenderVideoParser.m
//  KCADRender
//
//  Created by simon on 2022/3/22.
//

#import "KCADRenderVideoParser.h"

#import "RIAIDRenderVideoNode.h"

@implementation KCADRenderVideoParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    RIAIDRenderVideoNode *videoNode = [[RIAIDRenderVideoNode alloc] initWithRenderInfo:renderInfo];
    return videoNode;
}

@end
