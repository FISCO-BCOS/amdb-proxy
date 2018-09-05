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