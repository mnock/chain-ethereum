以太坊核心代码实现

#编译jar包
gradle build -x test

#运行jar包，可以设置启动参数
java -jar -server -Xms512M -Xmx512M -Xss256k chain-ethereum-1.0.0-SNAPSHOT.jar