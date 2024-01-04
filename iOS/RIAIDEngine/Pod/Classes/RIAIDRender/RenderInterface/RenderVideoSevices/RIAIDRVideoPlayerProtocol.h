//
//  RIAIDRVideoPlayerProtocol.h
//  KCADRender
//
//  Created by simon on 2022/2/18.
//

#import <Foundation/Foundation.h>

#import "RIAIDRVideoControlProtocol.h"
#import "RIAIDRVideoStateProtocol.h"

NS_ASSUME_NONNULL_BEGIN

/// 视频组件代理
/// @description 用于监听视频对应动作的代理
@protocol RIAIDRVideoDelegate <NSObject>

@optional

/// 视频处于准备播放阶段
- (void)videoDidPrepare;

/// 视频处于预加载阶段
- (void)videoDidPreload;

/// 视频开始播放
- (void)videoDidStart;

/// 视频处于暂停阶段
- (void)videoDidPause;

/// 视频准备完毕
/// @param duration 视频时长 单位s
- (void)videoDidFinishPreparingWithDuration:(NSTimeInterval)duration;

/// 视频首帧渲染完毕
- (void)videoDidRenderFirstVideoFrame;

/// 播放完成
- (void)videoDidPlayAtEnd;

/// 视频播放失败
- (void)videoDidPlayError:(NSError *)error;

/// 视频下载回调
/// @param downloadSize 已下载的进度
/// @param totalSize 总进度
- (void)videoDidDownLoadSize:(long long)downloadSize totalSize:(long long)totalSize;

@end

@protocol RIAIDRVideoPlayerProtocol <RIAIDRVideoControlProtocol, RIAIDRVideoStateProtocol>

/// 需要显示的View
@property (nonatomic, readonly, strong) UIView *playerView;

/// 添加代理监听 (可初始化 delegatesSupporter 实例 去管理多个delegate对象)
- (void)addDelegate:(id <RIAIDRVideoDelegate>)delegate;

/// 移除一个代理监听
- (void)removeDelegate:(id <RIAIDRVideoDelegate>)delegate;

/// 设置视频资源，可以通过 url 和 manifest 两种形式
/// @param urlString 字符串形式的视频 url 资源
/// @param manifest manifest 格式的视频资源
- (void)setDataSource:(nullable NSString *)urlString manifest:(nullable NSString *)manifest;

@end

NS_ASSUME_NONNULL_END
