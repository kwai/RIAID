//
//  RIAIDBServiceContainer.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import <Foundation/Foundation.h>
#import "RIAIDRServiceContainer.h"

/// Service容器，提供给ADRender处理相关操作
/// 内部实现了注册、接触协议等方法
@interface RIAIDBServiceContainer : NSObject<RIAIDRServiceContainer>

/// 注册外部服务
/// @param service 服务遵循的协议
/// @param serviceInstance 提供服务的实例
- (void)registerExternalService:(Protocol *)service serviceInstance:(id)serviceInstance;

@end
