### RabbitMQ 优缺点对比 
优点：
1、开源（代码版本更新快、社区活跃）、性能优秀（采用高效Erlang语言开发）、稳定性有保障
2、提供可靠性投递模式confirm模式、return模式等，以及表达式配置
3、与Spring AMQP 完美整合，spring提供了丰富的api
4、保证数据不丢失前提下做到高可靠性，高可用性，可以提供集群模式、HA模式、镜像队列模式
5、具有功能强大的功能管理页面
6、支持多种语言

缺点：
1、超大规模并发情况下逊于Kafka和RocketMQ


### AMQP（Advice Message Queuing Protocol）高级消息队列协议
AMQP核心概念：可以把消息传递的过程想象成:当你将一个包裹送到邮局，邮局会暂存并最终将邮件通过邮递员送到收件人的手上， AMQP协议就好比由邮局、邮箱和邮递员组成的一个系统。
- producer：生产者（发件人），消息的投递方
- comsumer：消费者（收件人），消息的消费方
- connection：网络连接，应用程序与broker之间网络连接
- channel：网络信道，进行消息读写操作的通道，每个channel代表一个会话任务，一个connection中可以创建多个channel
- broker/server：消息中间件的中间节点（邮局，也可以看作一台Rabbit MQ服务器）
- virtual host：虚拟机主机（邮局中工作小组），在borker下的对exchangge、queue等进行最上层的逻辑隔离，一个virtual host中可以创建多个不能同名的exchange和queue
- exchange：交换器（邮局中分拣员），它根据分发规则，匹配查询表中的routing key，分发消息到queue中去。
- queue：队列（收件人查收信件时的邮箱）
- message：消息（投递的包裹信件），由两部分组成body（消息体） 和 properties（参数，对消息进行修饰），
- routingkey：路由键（填写在包裹上的地址），在某些情形下， RoutingKey 与BindingKey 可以看作同一个东西。
- banding：绑定，exchange和exchange、queue之间的连接关系，某些情况下会分配一个bandingkey（direct、topic）
- bandingkey：绑定键（包裹的目的地）


### 消息是怎么流转？
step1：生产者将消息发送到指定的exchange，并指定消息的routing key
step2：消费者将queue绑定到相关exchange上
step3：消费者监听queue，获取并消费路由到queue上的消息
重点：生产者和消费者之间，最主要是通过exchangge和queue之间的routing key进行解耦


### 交换机属性
- Name：交换机名称
- Type：direct、topic、fanout、headers
- Durability：是否需要持久化，true持久化
- Auto delete：当绑定到exchange上最后一个队列删除后，此exchange自动删除
- Interal：当前exchange是否适用于rabbitMQ内部使用 ，默认false
- Arguments：扩展参数，用于AMQP协议自制定化使用，如alternate-exchange

### Exchange 交换机分类
- fanout（广播，一对多）、不用配置RoutingKey 和 BandingKey，交换器会无视BindingKey将消息路由到所有绑定到该交换器的队列中。所有交换机类型中性能最好的，原因它不走路由。
- direct（直连，一对一）、RoutingKey 和 BandingKey 必须相同，消息才能正常发送到queue
注意：direct模式可以使用rabbitmq自带的exchange（default exchange），使用时不需绑定操作，消息传递时routingkey和queue必须完全匹配才能接收消息，否则消息会被丢弃。
- topic（主题，模糊，一对多）、RoutingKey 和 BandingKey 做模糊匹配，两者不是相同的
- headers（基本上不用）


### Routingkey中可以包含两种通配符
- “.”  字符串分割符
- “#” 通配任何零个或多个word
- “*” 通配任何单个word


### Queue属性
- Name：队列名称
- Durability：是否需要持久化，true持久化
- Exclusive：true表示只有当前connection可以使用，其他不可使用
- Auto Delete：ture表示当最后一个监听被移除之后，队列会被自动删除
- Arguments：扩展参数，如x-message-ttl、x-expires、x-max-length、x-max-lenght-bytes、x-dead-letter-exchange、x-dead-letter-routing-key、x-max-priority


### Message组成
- body（消息体） 
- properties（属性参数，对消息进行修饰）


### Message常用属性
- delivery model 发送模式 1-普通投递 2-持久永化投递
- headers 消息头，可以自定义一些业务属性
- content_type 消息格式类型，json、text等
- content_encoding 消息编码
- priority 优先级 1-9
- correlation_id 唯一id
- reply_to 重回队列
- expiration 消息过期时间（单位毫秒）
- message_id 
- timestamp 时间戳
- type 
- user_id 用户id
- app_id 应用服务id
- cluster_id 集群id


推荐一个网站，http://tryrabbitmq.com ，它提供在线RabbitMQ 模拟器，可以帮助理解Exchange／queue／binding概念。
