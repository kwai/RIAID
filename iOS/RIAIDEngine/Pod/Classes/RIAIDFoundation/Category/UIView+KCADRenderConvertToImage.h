//
//  UIView+KCADRenderConvertToImage.h
//  KCADRender
//
//  Created by simon on 2021/12/4.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIView (KCADRenderConvertToImage)

/// 将当前视图转化为图片
- (UIImage *)convertToImage;

@end

NS_ASSUME_NONNULL_END
