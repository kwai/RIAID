//
//  RIAIDBExternalRenderProvider.h
//  Pods
//
//  Created by liweipeng on 2022/1/16.
//

#ifndef RIAIDBExternalRenderProvider_h
#define RIAIDBExternalRenderProvider_h

#import "RIAID.h"
#import "KCADRender.h"

/// 通过实现此协议，外部可以定制Scene对应的Render
@protocol RIAIDBExternalRenderProvider <NSObject>

/// 根据场景数据模型，生成Render
/// @param sceneModel 场景数据模型
/// @return Render数据
- (KCADRender*)renderWithScene:(RIAIDADSceneModel*)sceneModel;

@end


#endif /* RIAIDBExternalRenderProvider_h */
