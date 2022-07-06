# 欢迎使用 Pay-Assistant

[![Maven](https://img.shields.io/badge/Maven-v1.0.0-blue)](https://search.maven.org/search?q=g:cool.doudou%20a:pay-assistant-*)
[![License](https://img.shields.io/badge/License-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

## 简介

支付助手 - 简化配置，注解带飞！

## 特点

> 基于微信支付API、支付宝支付API封装

- 简单注解配置，即可实现支付相关操作
- 省掉碍眼繁琐的签名与验签，简化支付交互过程
- 透传返回交互结果

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
  notify-server-address: http://127.0.0.1:8000
  wx:
    appId: wx000001
    mchId: 14000001
    private-key-path: /home/test.pem
    private-key-serial-number: 70000000001
    api-key-v3: abcdefg
  ali:
    appId: ali00001
    private-key-path: /home/test.pem
    public-key-path: /home/test-pub.pem
```

### 使用方式

> 下单

| 参数          | 名称     | 微信                              | 支付宝                    |
|-------------|--------|---------------------------------|------------------------|
| outTradeNo  | 商户订单号  | 数字、大小写字母_-*且在同一个商户号下唯一          | 字母、数字、下划线且需保证在商户端不重复   |
| money       | 总金额    | 单位为分                            | 单位为元，精确到小数点后两位         |
| description | 商品描述   | -                               | 不可使用特殊字符，如 /，=，& 等     |
| timeExpire  | 订单失效时间 | 格式为yyyy-MM-DDTHH:mm:ss+TIMEZONE | 格式为yyyy-MM-dd HH:mm:ss |
| attach      | 附加数据   | 在查询API和支付通知中原样返回                | 在异步通知、对账单中原样返回         |
| uid         | 用户ID   | 用户标识                            | 买家支付宝用户ID              |

> 退款

| 参数          | 名称    | 微信                          | 支付宝                  |
|-------------|-------|-----------------------------|----------------------|
| outTradeNo  | 商户订单号 | 数字、大小写字母_-*且在同一个商户号下唯一      | 字母、数字、下划线且需保证在商户端不重复 |
| outRefundNo | 退款单号  | 商户系统内部唯一，只能是数字、大小写字母_-*@、竖线 | 标识一次退款请求，需要保证在交易号下唯一 |
| reason      | 退款原因  | -                           | -                    |
| money       | 总金额   | 单位为分                        | 无此参数                 |
| refundMoney | 退款金额  | 单位为分                        | 单位为元                 |

> 账单

- 微信交易账单需要两步：首先tradeBill获取账单地址，然后downloadBill返回字节数组数据流
- 支付宝交易账单需要一步：tradeBill获取账单地址，自行请求下载文件

> 支付通知

```java

@Component
public class PayNotifyComponent {
    /**
     * 微信
     */
    @WxPayNotify
    public void wxPayNotify(String message) {
        System.out.println(message);
    }

    /**
     * 支付宝
     */
    @AliPayNotify
    public void aliPayNotify(String message) {
        System.out.println(message);
    }
}
```

### 其他说明

> 支付宝RSA私钥字符串转换成pem文件

- openssl命令生成PKCS1格式密钥文件 *.pem，文件以-----BEGIN RSA PRIVATE KEY-----开头

```shell
openssl rsa -inform PEM -in *.txt -outform PEM -out *.pem
```

- openssl命令转换成PKCS8格式密钥文件 *.pem，文件以-----BEGIN PRIVATE KEY-----开头

```shell
openssl pkcs8 -topk8 -inform PEM -in *.pem -outform PEM -out *_pkcs8.pem -nocrypt
```

> 支付宝RSA公钥字符串转换成pem文件

- txt文件：开头添加-----BEGIN PUBLIC KEY-----，结尾添加-----END PUBLIC KEY-----，更改后缀名为.pem即可

## 版权

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## 鼓励一下，喝杯咖啡

> 欢迎提出宝贵意见，不断完善 MQ-Assistant

![鼓励一下，喝杯咖啡](https://user-images.githubusercontent.com/21210629/172556529-544b2581-ea34-4530-932b-148198b1b265.jpg)
