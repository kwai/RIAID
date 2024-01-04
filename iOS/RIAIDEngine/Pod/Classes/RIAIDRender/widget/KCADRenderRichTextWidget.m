//
//  KCADRenderRichTextWidget.m
//  KCADRender
//
//  Created by simon on 2021/12/4.
//

#import "KCADRenderRichTextWidget.h"

#pragma mark - utils
#import "UIView+KCADRenderConvertToImage.h"
#import "UIColor+KCADRenderHex.h"
#import "KCADRenderKBase.h"
#import "KCADRenderGeometryCalculate.h"
#import "RIAIDRDataBindingService.h"

static NSString * const kADRenderEllipsis = @"...";

#pragma mark - KCADRenderRichLabel
@interface KCADRenderRichTextWidget ()

@property (nonatomic, assign, readwrite) CGSize size;
@property (nonatomic, strong, readwrite) YYLabel *richLabel;
/// 尾部截断时需要显示的尾部富文本
@property (nonatomic, strong) NSMutableAttributedString *truncationToken;
@property (nonatomic, copy) NSAttributedString *lastRichString;
// 最终显示的富文本
@property (nonatomic, strong) NSMutableAttributedString *richString;

@property (nonatomic, copy) NSString *string;
// 父节点通过计算得出的预估宽高
@property (nonatomic, assign) CGSize estimateSize;
// render 信息中规定的 layout 样式
@property (nonatomic, assign) CGSize layoutSize;
@property (nonatomic, strong) RIAIDTextAttributes *attributedModel;
@property (nonatomic, weak) id<KCADRenderRichLabelDelegate> delegate;
/// key 为占位符在原始字符串中的位置，value 为占位符对应的 view 转化的 attributed string
@property (nonatomic, copy) NSDictionary<NSValue *, NSMutableAttributedString *> *placeholderDic;
@property (nonatomic, copy) NSDictionary<NSValue *, RIAIDHandler *> *handlerDic;
@property (nonatomic, strong) RIAIDRenderContext *context;

@end

@implementation KCADRenderRichTextWidget

- (instancetype)initWithString:(NSString *)string
                  estimateSize:(CGSize)estimateSize
              renderLayoutSize:(CGSize)layoutSize
                         model:(RIAIDTextAttributes *)model
                      delegate:(id<KCADRenderRichLabelDelegate>)delegate
                       context:(RIAIDRenderContext *)context {
    if (self = [super init]) {
        _string = string.copy;
        _estimateSize = estimateSize;
        _layoutSize = layoutSize;
        _attributedModel = model;
        _delegate = delegate;
        _context = context;
        _richLabel = [YYLabel new];
        [self drawText:NO];
    }
    return self;
}

- (void)reloadWithWithText:(RIAIDAttributes *)textAttributes
              estimateSize:(CGSize)estimateSize
          renderLayoutSize:(CGSize)layoutSize {
    if (textAttributes.hasText) {
        id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                    getServiceInstance:@protocol(RIAIDRDataBindingService)];
        NSString *string = [dataBinding parseHolderData:textAttributes.text.text];
        if (string.length > 0) {
            // 潜规则，不允许高亮改为空的文本
            self.string = string.copy;
        }
    }
    self.estimateSize = estimateSize;
    self.layoutSize = layoutSize;
    self.attributedModel = textAttributes.text;
    [self drawText:YES];
}

- (void)reloadTextColor:(NSString *)textColor {
    id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    [self.richLabel setTextColor:[UIColor riaid_colorWithHexString:[dataBinding parseHolderData:[KCADRenderKBase renderColorString:textColor]]]];
}

- (void)reloadTextLayoutWithSize:(CGSize)newSize {
    self.richLabel.textLayout = [self setupTextLayoutWithSize:newSize string:self.richString];
}

