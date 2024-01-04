//
//  KCADRenderHorizontalBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderHorizontalBoxParser.h"

#import "KCADRenderHorizontalNode.h"

@implementation KCADRenderHorizontalBoxParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderHorizontalNode *horizontalNode = [[KCADRenderHorizontalNode alloc] initWithRenderInfo:renderInfo];
    return horizontalNode;
}

@end
