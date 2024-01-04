//
//  RIAIDBInternalService.h
//  KCADBrowser
//
//  Created by liweipeng on 2022/3/7.
//

#ifndef RIAIDBInternalService_h
#define RIAIDBInternalService_h

/// RIAID内置服务协议
/// 服务的传递关系：外部(External) -> Browser内部(Internal) -> Render使用
/// Render需要的服务，可能既需要外部传入，也需要Browser内部处理。需要Browser内部处理的服务，统一实现本协议。
@protocol RIAIDBInternalService <NSObject>

/// 添加外部协议实例
- (void)addExternalService:(NSObject*)serviceInstance;

@end


#endif /* RIAIDBInternalService_h */
