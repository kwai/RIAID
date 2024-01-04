//
//  RIAIDRServiceContainer.h
//  KCADRender
//
//  Created by simon on 2021/12/19.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// ADRender 通用接口容器
@protocol RIAIDRServiceContainer <NSObject>

/// 注册某个接口
/// @param service 需要注册的接口
/// @param serviceInstance 实现该接口的实例
- (void)registerService:(Protocol *)service serviceInstance:(id)serviceInstance;

/// 注销某个接口
/// @param service 需要注销的接口
- (void)unregisterService:(Protocol *)service;

/// 获取遵循某个接口的实例
/// @param service 目标接口
- (id)getServiceInstance:(Protocol *)service;

/// 清除容器内注册的所有接口实例
- (void)clean;

@end

NS_ASSUME_NONNULL_END