- (void)drawText:(BOOL)isReload {
    NSMutableAttributedString * richString = [self getRichString];
    // 预估高度，不管多行单行，有固定高度时设置固定高度，没固定高度时，根据规则给予预估高度。
    CGFloat stringHeight = self.layoutSize.height;
    if (isReload) {
        stringHeight = self.richLabel.frame.size.height;
    } else if (self.layoutSize.height == RIAIDRenderMatchParent) {
        stringHeight = self.estimateSize.height;
    } else if (self.layoutSize.height == RIAIDRenderWrapContent) {
        stringHeight = CGFLOAT_MAX;
    }
    // 预估的宽度
    CGFloat stringWidth = self.layoutSize.width;
    if (isReload) {
        stringWidth = self.richLabel.frame.size.width;
    } else if (self.layoutSize.width == RIAIDRenderMatchParent) {
        stringWidth = self.estimateSize.width;
    } else if (self.layoutSize.width == RIAIDRenderWrapContent) {
        if (self.attributedModel.maxLines.value == 1) {
            // 单行时，使用预估最大值
            stringWidth = CGFLOAT_MAX;
        } else {
            // 多行时，宽度使用预估宽度
            stringWidth = self.estimateSize.width;
        }
    }
    self.richLabel.textLayout = [self setupTextLayoutWithSize:CGSizeMake(stringWidth, stringHeight)
                                                       string:richString];
    if (!isReload) {
        // 若为首次渲染，则需要计算最终渲染的宽高
        [self layoutRichLabelWithTextLayout:self.richLabel.textLayout];
    }
    [self setupLabelAfterAttributed];
}

- (YYTextLayout *)setupTextLayoutWithSize:(CGSize)textSize string:(NSAttributedString *)richString {
    // 计算富文本的宽高
    YYTextContainer *textContainer = [YYTextContainer containerWithSize:textSize];
    textContainer.maximumNumberOfRows = self.attributedModel.maxLines.value;
    // 设置文本截断方式
    YYTextTruncationType truncationType = YYTextTruncationTypeNone;
    switch (self.attributedModel.ellipsize) {
        case RIAIDTextAttributes_Ellipsize_EllipsizeStart: {
            truncationType = YYTextTruncationTypeStart;
        } break;
        case RIAIDTextAttributes_Ellipsize_EllipsizeEnd: {
            truncationType = YYTextTruncationTypeEnd;
        } break;
        case RIAIDTextAttributes_Ellipsize_EllipsizeMiddle: {
            truncationType = YYTextTruncationTypeMiddle;
        } break;
        case RIAIDTextAttributes_Ellipsize_EllipsizeUnknown:
        default: {
            truncationType = YYTextTruncationTypeNone;
        } break;
    }
    textContainer.truncationType = truncationType;
    if (self.attributedModel.ellipsize == RIAIDTextAttributes_Ellipsize_EllipsizeEnd) {
        textContainer.truncationToken = self.truncationToken;
    }
    YYTextLayout *textLayout = [YYTextLayout layoutWithContainer:textContainer text:richString];
    return textLayout;
}

// 计算 label 的最后尺寸
- (void)layoutRichLabelWithTextLayout:(YYTextLayout *)textLayout {
    // 计算最终渲染的宽高
    CGFloat sizeWidth = self.layoutSize.width,
            sizeHeight = self.layoutSize.height;
    if (self.layoutSize.width == RIAIDRenderMatchParent) {
        sizeWidth = self.estimateSize.width;
    } else if (self.layoutSize.width == RIAIDRenderWrapContent) {
        // 自适应也不应该超过约束的大小
        sizeWidth = MIN(textLayout.textBoundingSize.width, self.estimateSize.width);
    }
    if (self.layoutSize.height == RIAIDRenderMatchParent) {
        sizeHeight = self.estimateSize.height;
    } else if (self.layoutSize.height == RIAIDRenderWrapContent) {
        sizeHeight = textLayout.textBoundingSize.height;
    }
    self.richLabel.frame = CGRectMake(0, 0, sizeWidth, sizeHeight);
    self.size = self.richLabel.frame.size;
}

