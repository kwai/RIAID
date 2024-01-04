//
//  RIAIDBWeakObjectContainer.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/26.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 辅助类，用于持有一个对象
/// 例：NSObject添加Category，想要通过objc_setAssociatedObject对一个对象进行弱持有。
@interface RIAIDBObjectContainer : NSObject

/// 弱持有对象
@property (nonatomic, readonly, weak) id weakObject;

/// 初始化方法
/// @param object 弱持有对象
- (instancetype)initWithWeakObject:(id)object;

/// copy持有对象
@property (nonatomic, readonly, copy) id cpObject;

/// 初始化方法
/// @param object copy持有对象
- (instancetype)initWithCopyObject:(id)object;

@end

NS_ASSUME_NONNULL_END
