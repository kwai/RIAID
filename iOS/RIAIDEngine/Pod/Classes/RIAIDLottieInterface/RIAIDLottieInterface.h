//
//  RIAIDLottieInterface.h
//  Pods
//
//  Created by liweipeng on 2022/3/8.
//

#ifndef RIAIDLottieInterface_h
#define RIAIDLottieInterface_h

/// Lottie循环模式枚举
typedef NS_ENUM(NSInteger, RIAIDLottieViewLoopMode) {
    RIAIDLottieViewLoopModePlayOnce = 0,
    RIAIDLottieViewLoopModeLoop = 1,
    RIAIDLottieViewLoopModeAutoReverse = 2,
};

NS_ASSUME_NONNULL_BEGIN

typedef void(^LottiePlayCompletion)(BOOL complete);

/// 定义LottieView所需功能的接口
@protocol RIAIDLottieViewInterface <NSObject>

/// 动画速度
@property (nonatomic) CGFloat animationSpeed;
/// 播放进度
@property (nonatomic) CGFloat progress;
/// 循环模式
@property (nonatomic) enum RIAIDLottieViewLoopMode loopMode;

/// 获取真正展示Lottie动画的View
- (UIView *)getLottieView;
/// 暂停播放
- (void)pause;
/// 开始播放
- (void)play;
/// 开始播放，带播放完成回调
- (void)playWithCompletion:(nullable LottiePlayCompletion)completion;
/// 停止播放
- (void)stop;
/// 强制重绘动画上内容
- (void)forceDisplayUpdate;

/// 动态给 lottie 赋值文本
/// @param textDictionary 需要更改的文本 map，textDictionary 中的 key 为 lottie 的原始文本，value 为需要替换成的文本。
- (void)setDynamicTextWithTextDictionary:(NSDictionary<NSString *, NSString *> * _Nullable)textDictionary;

/// 替换 Lottie 某个 keypath 的色值
/// @param colorR R
/// @param colorG G
/// @param colorB B
/// @param colorA A
/// @param keyPathString 需要替换的 keyPath
/// @discussion 由于 Lottie 的 ColorValueProvider 只支持内部的 Color 结构体，所以需要外部先将目标色值转化为 RGBA 形式
- (void)setColorValue:(CGFloat)colorR
               colorG:(CGFloat)colorG
               colorB:(CGFloat)colorB
               colorA:(CGFloat)colorA
        keyPathString:(NSString *)keyPathString;

@end


/// 定义LottieView获取方式的接口
/// 因为目前的IoC框架，难以支持通过多种构造方式获取实例。所以通过中间层对象LottieViewProvider，支持通过不同的参数获取LottieView。
@protocol RIAIDLottieViewProviderInterface <NSObject>

/// 通过资源名称创建Lottie动画View
/// @param lottieName 资源名称
- (id<RIAIDLottieViewInterface>)viewWithLottieName:(NSString*)lottieName;


/// 通过文件路径创建Lottie动画View
/// @param filePath 文件路径
- (id<RIAIDLottieViewInterface>)viewWithFilePath:(NSString*)filePath;


/// 通过url创建Lottie动画View
/// @param url Lottie的远程资源地址
/// @param complete Lottie加载完成回调
- (id<RIAIDLottieViewInterface>)viewWithURL:(NSURL*)url complete:(void (^)(NSError *error))complete;

@end

NS_ASSUME_NONNULL_END

#endif /* RIAIDLottieInterface_h */
