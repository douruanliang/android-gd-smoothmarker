# 基于高德地图多小车实时移动demo

## 概述

       本demo模拟实时向List添加坐标点,MovingPointOverlay 根据自己绑定的坐标点，向前移动，根据实际需求会有CarId作为
     Map的Key,因此支持多小车移动。

## 架构设计

   项目结构

   app
     |
     |_ _ _ 负责业务

   app-lib-soomthmarker
     |
     |_ _ _ 封装导航、地图相关模块工程


## 设计说明

   ### 1 定容量，平滑移动 （10个坐标点，用一秒完成）


   ### 2 定时间，实时移动 （1秒走完，容器内的坐标,容器大小根据数据源吐数据的频率，动态添加）


## 核心说明

   提个问题

   项目代码中SMManager的初始化为啥要放到Activity声明周期之onResume中

   代码中有注释，需要帮助的，请联系我  1141851728（QQ）

```

    
    
    
    
