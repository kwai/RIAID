//
//  KCADRenderTextNode.h
//  KCADRender
//
//  Created by simon on 2021/11/13.
//

#import "KCADRenderItemNode.h"

#import "KCADRenderRichTextWidget.h"

NS_ASSUME_NONNULL_BEGIN

@interface KCADRenderTextNode : KCADRenderItemNode

@property (nonatomic, strong, readonly) KCADRenderRichTextWidget *richTextWidget;

@end

NS_ASSUME_NONNULL_END
