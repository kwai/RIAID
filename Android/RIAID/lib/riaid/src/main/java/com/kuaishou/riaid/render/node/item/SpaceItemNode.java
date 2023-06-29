package com.kuaishou.riaid.render.node.item;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuaishou.riaid.render.model.UIModel;
import com.kuaishou.riaid.render.node.item.base.AbsItemNode;
import com.kuaishou.riaid.render.util.LayoutPerformer;

/**
 * 这个充当弹簧
 */
public class SpaceItemNode extends AbsItemNode<UIModel.Attrs> {

  public SpaceItemNode(@NonNull NodeInfo<UIModel.Attrs> nodeInfo) {
    super(nodeInfo);
  }

  @Override
  public void loadAttributes() {
    // Space不会参与绘制，当然也不用bindData
  }

  @Nullable
  @Override
  protected View getItemRealView() {
    return null;
  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {
    size.width = LayoutPerformer.getSideValueByMode(
        mNodeInfo.layout.width, mNodeInfo.layout.width, widthSpec);
    size.height = LayoutPerformer.getSideValueByMode(
        mNodeInfo.layout.height, mNodeInfo.layout.height, heightSpec);
    // 再通过最大尺寸来约束一下
    size.width = LayoutPerformer.getSizeByMax(size.width, mNodeInfo.layout.maxWidth);
    size.height = LayoutPerformer.getSizeByMax(size.height, mNodeInfo.layout.maxHeight);
  }
}