// 配置需要在尺寸计算后设置的属性
- (void)setupLabelAfterAttributed {
    // 水平对齐方式
    switch ([KCADRenderKBase renderTextHorizontalAlign:self.attributedModel.align]) {
        case RIAIDTextAttributes_Align_Horizontal_HorizontalEnd: {
            self.richLabel.textAlignment = NSTextAlignmentRight;
            // 解决 yylabel 进行动画时会重绘的问题
            self.richLabel.contentMode = UIViewContentModeRight;
        } break;
        case RIAIDTextAttributes_Align_Horizontal_HorizontalCenter: {
            self.richLabel.textAlignment = NSTextAlignmentCenter;
            // 解决 yylabel 进行动画时会重绘的问题
            self.richLabel.contentMode = UIViewContentModeCenter;
        } break;
        case RIAIDTextAttributes_Align_Horizontal_HorizontalStart:
        default: {
            self.richLabel.textAlignment = NSTextAlignmentLeft;
            // 解决 yylabel 进行动画时会重绘的问题
            self.richLabel.contentMode = UIViewContentModeLeft;
        } break;
    }
    // 垂直对齐方式
    switch ([KCADRenderKBase renderTextVerticalAlign:self.attributedModel.align]) {
        case RIAIDTextAttributes_Align_Vertical_VerticalBottom: {
            self.richLabel.textVerticalAlignment = YYTextVerticalAlignmentBottom;
        } break;
        case RIAIDTextAttributes_Align_Horizontal_HorizontalCenter: {
            self.richLabel.textVerticalAlignment = YYTextVerticalAlignmentCenter;
        } break;
        case RIAIDTextAttributes_Align_Horizontal_HorizontalStart:
        default: {
            self.richLabel.textVerticalAlignment = YYTextVerticalAlignmentTop;
        } break;
    }
    // 设置事件
    [self handleActionWithRichString:self.richLabel.textLayout.text.mutableCopy];
}

/// 生成富文本文案
///
/// 这里不用 getter 是因为每次调用需要重新根据数据来更新
- (NSMutableAttributedString *)getRichString {
    NSArray *rangeArray = [self sortRangeArray];
    // 根据范围数组，拼接字符串
    NSMutableAttributedString *resultString = [NSMutableAttributedString new];
    // 每次循环计算时的起始位置
    NSUInteger startLocation = 0;
    // 富文本拼接方式：遍历字符串，将字符串划分为多个 [normal + rich]，每次遍历富文本 key 进行拼接，最后一个 key 时，判断是否为字符串的
    // 尾部，如不是尾部，则剩余部分为非富文本格式，直接进行拼接即可
    NSInteger i = 0;
    for (NSValue *rangeValue in rangeArray) {
        NSRange richRange = [rangeValue rangeValue];
        // 不需要富文本渲染的范围 super start
        if (richRange.location > startLocation) {
            NSRange normalRange = (NSRange){startLocation, richRange.location - startLocation};
            NSString *normalString = [self.string substringWithRange:normalRange];
            // 拼接普通字符串部分
            [resultString appendAttributedString:[self generateNormalAttributedString:normalString]];
        }
        // 拼接富文本部分
        [resultString appendAttributedString:self.placeholderDic[rangeValue]];
        startLocation = richRange.location + richRange.length;
        i++;
    }
    // 最后一个 key，处理字符串尾部
    NSInteger trailLength = self.string.length - startLocation;
    if (trailLength > 0) {
        NSString *normalString = [self.string substringWithRange:(NSRange){startLocation, trailLength}];
        [resultString appendAttributedString:[self generateNormalAttributedString:normalString]];
    } else {
        NSValue *lastRichRangeValue = rangeArray.lastObject;
        NSMutableAttributedString *lastRichString = self.placeholderDic[lastRichRangeValue];
        if (lastRichString) {
            self.truncationToken = [[NSMutableAttributedString alloc] initWithString:kADRenderEllipsis];
            id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                        getServiceInstance:@protocol(RIAIDRDataBindingService)];
            self.truncationToken.yy_color = [UIColor riaid_colorWithHexString:[dataBinding parseHolderData:[KCADRenderKBase
                                                                                                            renderTextColorString:self.attributedModel.fontColor]]];
            [self.truncationToken appendAttributedString:lastRichString];
        }
    }
    resultString.yy_lineSpacing = self.attributedModel.lineSpace.value;
    self.richString = resultString;
    return resultString;
}

