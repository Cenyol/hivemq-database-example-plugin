# hivemq-database-example-plugin
## 【过程记录】HiveMQ集成MySQL

背景：最近在折腾MQTT相关，主要需求是想为一堆传感器搭建一个连接服务器，了解了相关信息之后选用了MQTT。一开始是使用Mosquitto，eclipse下的一个开源项目，除了不能连接MySQL之外，其他都挺好。我能怎么办，我也很绝望啊。然后忍痛割爱，记得在谷歌的过程中，看到有人建议用HiveMQ，说是可以使用相关的数据库插件来讲信息保存起来，嗯然后就开始去折腾HiveMQ了。

进入官网，经过一番折腾，具体弯路就不说了，提供几个指导，免去亲爱的你走弯路：
1、http://www.hivemq.com/resources/tutorials/ ，官网的视频是放在YouTube的，考虑到你可能没翻墙，所以我特地下载下来，又传到bilibili上了，这是观看链接：。

2、插件开发手册，建议看：http://www.hivemq.com/docs/plugins/latest/

3、这是官网在github上面的插件库，可以下载第1点里面提到的那个插件来跟着视频玩玩。

对了，我们这里玩的是MQTT服务器端的功能代码，所以前提你得有个MQTT客户端的小玩意，设置好服务器的ip和port，启动它，然后进行测试嘛。至于mqtt的client其实你谷歌一下就一大堆的，这里我也顺便放上我自己测试用的client，是eclipse的paho，java版本的，看这里：https://github.com/Cenyol/MQTTClient。