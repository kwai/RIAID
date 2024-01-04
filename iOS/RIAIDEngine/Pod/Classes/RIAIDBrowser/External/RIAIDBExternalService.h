//
//  RIAIDBExternalService.h
//  Pods
//
//  Created by liweipeng on 2022/1/16.
//

#ifndef RIAIDBExternalService_h
#define RIAIDBExternalService_h

/// 通过实现此协议，定义外部传入服务
@protocol RIAIDBExternalServiceProtocol <NSObject>

/// 服务遵循的协议
- (Protocol*)serviceProtocol;

/// 提供服务的实例
- (id)serviceInstance;

@end

#endif /* RIAIDBExternalService_h */
