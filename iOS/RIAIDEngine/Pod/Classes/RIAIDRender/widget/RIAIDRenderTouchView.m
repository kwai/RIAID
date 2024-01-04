//
//  RIAIDRenderTouchView.m
//  KCADRender
//
//  Created by simon on 2021/12/22.
//

#import "RIAIDRenderTouchView.h"

#import <YYText/YYText.h>

@interface RIAIDRenderTouchView ()<UIGestureRecognizerDelegate>

@property (nonatomic, strong) UITapGestureRecognizer *singleTap;
@property (nonatomic, strong) UITapGestureRecognizer *doubleTap;
@property (nonatomic, strong) UILongPressGestureRecognizer *longPress;

@end

@implementation RIAIDRenderTouchView

- (instancetype)init {
    if (self = [super init]) {
        self.userInteractionEnabled = NO;
        [self adjustGesturePriority];
    }
    return self;
}

- (void)adjustGesturePriority {
    [self.singleTap requireGestureRecognizerToFail:self.doubleTap];
    [self.doubleTap requireGestureRecognizerToFail:self.longPress];
}

#pragma mark - delegate
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    // 是否能被识别
    BOOL isCanRecognize = YES;
    if ([touch.view isKindOfClass:[YYLabel class]]) {
        YYLabel *label = (YYLabel *)touch.view;
        NSAttributedString *attributedString = label.attributedText;
        NSRange textRange = [[label.textLayout textRangeAtPoint:[touch locationInView:label]] asRange];
        if ([self.actionDelegate respondsToSelector:@selector(haveHandlerWithRichText:textRange:)]) {
            isCanRecognize = ![self.actionDelegate haveHandlerWithRichText:attributedString textRange:textRange];
        }
    }
    return isCanRecognize;
}

#pragma mark - action
- (void)onSingleTapAction:(UITapGestureRecognizer *)sender {
    if ([self.actionDelegate respondsToSelector:@selector(onSingleTapAction)]) {
        [self.actionDelegate onSingleTapAction];
    }
}

- (void)onDoubleTapAction:(UITapGestureRecognizer *)sender {
    if ([self.actionDelegate respondsToSelector:@selector(onDoubleTapAction)]) {
        [self.actionDelegate onDoubleTapAction];
    }
}

- (void)onLongPressAction:(UILongPressGestureRecognizer *)sender {
    if (sender.state == UIGestureRecognizerStateBegan) {
        if ([self.actionDelegate respondsToSelector:@selector(onLongPressAction)]) {
            [self.actionDelegate onLongPressAction];
        }
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self pressStart];
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self pressEnd];
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    return [self findResponseViewWithPoint:point view:self];
}

- (void)pressStart {
    if ([self.actionDelegate respondsToSelector:@selector(onPressStart:)]) {
        [self.actionDelegate onPressStart:YES];
    }
}

- (void)pressEnd {
    if ([self.actionDelegate respondsToSelector:@selector(onPressEnd:)]) {
        [self.actionDelegate onPressEnd:YES];
    }
}

#pragma mark - getter
- (UITapGestureRecognizer *)singleTap {
    if (!_singleTap) {
        _singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self
                                                             action:@selector(onSingleTapAction:)];
        _singleTap.delaysTouchesEnded = NO;
        _singleTap.cancelsTouchesInView = NO;
        _singleTap.delegate = self;
        _singleTap.numberOfTapsRequired = 1;
        [self addGestureRecognizer:_singleTap];
    }
    return _singleTap;
}

- (UITapGestureRecognizer *)doubleTap {
    if (!_doubleTap) {
        _doubleTap = [[UITapGestureRecognizer alloc] initWithTarget:self
                                                             action:@selector(onDoubleTapAction:)];
        _doubleTap.delaysTouchesEnded = NO;
        _doubleTap.cancelsTouchesInView = NO;
        _doubleTap.delegate = self;
        _doubleTap.numberOfTapsRequired = 2;
        [self addGestureRecognizer:_doubleTap];
    }
    return _doubleTap;
}

- (UILongPressGestureRecognizer *)longPress {
    if (!_longPress) {
        _longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self
                                                                   action:@selector(onLongPressAction:)];
        _longPress.delaysTouchesEnded = NO;
        _longPress.cancelsTouchesInView = NO;
        _longPress.delegate = self;
        _longPress.allowableMovement = NO;
        [self addGestureRecognizer:_longPress];
    }
    return _longPress;
}

#pragma mark - private method
// 子当前视图和其子视图中寻找最合适响应的视图
- (UIView *)findResponseViewWithPoint:(CGPoint)point view:(UIView *)view {
    UIView *responseView = nil;
    if (self.userInteractionEnabled
        && !self.hidden
        && CGRectContainsPoint(self.bounds, point)) {
        responseView = self;
    }
    // 反转子视图列表，将列表反转成后序遍历的结果
    NSArray *subviews = [[[self findAllSubViews:view] reverseObjectEnumerator] allObjects];
    // 遍历子视图，最顶层的可响应子视图响应事件
    for (UIView *subview in subviews) {
        CGPoint viewPoint = [self convertPoint:point toView:subview];
        if (CGRectContainsPoint(subview.bounds, viewPoint)
            && subview.userInteractionEnabled
            && !self.hidden) {
            responseView = subview;
            break;
        }
    }
    return responseView;
}

// 寻找当前视图下的所有子视图，顺序插入子视图
- (NSArray *)findAllSubViews:(UIView *)view {
    NSMutableArray *views = [NSMutableArray array];
    for (UIView *subView in view.subviews) {
        [views addObject:subView];
        if (subView.subviews.count > 0) {
            [views addObjectsFromArray:[self findAllSubViews:subView]];
        }
    }
    return views.copy;
}

@end
