//
//  RIAIDRenderArrayIterator.m
//  KCADRender
//
//  Created by simon on 2021/12/22.
//

#import "RIAIDRenderArrayIterator.h"

#import "KCADRenderNode.h"
#import "KCADRenderButtonNode.h"
#import "RIAIDSwitchProtocol.h"

#pragma mark - 迭代器 item

/// 用于记录正在遍历的数组
@interface NSArrayIteratorItem : NSObject

@property (nonatomic, copy) NSArray *array;
// 记录当前数组的索引位置
@property (nonatomic, assign) NSUInteger index;

@end

@implementation NSArrayIteratorItem

- (id)initWithArray:(NSArray *)array {
    if (self = [super init]) {
        _array = array;
        _index = 0;
    }
    return self;
}

@end

#pragma mark - 迭代器
@interface RIAIDRenderArrayIterator ()

@property (nonatomic, copy) NSArray *array;
@property (nonatomic, strong) NSMutableArray *stack;
@property (nonatomic, strong) RIAIDRenderContext *context;

@end

@implementation RIAIDRenderArrayIterator

- (instancetype)initWithArray:(nullable NSArray *)array context:(RIAIDRenderContext *)context {
    if (self = [super init]) {
        _array = array;
        NSArrayIteratorItem *item = [[NSArrayIteratorItem alloc] initWithArray:array];
        _stack = [NSMutableArray arrayWithObject:item];
        _context = context;
    }
    return self;
}

- (id)next {
    id<RIAIDSwitchProtocol>switchService = [self.context.serviceContainer getServiceInstance:@protocol(RIAIDSwitchProtocol)];
    if ([[switchService switchValueForKey:@"enableiOSAdImagePreload"] boolValue]) {
        if (self.stack.count == 0) {
            return nil;
        }
        // 取栈尾元素
        NSArrayIteratorItem *item = [self.stack lastObject];
        while (item.index == item.array.count
               && self.stack.count > 0) {
            [self.stack removeLastObject];
            item = [self.stack lastObject];
        }
        // 对栈进行取值操作后，再次判空
        if (self.stack.count == 0) {
            return nil;
        }
        // 判断子节点是否有值
        if ([item.array[item.index] isKindOfClass:[KCADRenderNode class]]) {
            KCADRenderNode *valueNode = item.array[item.index];
            if (valueNode.childList.count > 0
                || [valueNode isKindOfClass:[KCADRenderButtonNode class]]) {
                // 更新父数组的索引, 进行子节点的遍历
                item.index ++;
                NSMutableArray <KCADRenderNode *> *valueNodeArray = [NSMutableArray arrayWithArray:valueNode.childList];
                if ([valueNode isKindOfClass:[KCADRenderButtonNode class]]) {
                    [valueNodeArray addObject:((KCADRenderButtonNode *)valueNode).contentRender];
                }
                NSArrayIteratorItem *newItem = [[NSArrayIteratorItem alloc] initWithArray:valueNodeArray];
                [self.stack addObject:newItem];
                return valueNode;
            }
        } else if ([item.array[item.index] isKindOfClass:[RIAIDNode class]]) {
            RIAIDNode *valueNode = item.array[item.index];
            if (valueNode.childrenArray.count > 0
                || valueNode.classType == RIAIDNode_ClassType_ClassTypeLayoutButton) {
                // 更新父数组的索引, 进行子节点的遍历
                item.index ++;
                NSMutableArray <RIAIDNode *> *valueNodeArray = [NSMutableArray arrayWithArray:valueNode.childrenArray];
                if (valueNode.classType == RIAIDNode_ClassType_ClassTypeLayoutButton) {
                    [valueNodeArray addObject:valueNode.attributes.button.content];
                }
                NSArrayIteratorItem *newItem = [[NSArrayIteratorItem alloc] initWithArray:valueNodeArray];
                [self.stack addObject:newItem];
                return valueNode;
            }
        }
        // 拿到一个值，并更新索引位置
        id node = item.array[item.index];
        item.index ++;
        return node;
    } else {
        if (self.stack.count == 0) {
            return nil;
        }
        // 取栈尾元素
        NSArrayIteratorItem *item = [self.stack lastObject];
        while (item.index == item.array.count
               && self.stack.count > 0) {
            [self.stack removeLastObject];
            item = [self.stack lastObject];
        }
        // 对栈进行取值操作后，再次判空
        if (self.stack.count == 0) {
            return nil;
        }
        // 判断子节点是否有值
        KCADRenderNode *valueNode = item.array[item.index];
        if ([valueNode isKindOfClass:[KCADRenderNode class]]) {
            if (valueNode.childList.count > 0
                || [valueNode isKindOfClass:[KCADRenderButtonNode class]]) {
                // 更新父数组的索引, 进行子节点的遍历
                item.index ++;
                NSMutableArray <KCADRenderNode *> *valueNodeArray = [NSMutableArray arrayWithArray:valueNode.childList];
                if ([valueNode isKindOfClass:[KCADRenderButtonNode class]]) {
                    [valueNodeArray addObject:((KCADRenderButtonNode *)valueNode).contentRender];
                }
                NSArrayIteratorItem *newItem = [[NSArrayIteratorItem alloc] initWithArray:valueNodeArray];
                [self.stack addObject:newItem];
                return valueNode;
            }
        }
        // 拿到一个值，并更新索引位置
        item.index ++;
        return valueNode;
    }
}

@end
