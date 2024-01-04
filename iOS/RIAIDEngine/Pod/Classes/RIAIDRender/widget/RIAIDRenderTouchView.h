//
//  RIAIDRenderTouchView.h
//  KCADRender
//
//  Created by simon on 2021/12/22.
//

#import <UIKit/UIKit.h>

#import "RIAIDTouchViewDelegate.h"

NS_ASSUME_NONNULL_BEGIN

/// Render 支持事件触发基类 View，用于处理手势事件。需要响应事件的节点 View 需要继承此 View
@interface RIAIDRenderTouchView : UIView

/// 事件代理回调
@property (nonatomic, weak) id<RIAIDTouchViewDelegate> actionDelegate;

@end

NS_ASSUME_NONNULL_END
