//
//  KCADRenderImageItemParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderImageItemParser.h"

#import "KCADRenderImageNode.h"

@implementation KCADRenderImageItemParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderImageNode *imageNode = [[KCADRenderImageNode alloc] initWithRenderInfo:renderInfo];
    return imageNode;
}

@end
