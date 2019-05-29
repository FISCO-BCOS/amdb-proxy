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


(2019-05-29)
**修复**

* 修复异常场景下数据库提交无法保证事务性的问题。
