//
//  UIView+KCADRenderConvertToImage.m
//  KCADRender
//
//  Created by simon on 2021/12/4.
//

#import "UIView+KCADRenderConvertToImage.h"

@implementation UIView (KCADRenderConvertToImage)

- (UIImage *)convertToImage {    
    CGSize size = self.layer.bounds.size;
    UIGraphicsBeginImageContextWithOptions(size, NO, [UIScreen mainScreen].scale);
    [self.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

@end
