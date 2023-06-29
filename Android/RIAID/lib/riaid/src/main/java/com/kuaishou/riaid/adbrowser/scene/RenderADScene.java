package com.kuaishou.riaid.adbrowser.scene;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.adbrowser.ADBrowserContext;
import com.kuaishou.riaid.proto.nano.ADSceneModel;
import com.kuaishou.riaid.proto.nano.Node;
import com.kuaishou.riaid.render.config.DSLRenderCreator;
import com.kuaishou.riaid.render.interf.IRealViewWrapper;
import com.kuaishou.riaid.render.util.ToolHelper;

/**
 * render渲染的广告场景
 * 视图是在场景可见时才会真正的渲染出来
 */
public class RenderADScene extends BaseADScene {

  @Nullable
  private DSLRenderCreator render;

  public RenderADScene(@NonNull ADBrowserContext context, @NonNull ADSceneModel ADSceneModel) {
    super(context, ADSceneModel);
  }

  @Override
  public void replaceRender(@NonNull DSLRenderCreator renderCreator, View view) {
    render = renderCreator;
    mSceneRenderView = view;
    mSceneContainer.removeAllViews();
  }

  @Override
  public void removeRender() {
    render = null;
    mSceneRenderView = null;
    mSceneContainer.removeAllViews();
  }

  @Override
  @Nullable
  public DSLRenderCreator getRenderCreator() {
    return render;
  }

  @Nullable
  @Override
  public Node getRenderData() {
    if (mADSceneModel.render != null &&
        mADSceneModel.render.renderData != null) {
      return mADSceneModel.render.renderData;
    }
    return null;
  }

  @Override
  @Nullable
  protected View createRenderView() {
    View renderView = null;

    Node nodeData = null;
    if (mADSceneModel.render != null &&
        mADSceneModel.render.renderData != null) {
      // 真正通过转过来的proto对象
      nodeData = mADSceneModel.render.renderData;
    }
    if (nodeData != null) {
      this.render = new DSLRenderCreator.Builder(mBrowserContext.getRenderService())
          .withPbData(nodeData)
          .withMaxWidth(mBrowserContext.getADCanvas().getCanvasWidth())
          .withMaxHeight(mBrowserContext.getADCanvas().getCanvasHeight())
          .build();
      renderView = this.render.render(mContext);
    }
    return renderView;
  }

  @Override
  public int getSceneMeasureWidth() {
    if (render == null) {
      // 渲染的视图如果为空，需要先创建
      tryCreateRenderView();
    }
    if (render == null || render.rootRender == null) {
      return 0;
    }
    return render.rootRender.getCurrentViewInfo().getRealViewSize().width;
  }

  @Override
  @Nullable
  public View findViewByKey(int viewKey) {
    if (render != null && render.rootRender != null) {
      IRealViewWrapper wrapper = ToolHelper.findViewByKey(viewKey, render.rootRender);
      if (wrapper != null) {
        return wrapper.getRealView();
      }
    }
    return null;
  }

  @Override
  @Nullable
  public IRealViewWrapper findViewWrapperByKey(int viewKey) {
    if (render != null && render.rootRender != null) {
      return ToolHelper.findViewByKey(viewKey, render.rootRender);
    }
    return null;
  }
}
