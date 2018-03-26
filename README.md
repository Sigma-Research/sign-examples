# API使用说明
## 前言

为了保证第三方和服务器交互的安全性，需要对所有的http请求进行签名验证
## 请求签名

### 签名示例

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


2.将上述字段置于http request headers中


注：

1.app-key app-secret是分配给第三方用户的唯一标识

2.app-timestamp,app-nonstr每次请求都需生成，保证每次的签名不一致

3.签名（app-signature）每次请求都需生成，请求完成后立即失效，生成5分钟未使用即为失效
## 域名
```
http://api.manhattan.hexinedu.com
```

## 接口列表

### POST /task/detect
#### 描述
用于同步机器识别
#### 请求参数
body

```
{
	"type": "express", // 暂时只支持这个
	"data": "http://www.xxxx.com/xxxx.jpg" // 图片地址或者图片base64编码
}
```
#### 正常返回

```
{
	"status": 0, // 只有status=0是正常返回
	"statusInfo": "500ms",
	"data": {
		"receiveProvince": "XXX",
		"receiveCompany": "XX",
		"dest": "1",
		"insuranceCharge": "1",
		"receiveCustomerPhone": "XX",
		"goodsNumber": "1",
		"receiveCustomerName": "XXXXXXXXX",
		"clientCode": "XXXXXXXXX",
		"chargedWeight": "18",
		"actualWeight": "2",
		"insuranceAmount": "3000",
		"goodsName": "XX",
		"packages": "1",
		"standardExpress": "0",
		"receiveStreet": "XXX"
	}
}
```

### POST /task/create
#### 描述
创建异步识别任务，传入图片信息、callbackUrl，等待识别+标注数据反馈，任务一次***最多50条数据***
#### 请求参数
body

```
{
	"dataType": "url", // 图片列表参数类型
	"imageList": [{ // 需要识别的图片数据列表
		"id": "1",
		"data": "http://example.com/image1.jpg"
	}, {
		"id": "2",
		"data": "http://example.com/image1.jpg"
	}],
	"callbackUrl": "http://example.com/callback", // 回调地址
	"type": "express" // 识别类型
}
```
创建任务需要传入参数callbackUrl，用于任务结束后回调传入数据

```
// 传入callbackUrl的数据示例
{
	"data": [{ // 返回结果是数组
		"inputId": "1", // 输入时的图片Id
		"result": { // 识别结果
			"receiveProvince": "XXX",
			"receiveCompany": "XX",
			"dest": "1",
			"insuranceCharge": "1",
			"receiveCustomerPhone": "XX",
			"goodsNumber": "1",
			"receiveCustomerName": "XXXXXXXXX",
			"clientCode": "XXXXXXXXX",
			"chargedWeight": "18",
			"actualWeight": "2",
			"insuranceAmount": "3000",
			"goodsName": "XX",
			"packages": "1",
			"standardExpress": "0",
			"receiveStreet": "XXX"
		}
	}, ... ]
}
```

参数 | 说明 | 类型 | 可选值 | 默认 | 备注
--- | ---| ---| --- | --- | --- | ---
dataType | 图片列表参数类型 | String | 'url'或 'base64' | 'base64' |
imageList | 需要识别的图片数据列表 | Array | [] | | imageList[i].data类型需要与dataType保持一致
callbackUrl | 任务完成后回调地址 | String | 合法的url字符串 | | imageList[i].data类型需要与dataType保持一致 
type | 识别服务类型 | String | 'express' | 'express' |

#### 正常返回

```
{
	"status": 0,
	"data": {
		"taskId": "xxx" // 任务Id
	}
}
```
### GET /image/getOne?imageId=xxx
#### 描述

根据imageId获取该图片的信息

#### 请求参数

参数 | 说明 | 类型 | 可选值 | 默认 | 备注
--- | ---| ---| --- | --- | --- | ---
imageId | 图片uid | String |  |  |

#### 正常返回

```
{
	"status": 0,
	"data": {
		"uid": "b88a1903-a1bd-4b2a-932a-160c71b32a4d", // 图片uid
		"taskId": "29454c9a-7f54-441c-95bd-a3b96ec68e9e", // 任务uid
		"url": "http://manhattan-test-image.oss-cn-shanghai.aliyuncs.com/open/task/29454c9a-7f54-441c-95bd-a3b96ec68e9e/b88a1903-a1bd-4b2a-932a-160c71b32a4d.jpg", // 图片地址
		"inputId": "ff18e6cd-7d19-4d0b-8848-4c1ecf8b69df", // 图片输入时的uid
		"detectResult": { // 机器识别结果
			"receiveDetailAddress": "辽宁省镇岭市北原市园西单",
			"deliveryCustomerAddress": "浙江省海宁市许村镇新益开发区",
			...
		},
		"status": 1, // 图片状态
		"markResult": { // 人工标注结果
			"receiveDetailAddress": "辽宁省镇岭市北原市园西单",
			"deliveryCustomerAddress": "浙江省海宁市许村镇新益开发区",
			...
		},
		"markHistory": [ // 标注历史列表
			{
				"markId": "7d8ffc13-bbe3-48d3-9406-b734e94efd08", // 标注Id
				"markResult": { // 标注结果
					"goodsName": "布",
					"accountName": "7折",
					...
				},
				"markTime": 1521968919224, // 标注提交时间
				"reviewResult": { // 审核修改结果
					"goodsName": "布",
					"accountName": "7折",
					...
				},
				"isPassed": 0, // 是否通过审核
				"reviewTime": 1521973298037 // 审核提交时间
			}
			...
		]
	},
	"statusInfo": "14ms"
}
```
### POST /mark/apply

