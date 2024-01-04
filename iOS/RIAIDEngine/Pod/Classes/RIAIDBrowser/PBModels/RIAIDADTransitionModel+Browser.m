//
//  RIAIDADTransitionModel+Browser.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/20.
//

#import "RIAIDADTransitionModel+Browser.h"

@implementation RIAIDADTransitionModel (Browser)

- (RIAIDADTransitionType)transitionType {
    if (self.hasVisibility) {
        return RIAIDADTransitionTypeVisibility;
    }
    
    if (self.hasTemplate_p) {
        return RIAIDADTransitionTypeTemplate;
    }
    
    if (self.hasTranslation) {
        return RIAIDADTransitionTypeTranslation;
    }
    
    if (self.hasInSceneAnimation) {
        return RIAIDADTransitionTypeInSceneAnimation;
    }
    
    if (self.hasSceneShare) {
        return RIAIDADTransitionTypeSceneShare;
    }
    
    if (self.hasLottie) {
        return RIAIDADTransitionTypeLottie;
    }
    
    if (self.hasRenderContent) {
        return RIAIDADTransitionTypeRenderContent;
    }
    
    return RIAIDADTransitionTypeUnknown;
}

@end
