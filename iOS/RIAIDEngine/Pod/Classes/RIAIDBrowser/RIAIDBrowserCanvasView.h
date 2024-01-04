//
//  KCADBCanvasView.h
//  KCADBCanvasView
//
//  Created by liweipeng on 2021/12/11.
//

#import <UIKit/UIKit.h>
#import "RIAIDBrowserDirector.h"
#import "RIAID.h"

NS_ASSUME_NONNULL_BEGIN
@class RIAIDBrowserCanvasView;
/// 画布代理协议
@protocol RIAIDBrowserCanvasViewDelegate <NSObject>
- (void)canvasDidLayoutSubviews:(RIAIDBrowserCanvasView*)canvas;

@end


/// 画布类
@interface RIAIDBrowserCanvasView : UIView
/// 画布的代理实例
@property (nonatomic, weak) id<RIAIDBrowserCanvasViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
