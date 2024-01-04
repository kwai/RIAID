//
//  RIAIDBServiceContainer.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/22.
//

#import "RIAIDBServiceContainer.h"
#import "RIAIDBInternalService.h"

@interface RIAIDBServiceContainer()
@property (nonatomic, strong) NSMutableDictionary<NSString*, NSObject*> *serviceMap;
@end


@implementation RIAIDBServiceContainer

- (void)registerExternalService:(Protocol *)service serviceInstance:(id)serviceInstance {
    if (nil == serviceInstance) {
        return;
    }
    NSString *key = NSStringFromProtocol(service);
    NSObject *instance = [self.serviceMap objectForKey:key];
    if ([instance conformsToProtocol:@protocol(RIAIDBInternalService)]) {
        [(id<RIAIDBInternalService>)instance addExternalService:serviceInstance];
    } else {
        [self.serviceMap setObject:serviceInstance forKey:key];
    }
}

- (void)registerService:(Protocol *)service serviceInstance:(id)serviceInstance {
    NSString *key = NSStringFromProtocol(service);
    [self.serviceMap setObject:serviceInstance forKey:key];
}

- (void)unregisterService:(Protocol *)service {
    NSString *key = NSStringFromProtocol(service);
    [self.serviceMap removeObjectForKey:key];
}

- (id)getServiceInstance:(Protocol *)service {
    NSString *key = NSStringFromProtocol(service);
    return [self.serviceMap objectForKey:key];
}

- (void)clean {
    [self.serviceMap removeAllObjects];
}

- (NSMutableDictionary<NSString *,NSObject *> *)serviceMap {
    if (!_serviceMap) {
        _serviceMap = [NSMutableDictionary dictionary];
    }
    return _serviceMap;
}
@end
