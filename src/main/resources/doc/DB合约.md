# DB合约

## 介绍

DB合约用于访问AMDB数据库

## 接口

DB.sol

	import "String.sol";
	
	contract DBFactory {
	    function openDB(string) public constant returns (DB);
	}
	
	//查询条件
	contract Condition {
	    function EQ(string, int);
	    function EQ(string, string);
	    function EQ(string, String);
	    
	    function NE(string, int);
	    function NE(string, string);
	    function NE(string, String);
	    
	    function GT(string, int);
	    function GE(string, int);
	    
	    function LT(string, int);
	    function LE(string, int);
	    
	    function limit(int);
	    function limit(int, int);
	}
	
	//单条数据记录
	contract Entry {
	    function getInt(string) public constant returns(int);
	    function getString(string) public constant returns(String);
	    
	    function set(string, int) public;
	    function set(string, string) public;
	    function set(string, String) public;
	}
	
	//数据记录集
	contract Entries {
	    function get(int) public constant returns(Entry);
	    function size() public constant returns(int);
	}
	
	//DB主类
	contract DB {
	    function select(String, Condition) public constant returns(Entries);
	    function select(string, Condition) public constant returns(Entries);
	    
	    function insert(String, Entry) public returns(int);
	    function insert(string, Entry) public returns(int);
	    
	    function update(String, Entry, Condition) public returns(int);
	    function update(string, Entry, Condition) public returns(int);
	    
	    function remove(String, Condition) public returns(int);
	    function remove(string, Condition) public returns(int);
	    
	    function newEntry() public constant returns(Entry);
	    function newCondition() public constant returns(Condition);
	}