# console
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![Build Status](https://travis-ci.org/FISCO-BCOS/AMDB.svg?branch=master)](https://travis-ci.org/FISCO-BCOS/AMDB)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e76b787651514cccadd0a22a409e4dd0)](https://www.codacy.com/app/fisco/AMDB?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=FISCO-BCOS/AMDB&amp;utm_campaign=Badge_Grade)
[![GitHub All Releases](https://img.shields.io/github/downloads/FISCO-BCOS/AMDB/total.svg)](https://github.com/FISCO-BCOS/AMDB)
---

# AMDB
In order to overcome the limitations of local storage in version 1.0,we add support for distributed storage system in version 2.0,which is called AMDB.
AMDB has following advantages:
- It supports multiple storage engines and chooses highly available distributed storage system which can support data expansion easily and quickly.
- It separates computation from data, thus node failure will not lead to data anomalies.
- Data is stored remotely and can be stored in a safer isolation area, which is very significant in many scenarios.
- Distributed storage not only supports the form of Key-Value, but also supports the way of SQL, which makes business development easier.
- The storage of world state is also changed from MPT to distributed storage,which  avoids the problem of performance degradation caused by rapid expansion of world state.
- It optimizes the structure of data storage,saves storage space and has higher access efficiency.

![Architecture](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-2.0/_images/logic_archite.png) 

## Usage

- You can download source code from git and compile manually to use amdb. See [amdb manual](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/amdbconfig.html) for more details.

## Source Installation
```bash
git clone https://github.com/FISCO-BCOS/AMDB.git
cd AMDB;gradle build
```
If you install successfully, it produces the `dist` directory.

## Configuration
Please see the [documentation](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/amdbconfig.html) about configurating for the amdb.

## Developing & Contributing
- Star our Github.
- Pull requests. See [CONTRIBUTING](CONTRIBUTING.md).
- [Ask questions](https://github.com/FISCO-BCOS/AMDB/issues).
- Discuss in [WeChat group](image/WeChatQR.jpg)  or [Gitter](https://gitter.im/fisco-bcos/Lobby).

## Community

By the end of 2018, Financial Blockchain Shenzhen Consortium (FISCO) has attracted and admitted more than 100 members from 6 sectors including banking, fund management, securities brokerage, insurance, regional equity exchanges, and financial information service companies. The first members include the following organizations: Beyondsoft, Huawei, Shenzhen Securities Communications, Digital China, Forms Syntron, Tencent, WeBank, Yuexiu FinTech.

- Join our WeChat [![Scan](https://img.shields.io/badge/style-Scan_QR_Code-green.svg?logo=wechat&longCache=false&style=social&label=Group)](image/WeChatQR.jpg) 

- Discuss in [![Gitter](https://img.shields.io/badge/style-on_gitter-green.svg?logo=gitter&longCache=false&style=social&label=Chat)](https://gitter.im/fisco-bcos/Lobby) 

- Read news by [![](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&label=Follow@FiscoBcos)](https://twitter.com/FiscoBcos)

- Mail us at [![](https://img.shields.io/twitter/url/http/shields.io.svg?logo=Gmail&style=social&label=service@fisco.com.cn)](mailto:service@fisco.com.cn)


