//
//  RenderVideoControlProtocol.h
//  KCADRender
//
//  Created by simon on 2022/2/21.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 视频行为控制协议
/// @description 声明了视频行为控制相关的接口
@protocol RIAIDRVideoControlProtocol <NSObject>

/// 准备播放
- (void)prepareToPlay;

/// 播放视频
- (void)play;

/// 暂停视频
- (void)pause;

/// 恢复视频
- (void)resume;

/// 停止视频
- (void)stop;

/// 跳转播放
/// @param time 需要跳转到的时刻
- (void)seekToTime:(NSTimeInterval)time;

/// 设置循环播放
/// @param loop 设置为 YES 时表示开启循环播放
- (void)setLoop:(BOOL)loop;

/// 设置播放器的自动播放
/// @param autoPlay 自动播放
- (void)setAutoPlay:(BOOL)autoPlay;

/// 设置视频音量
/// @param leftVolume 左声道音量大小
/// @param rightVolume 右声道音量大小
- (void)setLeftVolume:(float)leftVolume rightVolume:(float)rightVolume;;

/// 设置播放器静音
/// @param mute 是否静音，YES 将设置播放器为静音，NO 将设置播放器为非静音
- (void)setVolumeMute:(BOOL)mute;

@end

NS_ASSUME_NONNULL_END