// 对占位符字典的 keys 进行排序，得到顺序范围数组
- (NSArray *)sortRangeArray {
    [self setupRichListArray];
    NSArray *rangeArray = [self.placeholderDic.allKeys
                           sortedArrayUsingComparator:^NSComparisonResult(NSValue *obj1, NSValue *obj2) {
        NSRange range1 = [obj1 rangeValue], range2 = [obj2 rangeValue];
        NSComparisonResult result = [[NSNumber numberWithInteger:range1.location]
                                     compare:[NSNumber numberWithInteger:range2.location]];
        if (result == NSOrderedAscending) {
            return NSOrderedAscending;
        } else {
            return NSOrderedDescending;
        }
    }];
    if (rangeArray.lastObject) {
        self.lastRichString = self.placeholderDic[rangeArray.lastObject];
    }
    return rangeArray;
}

/// 处理富文本列表，将其放入富文本字典和事件字典中
- (void)setupRichListArray {
    NSMutableDictionary *placeholderDic = [NSMutableDictionary dictionary];
    NSMutableDictionary *handlerDic = [NSMutableDictionary dictionary];
    for (RIAIDTextAttributes_RichText *model in self.attributedModel.richListArray) {
        if ([self.delegate respondsToSelector:@selector(renderViewWithInfo:estimateSize:)]) {
            UIView *renderView = [self.delegate renderViewWithInfo:model.content
                                                      estimateSize:CGSizeMake(CGFLOAT_MAX, CGFLOAT_MAX)];
            UIImage *image = [renderView convertToImage];
            NSRange placeholderRange = [self.string rangeOfString:model.placeHolder];
            if (placeholderRange.location != NSNotFound
                && image) {
                CGFloat fontSize = [KCADRenderKBase renderFontSize:self.attributedModel.fontSize.value];
                NSString *fontName = self.attributedModel.fontName;
                UIFont *font = [UIFont systemFontOfSize:fontSize];
                if (self.attributedModel.fontName.length > 0) {
                    font = [UIFont fontWithName:fontName size:fontSize];
                }
                if (self.attributedModel.bold.value && self.attributedModel.tilt.value) {
                    font = YYTextFontWithBoldItalic(font);
                } else {
                    if (self.attributedModel.bold.value) {
                        font = YYTextFontWithBold(font);
                    }
                    if (self.attributedModel.tilt.value) {
                        font = YYTextFontWithItalic(font);
                    }
                }
                YYTextVerticalAlignment alignment = YYTextVerticalAlignmentCenter;
                switch ([KCADRenderKBase richTextAlign:model]) {
                    case RIAIDTextAttributes_RichText_RichAlign_RichAlignCenter: {
                        alignment = YYTextVerticalAlignmentCenter;
                    } break;
                    case RIAIDTextAttributes_RichText_RichAlign_RichAlignBottom: {
                        alignment = YYTextVerticalAlignmentBottom;
                    } break;
                    default: {
                        alignment = YYTextVerticalAlignmentTop;
                    } break;
                }
                NSMutableAttributedString *imageAttachment = [NSMutableAttributedString
                                                              yy_attachmentStringWithContent:image
                                                              contentMode:UIViewContentModeCenter
                                                              attachmentSize:image.size
                                                              alignToFont:font
                                                              alignment:alignment];
                if (imageAttachment) {
                    [placeholderDic setObject:imageAttachment
                                       forKey:[NSValue valueWithRange:placeholderRange]];
                }
                if (model.hasHandler && model.handler) {
                    [handlerDic setObject:model.handler
                                   forKey:[NSValue valueWithRange:placeholderRange]];
                }
            }
        }
    }
    self.placeholderDic = placeholderDic.copy;
    self.handlerDic = handlerDic.copy;
}

