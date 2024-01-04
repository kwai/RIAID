//
//  RIAIDRImageLoadService.h
//  KCADRender
//
//  Created by simon on 2021/12/29.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^RIAIDImageLoadCompletionBlock)(UIImage * _Nullable image,
                                             NSError * _Nullable error,
                                             NSString * _Nullable imageUrlString);

/// 图片加载服务
/// 内部默认加载服务
@protocol RIAIDRImageLoaderService <NSObject>

/// 加载图片
/// @param urlString 图片的链接，可能是网络图片，也可能是本地图片
/// @param imageView 需要进行图片加载的 imageView
/// @discussion 本地图片为 file:// 开头，需要 browser 进行解析
- (void)loadImageUrlString:(NSString *)urlString imageView:(UIImageView *)imageView;

/// 加载图片
/// @param urlString 图片的链接，可能是网络图片，也可能是本地图片
/// @param completionBlock 图片加载完成回调
/// @discussion 本地图片为 file:// 开头，需要 browser 进行解析
- (void)loadImageUrlString:(NSString *)urlString
           completionBlock:(RIAIDImageLoadCompletionBlock)completionBlock;

@end

NS_ASSUME_NONNULL_END
