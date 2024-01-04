//
//  KCADBCanvasView.m
//  KCADBCanvasView
//
//  Created by liweipeng on 2021/12/11.
//

#import "RIAIDBrowserCanvasView.h"

@implementation RIAIDBrowserCanvasView

- (void)layoutSubviews {
    [super layoutSubviews];
    /// 当画布本身的layoutSubviews被触发，回调给代理
    if (_delegate && [_delegate respondsToSelector:@selector(canvasDidLayoutSubviews:)]) {
        [_delegate canvasDidLayoutSubviews:self];
    }
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    // 画布本身不响应事件，若响应者是画布的话，则返回 nil，若是其他视图则正常抛出事件
    UIView *view = [super hitTest:point withEvent:event];
    if ([view isEqual:self]) {
        return nil;
    }
    return view;
}

@end
