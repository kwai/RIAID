//
//  RIAIDADTransitionModel+Browser.h
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/20.
//

#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN

/// Transition类型枚举
typedef NS_ENUM(NSUInteger, RIAIDADTransitionType) {
    /// unknown
    RIAIDADTransitionTypeUnknown,
    /// 是否可见转场
    RIAIDADTransitionTypeVisibility,
    /// 模板转场
    RIAIDADTransitionTypeTemplate,
    /// 位移转场
    RIAIDADTransitionTypeTranslation,
    /// 场景内动画
    RIAIDADTransitionTypeInSceneAnimation,
    /// 场景共享元素转场
    RIAIDADTransitionTypeSceneShare,
    /// Lottie操作
    RIAIDADTransitionTypeLottie,
    /// 更新Render内部数据
    RIAIDADTransitionTypeRenderContent
};

/// 给RIAIDADTransitionModel添加一些便利方法
@interface RIAIDADTransitionModel (Browser)

/// 获取RIAIDADTransitionModel对应的Transition类型
- (RIAIDADTransitionType)transitionType;

@end

NS_ASSUME_NONNULL_END
