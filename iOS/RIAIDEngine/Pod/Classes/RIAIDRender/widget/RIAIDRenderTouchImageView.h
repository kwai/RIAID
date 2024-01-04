//
//  RIAIDRenderTouchImageView.h
//  KCADRender
//
//  Created by simon on 2021/12/22.
//

#import <UIKit/UIKit.h>

#import "RIAIDTouchViewDelegate.h"

NS_ASSUME_NONNULL_BEGIN

/// Render 支持事件触发的 ImageView，用于处理手势事件。需要响应事件的节点 ImageView 需要继承此 ImageView
@interface RIAIDRenderTouchImageView : UIImageView

/// 事件代理回调
@property (nonatomic, weak) id<RIAIDTouchViewDelegate> actionDelegate;

@end

NS_ASSUME_NONNULL_END
