//
//  KCADRenderRichTextWidget.h
//  KCADRender
//
//  Created by simon on 2021/12/4.
//

#import <UIKit/UIKit.h>

#import <YYText/YYText.h>
#import "RIAID.h"
#import "RIAIDRenderContext.h"

NS_ASSUME_NONNULL_BEGIN

@protocol KCADRenderRichLabelDelegate <NSObject>

/// 富文本渲染代理，需要外界对实现对 RenderInfo 的渲染
/// @param renderInfo 需要渲染的 Render 信息
/// @param estimateSize 富文本根据计算得出的预估尺寸
/// @return 返回真实的渲染信息
- (UIView *)renderViewWithInfo:(id)renderInfo estimateSize:(CGSize)estimateSize;

@end

@protocol RIAIDRRichLabelActionDelegate <NSObject>

- (void)longPressAction:(RIAIDHandler *)handler;
- (void)tapAction:(RIAIDHandler *)handler;

@end

/// ADRender 富文本组件
@interface KCADRenderRichTextWidget : NSObject

/// 富文本控件的尺寸
@property (nonatomic, assign, readonly) CGSize size;

@property (nonatomic, weak) id<RIAIDRRichLabelActionDelegate> actionDelegate;

/// 最终需要渲染到屏幕上的 label
@property (nonatomic, strong, readonly) YYLabel *richLabel;

- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithString:(NSString *)string
                  estimateSize:(CGSize)estimateSize
              renderLayoutSize:(CGSize)layoutSize
                         model:(RIAIDTextAttributes *)model
                      delegate:(id<KCADRenderRichLabelDelegate>)delegate
                       context:(RIAIDRenderContext *)context;

- (void)reloadWithWithText:(RIAIDAttributes *)textAttributes
              estimateSize:(CGSize)estimateSize
          renderLayoutSize:(CGSize)layoutSize;

/// 重新加载富文本颜色
/// @param textColor 新颜色
- (void)reloadTextColor:(NSString *)textColor;

/// 根据传入的 size 刷新文字布局
/// @param newSize 新的尺寸
- (void)reloadTextLayoutWithSize:(CGSize)newSize;

/// 根据富文本和点击范围确定是否有对应事件
/// @param text 富文本
/// @param range 点击范围
- (RIAIDHandler *)getHandlerWithText:(NSAttributedString *)text range:(NSRange)range;

@end

NS_ASSUME_NONNULL_END
