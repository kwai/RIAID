//
//  RIAIDRVideoStateProtocol.h
//  KCADRender
//
//  Created by simon on 2022/2/21.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 视频组件状态接口
/// @description 获取视频状态的接口集合
@protocol RIAIDRVideoStateProtocol <NSObject>

/// 视频是否正在播放
- (BOOL)isPlaying;

/// 是否需要循环播放
- (BOOL)needLoop;

/// 是否需要自动播放
- (BOOL)needAutoPlay;

/// 是否是第一次播放
- (BOOL)isFirstPlay;

/// 获取当前视频播放位置
- (NSTimeInterval)getCurrentPosition;

/// 获取当前视频的总时长
/// @discussion 视频时长 * 循环播放次数
- (NSTimeInterval)getTotalDuration;


@end

NS_ASSUME_NONNULL_END
