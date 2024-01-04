//
//  RIAIDBrowserDirector+Layout.m
//  KCADBrowser
//
//  Created by liweipeng on 2021/12/19.
//

#import "RIAIDBrowserDirector+Layout.h"
#import "NSNumber+RIAIDBToString.h"

@implementation RIAIDBrowserDirector (Layout)

- (void)addLayout:(RIAIDADSceneRelationModel*)layoutModel {
    RIAIDBSceneController *sourceSceneCtrl = [self sceneCtrlForKey:layoutModel.sourceKey];
    
    if ((layoutModel.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Start || layoutModel.targetEdge == RIAIDADSceneRelationModel_SceneEdge_End) && (layoutModel.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Start || layoutModel.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_End)) {
        sourceSceneCtrl.xRelation = layoutModel;
    }
    
    if ((layoutModel.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Top || layoutModel.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom) && (layoutModel.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Top || layoutModel.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom)) {
        sourceSceneCtrl.yRelation = layoutModel;
    }
}

- (void)executeAllLayout {
    [self cleanAllLayouts];
    [self calculateAllLayouts];
    [self applyAllLayouts];
}

/// 计算viewMap中所有view的布局
- (void)calculateAllLayouts {
    NSMutableArray<NSString*> *calculatingSceneKeys = self.sceneCtrlMap.allKeys.mutableCopy;
    
    while (calculatingSceneKeys.count > 0) {
        NSMutableArray<NSString*> *calculatedSceneKeys = @[].mutableCopy;
        
        for (NSString *sceneKey in calculatingSceneKeys) {
            RIAIDBSceneController *sourceSceneCtrl = [self.sceneCtrlMap objectForKey:sceneKey];
            [self calculateLayoutForScene:sourceSceneCtrl];

            if (sourceSceneCtrl.originXValue && sourceSceneCtrl.originYValue) {
                [calculatedSceneKeys addObject:sceneKey];
            }
        }
        
        if (calculatedSceneKeys.count == 0) {
            NSAssert(NO, @"存在无法计算的布局");
        }
        
        [calculatingSceneKeys removeObjectsInArray:calculatedSceneKeys];
    }
}

/// 清空所有view中存储的origin属性
- (void)cleanAllLayouts {
    for (RIAIDBSceneController *sceneCtrl in self.sceneCtrlMap.allValues) {
        sceneCtrl.originYValue = nil;
        sceneCtrl.originXValue = nil;
    }
}

/// 应用所有已经计算的布局
- (void)applyAllLayouts {
    for (RIAIDBSceneController *sceneCtrl in self.sceneCtrlMap.allValues) {
        [sceneCtrl applyCurrentLayout];
    }
}

