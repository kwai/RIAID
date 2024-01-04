//
//  RIAIDLottieZipDownloader.m
//  KCADRender
//
//  Created by simon on 2021/12/31.
//

#import "RIAIDLottieZipDownloader.h"

#import <SSZipArchive/ZipArchive.h>

NSString * const kLottieFilePath = @"Lottie";

@implementation RIAIDLottieZipDownloader

+ (void)unZipWithLottieUrlString:(NSString *)lottieUrlString
                         success:(RIAIDLottieZipDownloadBlock)resultBlock {
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        // 有 json 取 json
        NSString *lottiePath = [self _findLottieFileWithUrlString:lottieUrlString];
        if (lottiePath.length > 0) {
            if (resultBlock) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    resultBlock(lottiePath, nil);
                });
            }
            return;
        }
        
        // 无 json 的话，有 zip 解压 zip
        NSFileManager *fileManager = [NSFileManager defaultManager];
        NSString *lottieDir = [self _findAndCreateLottieFile];
        NSString *lottieName = [self _getLottieNameWithUrlString:lottieUrlString];
        __block NSString *lottieUnzipDir = [lottieDir stringByAppendingPathComponent:lottieName];
        NSString *zipName = [lottieName stringByAppendingPathExtension:@"zip"];
        __block NSString *zipFilePath = [lottieDir stringByAppendingPathComponent:zipName];
        if ([fileManager fileExistsAtPath:zipFilePath]) {
            [self _unzipFilePath:zipFilePath
                   toDestination:lottieUnzipDir
                     resultBlock:resultBlock];
            return;
        }
        // json 和 zip 都没，重新请求，并且解压
        NSURL *url = [NSURL URLWithString:lottieUrlString];
        NSURLRequest *request = [NSURLRequest requestWithURL:url];
        NSURLSession *session = [NSURLSession sharedSession];
        NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request
                                                    completionHandler:^(NSData * _Nullable data,
                                                                        NSURLResponse * _Nullable response,
                                                                        NSError * _Nullable error) {
            [data writeToFile:zipFilePath atomically:YES];
            [self _unzipFilePath:zipFilePath
                   toDestination:lottieUnzipDir
                     resultBlock:resultBlock];
        }];
        [dataTask resume];
    });
}

#pragma mark - private method
/// 查找 lottie 缓存目录，无相关目录，会直接进行创建
/// @discussion 路径为 Library/Cache/Lottie/
+ (NSString *)_findAndCreateLottieFile {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *cachePath = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES)
                           objectAtIndex:0];
    NSString *lottieFilePath = [cachePath stringByAppendingPathComponent:kLottieFilePath];
    BOOL isDir = NO;
    BOOL existed = [fileManager fileExistsAtPath:lottieFilePath isDirectory:&isDir];
    if (!isDir || !existed) {
        [fileManager createDirectoryAtPath:lottieFilePath
                         withIntermediateDirectories:YES
                                          attributes:nil
                                               error:nil];
    }
    return lottieFilePath;
}

/// 根据 url，重新拼接 lottie 解压后的目录名称
+ (NSString *)_getLottieNameWithUrlString:(NSString *)lottieUrlString {
    NSURL *url = [NSURL URLWithString:lottieUrlString];
    NSMutableString *lottieName = [NSMutableString string];
    if (url.host.length > 0) {
        [lottieName appendString:url.host];
    }
    if (url.path.length > 0) {
        [lottieName appendString:[url.path stringByReplacingOccurrencesOfString:@"/" withString:@"_"]];
    }
    return [lottieName stringByDeletingPathExtension].copy;
}

/// 根据 url 查找解压后的最终 json 路径
+ (NSString *)_findLottieFileWithUrlString:(NSString *)lottieUrlString {
    NSString *lottieName = [self _getLottieNameWithUrlString:lottieUrlString];
    NSString *lottieUnzipDir = [[self _findAndCreateLottieFile] stringByAppendingPathComponent:lottieName];
    return [self _findLottieFileWithUnzipDir:lottieUnzipDir];
}

/// 根据解压目录查找解压后的最终 json 路径
+ (NSString *)_findLottieFileWithUnzipDir:(NSString *)unzipDir {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isDir = NO;
    if ([fileManager fileExistsAtPath:unzipDir isDirectory:&isDir]) {
        if (isDir) {
            NSDirectoryEnumerator *directoryEnumerator = [fileManager enumeratorAtPath:unzipDir];
            for (NSString *path in directoryEnumerator.allObjects) {
                if ([path.pathExtension isEqualToString:@"json"]) {
                    return [unzipDir stringByAppendingPathComponent:path];
                }
            }
        }
    }
    return nil;
}

/// 传入目标文件以及制定解压路径，进行解压
/// @discussion 解压后的目录机构为 Library/Cache/Lottie/{filePath}/{lottie 名称}
+ (void)_unzipFilePath:(NSString *)filePath
         toDestination:(NSString *)destination
           resultBlock:(RIAIDLottieZipDownloadBlock)resultBlock {
    [SSZipArchive unzipFileAtPath:filePath
                    toDestination:destination
                        overwrite:NO
                         password:nil
                  progressHandler:nil
                completionHandler:^(NSString * _Nonnull path, BOOL succeeded, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (error
                && resultBlock) {
                resultBlock(nil, error);
            } else {
                if (resultBlock) {
                    resultBlock([self _findLottieFileWithUnzipDir:destination], nil);
                }
            }
        });
    }];

}

@end
