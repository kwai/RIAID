//
//  KCADRenderLayout.m
//  KCADRender
//
//  Created by simon on 2021/11/29.
//

#import "KCADRenderLayout.h"

@implementation KCADRenderLayout

+ (void)addTargetView:(RIAIDDecorViewWidget *)targetView toDecorView:(UIView *)decorView withAbsoluteFrame:(CGRect)frame {
    if (targetView && decorView) {
        targetView.frame = frame;
        [self parentView:decorView addSubView:targetView.getDecorView];
    }
}

+ (void)parentView:(UIView *)parentView addSubView:(UIView *)subView {
    BOOL isAdded = [parentView.subviews containsObject:subView];
    if (!isAdded) {
        [parentView addSubview:subView];
    }
}

@end
