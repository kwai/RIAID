//
//  RIAIDRenderContext.h
//  KCADRender
//
//  Created by simon on 2021/12/20.
//

#import <Foundation/Foundation.h>

#import "RIAIDRServiceContainer.h"

NS_ASSUME_NONNULL_BEGIN

/// 渲染 Render 的上下文，会被所有的 render 节点持有
@interface RIAIDRenderContext : NSObject

@property (nonatomic, strong) id<RIAIDRServiceContainer> serviceContainer;

@end

NS_ASSUME_NONNULL_END
