# 发布流程:
- 检查主体域名是否正确
- 检查SDK版本号
- 是否去掉LOG开关
- 默认广告位配置是否正确
- 是否能正常上报(请求、展示、点击、曝光等)
- 每次灰度的广告位更换
- 热更新是否正常
- 接口是正式接口而非测试接口
- 广点通、百度、穿山甲SDK是否升级，如果升级，下游提示升级
- 发布完成，提交gitlab并标记版本号和本次优化内容

# 开发者版本
- 有妖气漫画     39000
- 贵州通         38000
- 热门免费小说   37000
- 小书亭         36000
- 中华传统万年历 35000
- neets壁纸      34000
- 美剧控社区     33000
- 日剧吧         31000
- 泰剧迷         31000
- 韩剧TV社区版   30000
- 韩剧TV         29000
- 日剧屋         28000
- 美剧社         27000
- 泰剧社         26000
- 儿歌点点       25001
- 影音先锋       24001
- 出国翻译官     23001
- 托卡世界       22001
- neets          21000
- CAD看图王      20003
- 波波视频       19002
- 飞读           18000
- 淘新闻         17000
- 海报           16000
- 追更小说       15000
- 迅雷聚合       14000
- 桃小橙         13000
- 朝夕           12000
- 花生           11000
- 迅雷           10000


# 版本发布2.0流程
- 新建开发者版本 1000 递增
- 在sdk-3rd-config中创建配置文件 -- 修改对应的配置
- 根据需求更改线上会在预上线域名
- 在setting.gradle中添加配置文件
- 打包 -- sdk_genAAR
- 在app下的build.gradle中应用对应的版本号
- 运行到手机上成功显示广告
- 查看配置是否正确
- 下载官方的APP 抓取开屏页和主页 配置到SPLASH_ACTIVITY_NAME - MAIN_ACTIVITY_NAME 重新打包
- 修改桌面demo配置，将打好的aar包,SDK使用说明压缩，发布
- 发布完成，提交gitlab并标记版本号和本次优化内容
- 将发布包的版本号，混淆对应文件，压缩包保存到D盘下的Released文件夹中