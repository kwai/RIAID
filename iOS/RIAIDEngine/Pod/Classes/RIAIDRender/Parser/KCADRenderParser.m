//
//  KCADRenderParser.m
//  KCADRender
//
//  Created by simon on 2021/11/10.
//

#import "KCADRenderParser.h"

@implementation KCADRenderParser

// 子类需要重写，父类返回为空
- (NSString *)getParseKey {
    NSAssert(NO, @"该方法为父类声明的抽象方法，子类需要重写，直接调用父类无实际意义");
    return nil;
}

// 子类需要重写，父类返回为空
- (KCADRenderNode *)createRender:(RIAIDNode *)renderInfo {
    NSAssert(NO, @"该方法为父类声明的抽象方法，子类需要重写，直接调用父类无实际意义");
    return nil;
}

- (BOOL)canParseWithKey:(NSString *)parseKey {
    return [parseKey isEqualToString:[self getParseKey]];
}

- (KCADRenderNode *)parseWithRenderInfo:(RIAIDNode *)renderInfo
                           nodeDelegate:(id<KCADRenderNodeDelegate>)delegate
                                context:(nonnull RIAIDRenderContext *)context {
    KCADRenderNode *renderNode = [self createRender:renderInfo];
    renderNode.context = context;
    renderNode.renderInfo = renderInfo;
    renderNode.delegate = delegate;
    return renderNode;
}

@end
