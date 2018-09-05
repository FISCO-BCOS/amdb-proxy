# String内部合约

## 介绍

String内部合约用于解决solidity原生string的缺陷，其解决了以下场景的问题：
- 提供字符串的比较、连接、遍历和修改功能
- 允许在智能合约调用智能合约时，传递字符串
- 允许将字符串用于log输出和函数返回值

AMDB的所有与字符串相关的操作，均基于String内部合约

## 接口

String.sol

	contract StringFactory {
	    function newString(string) public returns(String); //从原生string构建String
	    function newString(int) public returns(String); //从原生int构建String
	    function newString(String) public returns(String); //复制一个String
	    
	    function newString(string, int begin, int end) public returns(String); //从原生string截取部分内容构建String
	    function newString(String, int begin, int end) public returns(String); //从String截取部分内容构建String
	    
	    function toString() public constant returns(string);
	}
	
	contract String {
	    function charAt(int) public constant returns (bytes1); //获取指定位置的一个字符
	    
	    function equal(String) public constant returns (bool); //判断两个字符串是否相等
	    function equal(string) public constant returns (bool); //判断两个字符串是否相等
	    
	    function concat(String) public returns (String); //连接字符串
	    function concat(string) public returns (String); //连接字符串
	    function concat(byte) public returns (String); //连接字符串
	    
	    function contains(String) public; //判断是否包含特定字符串
	    function contains(string) public; //判断是否包含特定字符串
	    function contains(byte) public; //判断是否包含特定字符串
	    
	    function isEmpty() public constant returns(bool); //字符串是否为空
	    
	    function length() public constant returns(int); //字符串长度
	    
	    function toInt() public constant returns(int); //转换为int
	    function toString() public constant returns(string); //转换为原生string
	    function toBytes32() public constant returns(bytes32); //转换为原生bytes32
	}

## 使用

案例代码：

	import "String.sol";
	
	contract StringTest {
	    function test() public constant returns(int) {
	        StringFactory sf = StringFactory(0x1000);
	        
	        String lhs = sf.newString(100);
	        String rhs = sf.newString(86);
	        
	        lhs.concat(rhs);
	        
	        return lhs.toInt();
	    }
	}

StringFactory合约的地址固定为0x1000