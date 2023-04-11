## JDBC

JDBC，Java Database Connectivity java语言连接数据库，其是用来规范客户端如何访问数据库的应用程序接口。

JDBC允许Java程序连接各种类型的数据库，并且可以用Java语言对数据库执行SQL操作。

其所有关联的类库都在`java.sql.*`包下。JDBC是sun公司的一套接口，不管是MySQL还是oracle，JDBC都是用同一个连口去连接

### Driver驱动

`Driver`驱动是每个数据库公司或机构根据`java`的规范针对自己的数据库开发的一套接口。

每个数据库对应不同的driver驱动，需要开发者到官网下载并添加到开发环境中。

#### MySQL

在MySQL 5.0之前driver驱动的全限定类名为`com.mysql.jdbc.Driver`，而到 8.0 改为`com.mysql.cj.jdbc.Driver`

MySQL的Driver驱动包下载到MySQL官网[MySQL :: MySQL Community Downloads](https://dev.mysql.com/downloads/) - [Connector/J](https://dev.mysql.com/downloads/connector/j/) ，Select Operating System 选择 **Platform Independent** 下载

![image-20230410153934749](https://gitee.com/imgsbed_8/my-images/raw/master/img/202304101539803.png)

![image-20230410154103815](https://gitee.com/imgsbed_8/my-images/raw/master/img/202304101541870.png)

![image-20230410154139450](https://gitee.com/imgsbed_8/my-images/raw/master/img/202304101541488.png)

解压后将其中的jar包添加到开发环境即可

如未使用集成工具开发JDBC则要在环境变量中，将jar加到`classpath`变量里，例windows。其中，`.;`是代表当前的路径，classpath变量会让所有的class文件都执行这个变量值的内容，但不是所有的class文件都在mysql jdbc的路径下，所以，要加上`.;`让其他类文件可以正常被识别运行。

![image-20230410154724265](https://gitee.com/imgsbed_8/my-images/raw/master/img/202304101547311.png)

在IDEA的项目中加入驱动，在项目的模块上右键，选择`Open Module Settings`后，在选择左侧的`Libraries`，在右侧选择加入jar包，并找到解压后的驱动包即可

![image-20230410155801078](https://gitee.com/imgsbed_8/my-images/raw/master/img/202304101558127.png)

![image-20230410160048165](https://gitee.com/imgsbed_8/my-images/raw/master/img/202304101600198.png)

![](https://gitee.com/imgsbed_8/my-images/raw/master/img/202304101607975.png)



### JDBC编程六步走

1. 注册驱动

   两种注册方法

   1. 使用`DriverManager.registerDriver(Driver driver)`
   2. 使用`Class.forName(Class class)`

2. 获取连接

   `DriverManager.getConnection(URL,Username,Password)`

   - `url`：代表数据库的URL，如：`jdbc:mysql://localhost:3306/db_name`，其中`localhost`是MySQL的主机ip地址，`3306`是MySQL的端口号，db_name是要连接的数据库名
   - `username`：代表连据数据库所需的用户名
   - `password`：代表连据数据库所需的用户密码

3. 获取数据库操作对象

   `Connection.createStatement()`

4. 执行SQL

   - 执行 INSERT, UPDATE,  DELETE 语句

      `Statement.executeUpdate()`

   - 执行 SELECT 语句

     `Statement.executeQuery()`

5. 处理结果集

6. 关闭连接，释放资源

   - `connection.close()`
   - `statement.close()`

**优化**：上面的注册驱动只需执行一次，而数据库操作对象可以重复使用。上面的1、2、3可以整合，不用每次执行SQL都执行这三个步骤的代码，只需在第一次都调用时就执行。而URL、Username、Password和Driver驱动这信息可以作用配置文件，在要变更数据库厂商时不用去改变源代码。

#### 整合

JDBC可以通过获取`properties`配置文件中的`driver`值从而加载不同的驱动

步骤

1. 创建配置文件，添加下面的属性和值

   - `url`：代表数据库的URL，如：`jdbc:mysql://localhost:3306/db_name`，其中`localhost`是MySQL的主机ip地址，`3306`是MySQL的端口号，db_name是要连接的数据库名
   - `username`：代表连据数据库所需的用户名
   - `password`：代表连据数据库所需的用户密码
   - `driver`：代表Java程序中要加载哪个驱动程序的类名，例如要使用MySQL则写`com.mysql.jdbc.Driver`

2. 编写JDBC程序，并将下面的步骤写到静态代码块中以确保类被加载时建立好连接

   1. 读取`.properties`配置文件，并获取其属性值
   2. 加载配置文件中的`driver`驱动
      - `Class.forNmae(driver)`
   3. 建立连接
      - `Drivermanager.getConnection(url,username.password)`
   4. 编写获取连接对象的方法`getConnection`
      - `return connection`

3. 代码实现

   1. `config.properties`配置文件

      ```properties
      username=admin
      password=123456
      URL=jdbc:mysql://localhost:3306/test
      driver=com.mysql.cj.jdbc.Driver
      ```

   2. JDBC

      ```java
      package com.hello.Util;
      
      import java.io.FileInputStream;
      import java.sql.*;
      import java.util.Properties;
      
      public class JDBCConnection {
          private static Connection connection;
          private static String URL;
          private static String Username;
          private static String Password;
          private static String Driver;
          private static Statement statement;
      
          static{
              try {
                  Properties properties = new Properties();
                  properties.load(new FileInputStream("src/config.properties"));
      
                  URL = properties.getProperty("URL");
                  Username = properties.getProperty("Username");
                  Password = properties.getProperty("Password");
                  Driver = properties.getProperty("Driver");
      
                  Class.forName(Driver);
                  //连接对象
                  connection = DriverManager.getConnection(URL, Username, Password);
                  System.out.println("URL:"+URL);
                  System.out.println("Username:"+Username);
                  System.out.println("Password:"+Password);
                  System.out.println("Driver:"+Driver);
                  //数据库操作对象
                  statement = connection.createStatement();
      
              }catch(Exception e){
                  e.printStackTrace();
              }
          }
      
          /*
          * 向外界提供连接对象
          * */
          public static Connection getConnection(){
              return connection;
          }
          /*
          * 向外界提供数据库操作对象
          * */
          public static Statement getStatement(){ return statement; }
          /*
          * 释放资源
          * */
          public static void close(){
              if (connection != null){
                  try {
                      connection.close();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
              if (statement != null){
                  try {
                      statement.close();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          }
      }
      ```
