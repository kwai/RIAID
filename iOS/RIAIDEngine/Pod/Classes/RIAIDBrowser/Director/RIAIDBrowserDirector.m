//
//  KCADBLayoutExecutor.m
//  KCADBLayoutExecutor
//
//  Created by liweipeng on 2021/12/1.
//

#import "RIAIDBrowserDirector.h"
#import "NSNumber+RIAIDBToString.h"
#import "RIAIDDebugConfig.h"
#import "RIAIDBrowserDirector+Layout.h"
#import "RIAIDBrowserCanvasView.h"

@interface RIAIDBrowserDirector()<RIAIDBrowserCanvasViewDelegate>

/// ADRender需要的上下文对象
@property (nonatomic, strong) RIAIDRenderContext *context;
/// 存储所有RIAIDBSceneController的哈希表
@property (nonatomic, strong, readwrite) NSMutableDictionary<NSString*, RIAIDBSceneController*> *sceneCtrlMap;
/// 画布对象
@property (nonatomic, strong, readwrite) RIAIDBrowserCanvasView *canvas;
/// 记录画布大小，画布大小若发生变化，需要重绘Render并重新计算布局
@property (nonatomic, assign) CGSize canvasSize;
@end

@implementation RIAIDBrowserDirector

- (instancetype)initWithCanvas:(RIAIDBrowserCanvasView *)canvas context:(RIAIDRenderContext*)context {
    self = [self init];
    if (self) {
        self.context = context;
        self.canvas = canvas;
        self.canvas.delegate = self;
    }
    return self;
}

- (void)registerSceneCtrl:(RIAIDBSceneController*)sceneCtrl forSceneKey:(int32_t)sceneKey {
    [self.sceneCtrlMap setObject:sceneCtrl forKey:@(sceneKey).riaidIntString];
}

/// 当画布大小发生变化时，调用此方法对Render重绘
- (void)resizeRenders {
    for (RIAIDBSceneController *controller in self.sceneCtrlMap.allValues) {
        [controller resizeRender:self.canvas.bounds.size];
    }
}

- (nullable KCADRender*)findRenderByViewKey:(int32_t)viewKey {
    for (RIAIDBSceneController *sceneController in self.sceneCtrlMap.allValues) {
        KCADRenderNode *node = [sceneController.adRender findRenderNodeByKey:viewKey];
        if (node) {
            return sceneController.adRender;
        }
    }
    return nil;
}

#pragma mark - Lazy init
- (NSMutableDictionary<NSString *,RIAIDBSceneController *> *)sceneCtrlMap {
    if (!_sceneCtrlMap) {
        _sceneCtrlMap = [NSMutableDictionary dictionary];
    }
    return _sceneCtrlMap;
}

#pragma mark - Setter
- (void)setCanvasSize:(CGSize)canvasSize {
    if (!CGSizeEqualToSize(_canvasSize, canvasSize)) {
        _canvasSize = canvasSize;
        [self resizeRenders];
        [self executeAllLayout];
    }
}

#pragma mark - RIAIDBrowserCanvasViewDelegate
/// 每当Canvas自身的LayoutSubviews:被触发，会调用此方法
- (void)canvasDidLayoutSubviews:(RIAIDBrowserCanvasView *)canvas {
    self.canvasSize = canvas.bounds.size;
}

#pragma mark - Debug
- (void)addDebugMaskForView:(UIView*)view sceneKey:(int32_t)sceneKey {
    if (![RIAIDDebugConfig allow:RIAIDDebugTypeRenderMask]) {
        return;
    }
    
    UILabel *debugLabel = [UILabel new];
    debugLabel.backgroundColor = [[UIColor redColor] colorWithAlphaComponent:0.2];
    debugLabel.textAlignment = NSTextAlignmentCenter;
    debugLabel.textColor = [UIColor blueColor];
    debugLabel.text = [NSString stringWithFormat:@"SceneKey: %d", sceneKey];
    debugLabel.frame = view.bounds;
    [view addSubview:debugLabel];
}
@end
