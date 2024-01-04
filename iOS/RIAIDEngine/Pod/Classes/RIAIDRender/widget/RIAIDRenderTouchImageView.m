//
//  RIAIDRenderTouchImageView.m
//  KCADRender
//
//  Created by simon on 2021/12/22.
//

#import "RIAIDRenderTouchImageView.h"

@interface RIAIDRenderTouchImageView ()<UIGestureRecognizerDelegate>

@property (nonatomic, strong) UITapGestureRecognizer *singleTap;
@property (nonatomic, strong) UITapGestureRecognizer *doubleTap;
@property (nonatomic, strong) UILongPressGestureRecognizer *longPress;

@end

@implementation RIAIDRenderTouchImageView

- (instancetype)init {
    if (self = [super init]) {
        self.userInteractionEnabled = YES;
        [self adjustGesturePriority];
    }
    return self;
}

- (void)adjustGesturePriority {
    [self.singleTap requireGestureRecognizerToFail:self.doubleTap];
    [self.doubleTap requireGestureRecognizerToFail:self.longPress];
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
    } else if (sender.state == UIGestureRecognizerStateEnded
               || sender.state == UIGestureRecognizerStateCancelled) {
        if ([self.actionDelegate respondsToSelector:@selector(onPressEnd:)]) {
            [self.actionDelegate onPressEnd:NO];
        }
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    if ([self.actionDelegate respondsToSelector:@selector(onPressStart:)]) {
        [self.actionDelegate onPressStart:YES];
    }
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    if (self.hidden) {
        return nil;
    }
    UIView *hitTestView = [super hitTest:point withEvent:event];
    return hitTestView;
}

#pragma mark - getter
- (UITapGestureRecognizer *)singleTap {
    if (!_singleTap) {
        _singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self
                                                             action:@selector(onSingleTapAction:)];
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
        _longPress.delegate = self;
        [self addGestureRecognizer:_longPress];
    }
    return _longPress;
}

@end