/// 基于xRelation和yRelation, 计算Scene的origin，并存储到SceneController中
/// 如果relation的target是画布，可以直接计算
/// 如果relation的target是render，则需要target对应方向的origin已经计算出来
- (void)calculateLayoutForScene:(RIAIDBSceneController*)sourceSceneCtrl {
    UIView *canvas = (UIView*)self.canvas;
    
    CGFloat canvasWidth = canvas.bounds.size.width;
    CGFloat canvasHeight = canvas.bounds.size.height;

    CGFloat sourceViewWidth = [sourceSceneCtrl expectSize].width;
    CGFloat sourceViewHeight = [sourceSceneCtrl expectSize].height;
    
    if (sourceSceneCtrl.xRelation) {
        RIAIDBSceneController *targetSceneCtrl = [self sceneCtrlForKey:sourceSceneCtrl.xRelation.targetKey];
        CGFloat targetWidth = [targetSceneCtrl expectSize].width;
        
        CGFloat calculateDistance = [RIAIDBrowserDirector calculateDistance:sourceSceneCtrl.xRelation];
        
        if (sourceSceneCtrl.xRelation.targetKey == RIAIDSystemKeyEnum_SystemKeys_SceneKeyCanvas) {
            if (sourceSceneCtrl.xRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Start) {
                // sourceEdge == start
                
                // targetEdge == start
                if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Start) {
                    CGFloat originX = calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                } else if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_End) {
                    // targetEdge == end
                    CGFloat originX = canvasWidth + calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                }
            } else if (sourceSceneCtrl.xRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_End) {
                // sourceEdge == end
                
                // targetEdge == start
                if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Start) {
                    CGFloat originX = -sourceViewWidth + calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                } else if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_End) {
                    // targetEdge == end
                    CGFloat originX = canvasWidth - sourceViewWidth + calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                }
            }
        } else if (targetSceneCtrl.originXValue) {
            if (sourceSceneCtrl.xRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Start) {
                // sourceEdge == start
                
                // targetEdge == start
                if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Start) {
                    CGFloat originX = targetSceneCtrl.originXValue.floatValue + calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                } else if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_End) {
                    // targetEdge == end
                    CGFloat originX = targetSceneCtrl.originXValue.floatValue + targetWidth + calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                }
            } else if (sourceSceneCtrl.xRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_End) {
                // sourceEdge == end
                
                // targetEdge == start
                if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Start) {
                    CGFloat originX = targetSceneCtrl.originXValue.floatValue - sourceViewWidth + calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                } else if (sourceSceneCtrl.xRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_End) {
                    // targetEdge == end
                    CGFloat originX = targetSceneCtrl.originXValue.floatValue + targetWidth - sourceViewWidth + calculateDistance;
                    sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
                }
            }
        }
    } else {
        // 没有relation，默认居中
        CGFloat originX = (canvasWidth - sourceViewWidth)/2;
        sourceSceneCtrl.originXValue = [NSNumber numberWithFloat:originX];
    }
    
    if (sourceSceneCtrl.yRelation) {
        RIAIDBSceneController *targetSceneCtrl = [self sceneCtrlForKey:sourceSceneCtrl.yRelation.targetKey];
        CGFloat targetHeight = [targetSceneCtrl expectSize].height;
        
        CGFloat calculateDistance = [RIAIDBrowserDirector calculateDistance:sourceSceneCtrl.yRelation];
        if (sourceSceneCtrl.yRelation.targetKey == RIAIDSystemKeyEnum_SystemKeys_SceneKeyCanvas) {
            if (sourceSceneCtrl.yRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Top) {
                // sourceEdge == top
                
                // targetEdge == top
                if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Top) {
                    CGFloat originY = calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                } else if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom) {
                    // targetEdge == bottom
                    CGFloat originY = canvasHeight + calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                }
            } else if (sourceSceneCtrl.yRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom) {
                // sourceEdge == bottom
                
                // targetEdge == top
                if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Top) {
                    CGFloat originY = -sourceViewHeight + calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                } else if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom) {
                    // targetEdge == bottom
                    CGFloat originY = canvasHeight - sourceViewHeight + calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                }
            }
        } else if (targetSceneCtrl.originYValue) {
            if (sourceSceneCtrl.yRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Top) {
                // sourceEdge == top
                
                // targetEdge == top
                if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Top) {
                    CGFloat originY = targetSceneCtrl.originYValue.floatValue + calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                } else if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom) {
                    // targetEdge == bottom
                    CGFloat originY = targetSceneCtrl.originYValue.floatValue + targetHeight + calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                }
            } else if (sourceSceneCtrl.yRelation.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom) {
                // sourceEdge == bottom
                
                // targetEdge == top
                if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Top) {
                    CGFloat originY = targetSceneCtrl.originYValue.floatValue - sourceViewHeight + calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                } else if (sourceSceneCtrl.yRelation.targetEdge == RIAIDADSceneRelationModel_SceneEdge_Bottom) {
                    // targetEdge == bottom
                    CGFloat originY = targetSceneCtrl.originYValue.floatValue + targetHeight - sourceViewHeight + calculateDistance;
                    sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
                }
            }
        }
    } else {
        // 没有relation，默认居中
        CGFloat originY = (canvasHeight - sourceViewHeight)/2;
        sourceSceneCtrl.originYValue = [NSNumber numberWithFloat:originY];
    }
}

/// 用以获取scenekey对应的view
- (RIAIDBSceneController*)sceneCtrlForKey:(int32_t)sceneKey {
    return [self.sceneCtrlMap objectForKey:@(sceneKey).riaidIntString];
}

/// 转换下发的distance
+ (CGFloat)calculateDistance:(RIAIDADSceneRelationModel*)layoutModel {
    // 左，上 两种约束，distance可以直接用
    if (layoutModel.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Start || layoutModel.sourceEdge == RIAIDADSceneRelationModel_SceneEdge_Top) {
        return layoutModel.distance;
    }
    
    // 右，下 两种约束，distance取负数用
    return (layoutModel.distance * -1);
}

@end
