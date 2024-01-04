//
//  RIAIDBBindingService.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import <Foundation/Foundation.h>
#import "RIAIDRDataBindingService.h"

#import "RIAIDBInternalService.h"
#import "RIAIDBrowserFunctionHandler.h"
#import "RIAIDBGlobalVarArea.h"

NS_ASSUME_NONNULL_BEGIN

/// 数据绑定协议实现类，提供给ADRender处理相关操作
@interface RIAIDBDataBindingService : NSObject<RIAIDRDataBindingService, RIAIDBInternalService>

/// 当前Browser所持有的functionHandler
@property (nonatomic, weak) RIAIDBrowserFunctionHandler *functionHandler;

/// 当前Browser所只有的globalVarArea
@property (nonatomic, weak) RIAIDBGlobalVarArea *globalVarArea;

@end

NS_ASSUME_NONNULL_END
