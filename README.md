# API使用说明

### 前言

为了保证第三方和服务器交互的安全性，需要对所有的http请求进行签名验证

### 请求签名

```
	{
	  "app-timestamp" : 1519740797398, // 13位时间戳
	  "app-key":"sg_asdlkaskljdasjd",// 分配给第三方的app_key
	  "app-signature":"asdsadasdasdlkjklj" // 利用app_secret签名根据一定hash算法生成
	  "app-nonstr":"sadasd" // 用于hash的随机字符串
	}
```

1.app-signature签名的生成方式：

将app-key,app-secret,app-nonstr,app-timestamp 以:分隔拼接，利用MD5 hash算法生成


2.将上述字段置于http request herders中


注：

1.app-key app-secret是分配给第三方用户的唯一标识

2.app-timestamp,app-nonstr每次请求都需生成，保证每次的签名不一致

3.签名（app-signature）每次请求都需生成，请求完成后立即失效，生成5分钟未使用即为失效



### 请求实体

#### 请求参数

```
	{
	  "type" : "express", // 暂时只支持这个
	  "data":"http://www.xxxx.com/xxxx.jpg",// 图片地址和图片base64编码
	}
```
#### 正常返回

```
	{
	  "status" : 0, // 只有status=0是正常返回
	  "statusInfo":"500ms",
	  "data":{
	      receiveProvince: 'XXX',
		  receiveCompany: 'XX',
		  dest: '1',
		  insuranceCharge: '1',
		  receiveCustomerPhone: 'XX',
		  goodsNumber: '1',
		  receiveCustomerName: 'XXXXXXXXX',
		  clientCode: 'XXXXXXXXX',
		  chargedWeight: '18',
		  actualWeight: '2',
		  insuranceAmount: '3000',
		  goodsName: 'XX',
		  packages: '1',
		  standardExpress: '0',
		  receiveStreet: 'XXX'
      }
	}
```

#### 其他异常status

```
	{
      "1020":"签名有误",
      "1021":"签名过期",
      "1022":"签名已被使用"
	}
```