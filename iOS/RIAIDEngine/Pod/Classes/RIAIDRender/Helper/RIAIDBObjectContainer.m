//
//  RIAIDBWeakObjectContainer.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/26.
//

#import "RIAIDBObjectContainer.h"

@implementation RIAIDBObjectContainer

- (instancetype)initWithWeakObject:(id)object {
    self = [super init];
    if (self) {
        _weakObject = object;
    }
    
    return self;
}

- (instancetype)initWithCopyObject:(id)object {
    self = [super init];
    if (self) {
        _cpObject = [object copy];
    }
    
    return self;
}
@end
