//
//  RIAIDRenderVideoNode.h
//  KCADRender
//
//  Created by simon on 2022/2/18.
//

#import "KCADRenderItemNode.h"

#import "RIAIDRVideoStateProtocol.h"

NS_ASSUME_NONNULL_BEGIN

/// Video 视频节点
/// @discussion 用于渲染 RIAID 视频, 最终生成的视图层级为：封面图(coverView)和播放器视图(playerView)作为子视图添加到内容视图(contentView)上
@interface RIAIDRenderVideoNode : KCADRenderItemNode

/// 获取视频节点中包含的视频信息
- (id<RIAIDRVideoStateProtocol>)getVideoStateInfo;

@end

NS_ASSUME_NONNULL_END
