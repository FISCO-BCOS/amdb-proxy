### v2.3.0

(2020-03-31)

**修改**

* 将依赖的Java-SDK升级为2.3.0

### v2.2.0

(2019-12-27)

**修改**

* 将依赖的Java-SDK升级为2.2.0

### v2.1.0

(2019-09-18)

**修复**

* 修复了CRUD下中文参数不符合预期的bug

**修改**

* 将依赖的Java-SDK升级为2.1.0
* 将配置操作统一到一个文件中进行

### v2.0.2

(2019-07-03)
**修复**

* 修复特殊场景下数据查询和提交异常的问题。

**修改**

* 将用户表的value字段类型从text调整为mediumtext。

### v2.0.1
(2019-05-29)
**修复**

* 修复异常场景下数据库提交无法保证事务性的问题。

**修改**

* 将mysql的sql_mode改成STRICT_TRANS_TABLES，确保SQL语句的严格检查以及字段长度的严格检查。
* 修改max_allowed_packet从默认的10M修改为1G。
* 将部分系统表的字段类型从mediumtext调整为longtext。

### v2.0.0

(2019-04-25)

**New Feature**

*AMDB服务提供了FISCO-BCOS 2.0接入数据库的功能，包括：
1. 提供数据库CRUD接口。
2. 支持事务，支持连接池。
3. 提供AMDB服务与节点之间的加密连接。


*AMDB service provides database connectors for FISCO-BCOS nodes.
1. Provide CRUD database interface
2. Support transactions and connection pool.
3. Support encrypted connections between AMDB service and FISCO-BCOS nodes (since 2.0.0-rc2).
