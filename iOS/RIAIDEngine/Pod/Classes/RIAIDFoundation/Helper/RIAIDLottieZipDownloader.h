//
//  RIAIDLottieZipDownloader.h
//  KCADRender
//
//  Created by simon on 2021/12/31.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^RIAIDLottieZipDownloadBlock)(NSString * _Nullable jsonName, NSError * _Nullable error);

/// zip lottie 远程资源下载器
@interface RIAIDLottieZipDownloader : NSObject

/// 解压远程 lottie 资源
/// @param lottieUrlString 远程 lottie 资源
/// @param resultBlock 结果 block，其中会返回两个参数。jsonName 代表下载的 json 路径，error 为解压报错信息
+ (void)unZipWithLottieUrlString:(NSString *)lottieUrlString
                         success:(RIAIDLottieZipDownloadBlock)resultBlock;

@end

NS_ASSUME_NONNULL_END
