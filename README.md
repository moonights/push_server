push_server
===========

nio socket push messages for andorid

--使用NIO实现的 Socket底层数据传输框架 
	参考了mina的部分实现思想  
  	服务端通过acceptor监听连接，将连接请求分配给具体的processor,processor中含有事件处理线程，对读写进行处理
	客户端通过connector建立连接，连接后分配processor,进行读写处理。

>>2014-08-18 
--去除filter ，原因由于filter和handler的作用类似，加filter的作用不大，去除后读写逻辑会清晰些
--
>>2014-08-19 
--去除Binding方法 将端口独立到配置文件中,对可能影响到性能的数值变量抽取到配置中 方便后续性能调试中的最佳值确定
--调整注册信息的细节问题

>>2014-08-20 
--将推送功能独立出来，放到单例Pusher中实现


>>2014-08-21 
--增加HandlerFactory 方便更具体的读写操作 对读写操作进行剥离
--增加ProtocolFactory 方便切换协议接口


>>2014-08-22 
--增加自定义传输协议实现 底层二进制byte[]，应用层使用String和MessageBean对应进行编码和解码
--对MessageBean信息进行封装

>>2.0.2
--增加time定时机制,加入定时心跳检验和定时检查是否超时的检验(类似心跳)
--增加ObjectProtocol读写协议

>>2.0.3
--增加窗体测试界面 
--完善服务端和客户端退出时资源释放。

>>2.0.4
--增加安全证书com.ns.security
--重构包结构

>>2.0.5
--MessageBean增加from to属性 扩展聊天功能
