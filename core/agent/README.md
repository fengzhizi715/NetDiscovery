
如果需要使用的话，先启动agent模块。

在工程中，agent模块先导出一个fat jar，然后使用如下的命令启动：java -jar agent-all.jar

然后在 spiderEngine 需要打开监控：spiderEngine.setUseMonitor(true);

这样才能使用 dashboard。