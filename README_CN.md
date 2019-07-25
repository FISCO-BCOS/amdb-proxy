中文 / [English](./README.md)
# AMDB

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![Build Status](https://travis-ci.org/FISCO-BCOS/amdb-proxy.svg?branch=master)](https://travis-ci.org/FISCO-BCOS/amdb-proxy)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e76b787651514cccadd0a22a409e4dd0)](https://www.codacy.com/app/fisco/AMDB?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=FISCO-BCOS/AMDB&amp;utm_campaign=Badge_Grade)
---

**此版本只支持**[FISCO BCOS 2.0及以上版本](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)。

AMDB 2.0版本中新增了对分布式数据存储的支持，克服了本地化数据存储的诸多限制，和1.0版本相比有如下优势：
- 支持多种存储引擎，选用高可用的分布式存储系统，可以支持数据简便快速地扩容；
- 将计算和数据隔离，节点故障不会导致数据异常；
- 数据在远端存储，数据可以在更安全的隔离区存储，这在很多场景中非常有意义；
- 分布式存储不仅支持Key-Value形式，还支持SQL方式，使得业务开发更为简便；
- 世界状态的存储也从原来的MPT存储结构转为分布式存储，避免了世界状态急剧膨胀导致性能下降的问题；
- 优化了数据存储的结构，更节约存储空间，存取效率更高。

![逻辑架构图](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-2.0/_images/logic_archite.png) 

## 使用

- 可以直接下载源码包并手动编译，具体请参考[amdb使用指南](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/distributed_storage.html)

## 源码安装
```bash
git clone https://github.com/FISCO-BCOS/amdb-proxy.git
cd amdb-proxy && ./gradlew build
```
安装成功后，将在当前目录生成一个dist目录。

## Configuration
amdb配置请参考[amdb使用指南](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/distributed_storage.html)

## 贡献代码
- 点亮我们的小星星(点击项目左上方Star按钮).
- 提交代码(Pull requests)，参考我们的[代码贡献流程](CONTRIBUTING.md).
- [提问](https://github.com/FISCO-BCOS/AMDB/issues).
- Discuss in [微信群](image/WeChatQR.jpg)  or [Gitter](https://gitter.im/fisco-bcos/Lobby).

## 社区生态

金链盟开源工作组，获得金链盟成员机构的广泛认可，并由专注于区块链底层技术研发的成员机构及开发者牵头开展工作。其中首批成员包括以下单位(排名不分先后): 博彦科技、华为、深证通、神州数码、四方精创、腾讯、微众银行、越秀金科。

- 微信群 [![Scan](https://img.shields.io/badge/style-Scan_QR_Code-green.svg?logo=wechat&longCache=false&style=social&label=Group)](image/WeChatQR.jpg) 

- Gitter [![Gitter](https://img.shields.io/badge/style-on_gitter-green.svg?logo=gitter&longCache=false&style=social&label=Chat)](https://gitter.im/fisco-bcos/Lobby) 

- Twitter [![](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&label=Follow@FiscoBcos)](https://twitter.com/FiscoBcos)

- e-mail [![](https://img.shields.io/twitter/url/http/shields.io.svg?logo=Gmail&style=social&label=service@fisco.com.cn)](mailto:service@fisco.com.cn)


