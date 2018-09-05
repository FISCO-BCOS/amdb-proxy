CREATE TABLE `tb_user` (
  `accountID` varchar(128) NOT NULL,
  `bee` bigint(20) NOT NULL,
  `exchangeBee` bigint(20) NOT NULL default 0,
  `addr` varchar(42) NOT NULL default '',
  `name` varchar(256) NOT NULL default '',
  `realAuth` int(10) DEFAULT NULL default 0,
  `nonce` int(10) NOT NULL default 0,
  `state` int(11) NOT NULL default 0,
  `status` int(11) NOT NULL DEFAULT 0,
  `hash` varchar(128) NOT NULL default '',
  `num` int(11) unsigned NOT NULL default 0,
  `type` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`accountID`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8;