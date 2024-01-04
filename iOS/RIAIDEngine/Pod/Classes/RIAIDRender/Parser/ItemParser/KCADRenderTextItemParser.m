//
//  KCADRenderTextItemParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderTextItemParser.h"

#import "KCADRenderTextNode.h"

@implementation KCADRenderTextItemParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderTextNode *textNode = [[KCADRenderTextNode alloc] initWithRenderInfo:renderInfo];
    return textNode;
}

@end
