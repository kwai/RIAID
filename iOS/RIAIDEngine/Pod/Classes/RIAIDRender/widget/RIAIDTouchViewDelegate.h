//
//  RIAIDTouchViewDelegate.h
//  KCADRender
//
//  Created by simon on 2021/12/22.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol RIAIDTouchViewDelegate <NSObject>

@optional
- (void)onSingleTapAction;
- (void)onDoubleTapAction;
- (void)onLongPressAction;
- (void)onPressStart:(BOOL)bySelf;
- (void)onPressEnd:(BOOL)bySelf;

/// 触发来源是否来源于富文本
/// @param richText 富文本内容
/// @param range 富文本点击的范围
/// @discussion 用于解决子视图为 YYLabel 时，富文本的点击和手势冲突的问题
- (BOOL)haveHandlerWithRichText:(NSAttributedString *)richText textRange:(NSRange)range;

@end

NS_ASSUME_NONNULL_END
