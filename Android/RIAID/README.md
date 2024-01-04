# RIAID-Android工程
Android的所有的RIAID相关的源码，包含了RIAID引擎源码，RIAID生产线和RIAID的示例。
## RIAID引擎源码
路径：Android/RIAID/lib/riaid
- 实现了自研的布局和绘制，其中布局层级整个是打平的，提高了绘制效率
- 实现了自研的逻辑控制，通过各种触发器和行为执行器，能实现一定的丰富的交互能力和动画能力

## RIAID生产线
- 可以通过工程化的形式来生产RIAID数据
- 通过编写Java程序的形式，把RiaidModel通过RIAIDFactory生产出来，所有的对象都由程序初始化、组装和构建，最终生产出一个RiaidModel

> 作者：孙弘法、鞠达豪