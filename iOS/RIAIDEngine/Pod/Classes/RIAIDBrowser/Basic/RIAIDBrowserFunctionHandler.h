//
//  RIAIDBrowserFunctionHandler.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/4/8.
//

#import <Foundation/Foundation.h>
#import "RIAID.h"

#import "RIAIDBrowserDirector.h"

NS_ASSUME_NONNULL_BEGIN

@interface RIAIDBrowserFunctionHandler : NSObject

/// 注册所有Functions
/// @param functions RIAIDADFunctionModel数组
- (void)registerFunctions:(NSArray<RIAIDADFunctionModel*> *)functions;

/// functionKey 对应的ReadAttributeFunction是否存在
/// @param functionKey Function的Key
- (BOOL)isReadAttributeFunction:(int32_t)functionKey;

/// 读取Function对应数据
/// @param functionKey Function的Key
/// @return 对应数据
- (nullable NSString*)readAttribute:(int32_t)functionKey;

/// 当前Browser所持有的director
@property (nonatomic, strong) RIAIDBrowserDirector *director;
@end

NS_ASSUME_NONNULL_END
