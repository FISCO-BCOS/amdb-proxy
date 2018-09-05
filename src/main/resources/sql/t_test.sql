CREATE TABLE `t_test` (
  `hash` varchar(64) NOT NULL,
  `num` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `item_id` int(11) NOT NULL default 0,
  `item_name` varchar(256) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL default 0,
  PRIMARY KEY (`hash`,`name`,`item_id`),
  KEY `hash` (`hash`),
  KEY `num` (`num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8