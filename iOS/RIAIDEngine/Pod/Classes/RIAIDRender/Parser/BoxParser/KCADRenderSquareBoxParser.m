//
//  KCADRenderSquareBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderSquareBoxParser.h"

#import "KCADRenderSquareNode.h"

@implementation KCADRenderSquareBoxParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderSquareNode *squareNode = [[KCADRenderSquareNode alloc] initWithRenderInfo:renderInfo];
    return squareNode;
}

@end
