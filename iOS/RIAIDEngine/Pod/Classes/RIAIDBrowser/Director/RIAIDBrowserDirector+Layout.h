//
//  RIAIDBrowserDirector+Layout.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/19.
//

#import "RIAIDBrowserDirector.h"

NS_ASSUME_NONNULL_BEGIN
/// 内部处理所有Relation布局操作
@interface RIAIDBrowserDirector (Layout)

/// 更新约束
/// @param layoutModel relation约束模型
- (void)addLayout:(RIAIDADSceneRelationModel*)layoutModel;

/// 更新所有注册过的renderView的约束
- (void)executeAllLayout;
@end

NS_ASSUME_NONNULL_END
