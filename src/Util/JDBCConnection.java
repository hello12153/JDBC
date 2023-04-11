package Util;

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
            properties.load(new FileInputStream("config.properties"));

            URL = properties.getProperty("URL");
            Username = properties.getProperty("Username");
            Password = properties.getProperty("Password");
            Driver = properties.getProperty("Driver");

            Class.forName(Driver);
            /**
             * java.sql.DriverManager.registerDriver(new com.mysql.jdbc.Driver)
             * 由于MySQL 8.0后的驱动程序内部有静态代码块去调用该方法注册驱动，所以只需加载该class文件让静态代码块被执行即可
             * */

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