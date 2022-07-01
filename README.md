# 欢迎使用 Pay-Assistant

[![Maven](https://img.shields.io/badge/Maven-v1.0.0-blue)](https://search.maven.org/search?q=g:cool.doudou%20a:pay-assistant-*)
[![License](https://img.shields.io/badge/License-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

## 简介

支付助手 - 简化配置，注解带飞！

## 特点

> 配置灵活，基于微信支付API、支付宝支付API，没有改变任何框架结构，只为简化； 简单注解，即可实现消息MQ

## 使用指引

### 引入依赖

```kotlin
implementation("cool.doudou:pay-assistant:latest")
```

### 微信支付配置

> 配置属性如下：

```yaml
pay:
  modes:
    - wx
    - ali
  notify-server: http://127.0.0.1:8000
  wx:
    appId: wx000001
    mchId: 14000001
  ali:
    appId: ali00001
```

### 使用方式

> 支付通知

```java

@Component
public class PayNotifyComponent {
    /**
     * 微信
     */
    @WxPayNotify
    public void wxPayNotify() {

    }

    /**
     * 支付宝
     */
    @AliPayNotify
    public void aliPayNotify() {

    }
}
```

## 版权

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## 鼓励一下，喝杯咖啡

> 欢迎提出宝贵意见，不断完善 MQ-Assistant

![鼓励一下，喝杯咖啡](https://user-images.githubusercontent.com/21210629/172556529-544b2581-ea34-4530-932b-148198b1b265.jpg)
