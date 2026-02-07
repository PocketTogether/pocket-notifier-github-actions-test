TODO
```
github actions 完善

应用id标准完善
常改的位置统计
改为uika

通知默认重要程度完善

TODO
界面完善，
应用图标修改，缩小一点
明暗主题完善
界面中的主图片，点击后用浏览器打开配置中配置的网址
主图片下方有一个设置按钮，点击后跳转到应用设置，以便于配置通知重要程度、声音、振动、是否弹出之类的

请求处理，处理数据，根据数据内容通知

文字完善，通知等相关代码中的文字用英文
```


同时有多个新消息时的通知标题与内容设计
```
标题
请求所得数组内容中，新消息为几个时
2 New Messages
请求所得数组内容中，全部为新消息时（数组长度要测量，以40为例）
也就说全部为新消息时要为 `${list.length}+ New Messages`
40+ New Messages

内容
一人至四人时
from Haruki
from Haruki, Kippu, Nash, Pan.
四人以上时
from 5 people: Haruki, Kippu, Nash, and others.
```

sse支持，pb实时订阅
```
sse支持，pb实时订阅
总共有三种请求
- pb实时订阅请求 post请求，将每一分钟请求一次
- sse的http长连接
- 和原来轮询一样的数据请求，每五分钟请求一次，用于避免漏消息

```