#### 描述

申领任务，任务过期时间为5分钟，不带参数时，申领一个新任务；带参数imageId时，可以验证是否任务是否过期，如果未过期即可续领该任务，时限仍为5分钟

#### 请求参数
body

```
{
	"imageId": "123", // 图片Id
}
```
参数 | 说明 | 类型 | 可选值 | 默认 | 备注
--- | ---| ---| --- | --- | --- | ---
imageId | 图片uid | String |  |  |非必选

#### 正常返回

```
{
	"status": 0,
	"data": {
		"uid": "6131cfe8-a3fe-404e-9cb7-3e7c354aa913",
		"taskId": "29454c9a-7f54-441c-95bd-a3b96ec68e9e",
		"url": "http://manhattan-test-image.oss-cn-shanghai.aliyuncs.com/open/task/29454c9a-7f54-441c-95bd-a3b96ec68e9e/6131cfe8-a3fe-404e-9cb7-3e7c354aa913.jpg",
		"inputId": "32aa2e7f-8eca-4b99-9c6a-f34722d1978f",
		"detectResult": {
			"receiveDetailAddress": "辽宁省镇岭市北原市园西单",
			"deliveryCustomerAddress": "浙江省海宁市许村镇新益开发区",
			"receiveCustomerName": "王志刚",
			"goodsNumber": "1",
			"accountName": "7折",
			"chargedWeight": "24",
			"deliveryCustomerName": "宋庆",
			"insuranceCharge": "2",
			"basicCharge": "97",
			"goodsName": "布",
			"shipperSignature": "乐",
			"insuranceAmount": "2000"
		},
		"markResult": null,
		"status": 2,
		"markHistory": []
	},
	"statusInfo": "13ms"
}
```
### POST /mark/create

#### 描述

创建并提交一条标注

#### 请求参数
body

```
{
	"imageId": "6131cfe8-a3fe-404e-9cb7-3e7c354aa913",
	"result": {
		"receiveDetailAddress": "xxxx",
		"deliveryCustomerAddress": "xxxx",
		"receiveCustomerName": "xxxx",
		"goodsNumber": "1",
		"accountName": "7折",
		"chargedWeight": "24",
		"deliveryCustomerName": "xxxx",
		"insuranceCharge": "2",
		"basicCharge": "97",
		"goodsName": "布",
		"shipperSignature": "乐",
		"insuranceAmount": "2000"
	},
	"markId": null
}
```
参数 | 说明 | 类型 | 可选值 | 默认 | 备注
--- | ---| ---| --- | --- | --- | ---
imageId | 图片uid | String |  |  |非必选

#### 正常返回
```
{
	"status": 0,
	"data": null, // 无数据返回，不报错即为提交成功
	"statusInfo": "20ms"
}
```

### POST /mark/getListByStatus?status=xxx

#### 描述

按状态查看与自己相关的标注历史

```
// 参数可为单属性或多个属性
/mark/getListByStatus?status=failed,passed
```

#### 请求参数
参数 | 说明 | 类型 | 可选值 | 默认 | 备注
--- | ---| ---| --- | --- | --- | ---
status | 状态列表 | StringArray | unchecked, checking, failed, passed|  |

#### 正常返回
```
{
	"status": 0,
	"data": [
		{
			"imageId": "6131cfe8-a3fe-404e-9cb7-3e7c354aa913", // 图片Id
			"url": "http://manhattan-test-image.oss-cn-shanghai.aliyuncs.com/open/task/29454c9a-7f54-441c-95bd-a3b96ec68e9e/6131cfe8-a3fe-404e-9cb7-3e7c354aa913.jpg", // 图片url
			"markId": "c3bc4c31-df6e-41af-a7cb-ad8e19123e71", // 标注Id
			"markResult": { // 标注结果
				"receiveDetailAddress": "辽宁省镇岭市北原市园西单",
				"deliveryCustomerAddress": "浙江省海宁市许村镇新益开发区",
				"receiveCustomerName": "王志刚",
				"goodsNumber": "1",
				"accountName": "7折",
				"chargedWeight": "24",
				"deliveryCustomerName": "宋庆",
				"insuranceCharge": "2",
				"basicCharge": "97",
				"goodsName": "布",
				"shipperSignature": "乐",
				"insuranceAmount": "2000"
			},
			"markTime": 1522070522729, 
			"reviewResult": null,
			"isPassed": null,
			"reviewTime": null
		}
	],
	"statusInfo": "36ms"
}
```


#### 其他异常status

```
{
	"1020": "签名有误",
	"1021": "签名过期",
	"1022": "签名已被使用",
	"1002": "参数错误",
	"1003": "图片不存在或者处于无法标注的状态"
}
```