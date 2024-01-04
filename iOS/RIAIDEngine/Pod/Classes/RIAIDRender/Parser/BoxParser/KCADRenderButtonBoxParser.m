//
//  KCADRenderButtonBoxParser.m
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderButtonBoxParser.h"

#import "KCADRenderButtonNode.h"

@implementation KCADRenderButtonBoxParser

- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    KCADRenderButtonNode *buttonNode = [[KCADRenderButtonNode alloc] initWithRenderInfo:renderInfo];
    return buttonNode;
}

@end