- (void)handleActionWithRichString:(nullable NSMutableAttributedString *)richString {
    __weak typeof(self) weakSelf = self;
    self.richLabel.textTapAction = ^(UIView * _Nonnull containerView, NSAttributedString * _Nonnull text, NSRange range, CGRect rect) {
        RIAIDHandler *hander = [weakSelf getHandlerWithText:text range:range];
        if (hander.hasClick) {
            if ([weakSelf.actionDelegate respondsToSelector:@selector(tapAction:)]) {
                [weakSelf.actionDelegate tapAction:hander];
            }
        }
    };
    self.richLabel.textLongPressAction = ^(UIView * _Nonnull containerView, NSAttributedString * _Nonnull text, NSRange range, CGRect rect) {
        RIAIDHandler *hander = [weakSelf getHandlerWithText:text range:range];
        if (hander.hasLongPress) {
            if ([weakSelf.actionDelegate respondsToSelector:@selector(longPressAction:)]) {
                [weakSelf.actionDelegate longPressAction:hander];
            }
        }
    };
}

- (RIAIDHandler *)getHandlerWithText:(NSAttributedString *)text range:(NSRange)range {
    __block NSAttributedString *targetText = self.lastRichString;
    if (text.length > 0
        && range.location != NSNotFound
        && (range.length + range.location) <= text.length) {
        targetText = [text attributedSubstringFromRange:NSMakeRange(range.location, 1)];
    }
    for (NSValue *rangeValue in self.placeholderDic.allKeys) {
        NSAttributedString *attributedString = self.placeholderDic[rangeValue];
        YYTextAttachment *targetAttachment = targetText.yy_attributes[YYTextAttachmentAttributeName];
        YYTextAttachment *currentAttachment = attributedString.yy_attributes[YYTextAttachmentAttributeName];
        if ([targetAttachment isEqual:currentAttachment]) {
            return self.handlerDic[rangeValue];
        }
    }
    return nil;
}

// 生成普通的富文本字符串
- (NSMutableAttributedString *)generateNormalAttributedString:(NSString *)string {
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:string ?: @""];
    // 字体处理
    CGFloat fontSize = [KCADRenderKBase renderFontSize:self.attributedModel.fontSize.value];
    NSString *fontName = self.attributedModel.fontName;
    UIFont *font = [UIFont systemFontOfSize:fontSize];
    if (self.attributedModel.fontName.length > 0) {
        font = [UIFont fontWithName:fontName size:fontSize];
    }
    if (self.attributedModel.bold.value && self.attributedModel.tilt.value) {
        font = YYTextFontWithBoldItalic(font);
    } else {
        if (self.attributedModel.bold.value) {
            font = YYTextFontWithBold(font);
        }
        if (self.attributedModel.tilt.value) {
            font = YYTextFontWithItalic(font);
        }
    }
    [attributedString addAttribute:NSFontAttributeName
                             value:font
                             range:NSMakeRange(0, attributedString.length)];
    // 行数处理
    if (self.attributedModel.lineSpace > 0) {
        NSMutableParagraphStyle * paragraphStyle = [[NSMutableParagraphStyle alloc] init];
        [paragraphStyle setLineSpacing:self.attributedModel.lineSpace.value];
        [attributedString addAttribute:NSParagraphStyleAttributeName
                                 value:paragraphStyle
                                 range:NSMakeRange(0, attributedString.length)];
    }
    // 字体颜色
    id<RIAIDRDataBindingService> dataBinding = [self.context.serviceContainer
                                                getServiceInstance:@protocol(RIAIDRDataBindingService)];
    attributedString.yy_color = [UIColor riaid_colorWithHexString:[dataBinding parseHolderData:[KCADRenderKBase
                                                                                                renderTextColorString:self.attributedModel.fontColor]]];
    // 划线处理
    YYTextDecoration *textDecoration = [YYTextDecoration decorationWithStyle:YYTextLineStyleThick];
    switch (self.attributedModel.lineMode) {
        case RIAIDTextAttributes_LineMode_LineModeStrikeThru: {
            attributedString.yy_textStrikethrough = textDecoration;
        } break;
        case RIAIDTextAttributes_LineMode_LineModeUnderline: {
            [attributedString setYy_textUnderline:textDecoration];
        } break;
        case RIAIDTextAttributes_LineMode_LineModeNormal:
        default:
            break;
    }
    
    return attributedString;
}

@end
