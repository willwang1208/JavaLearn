# Spring Simple Web Project

一个学习spring web的例子。

部署到web容器后，例如设置端口8083，访问 http://localhost:8083/simple-web/可以看到基本功能演示界面。

通过执行org.whb.springmvc.TestClient的main方法可以看到更多功能演示。

###学习内容

1. @Configuration和@Bean替代xml配置，见 GlobalWebApplicationInitializer

2. 加载WEB-INF和ClassPath下的资源文件，见 PropertiesConfiguration

3. 配置JDBC数据源和事务、Mybatis配置，见 RepositoryConfiguration

4. Groovy脚本支持、异步执行、缓存、RMI服务与客户端，见 ServiceConfiguration

5. Spring MVC、文件上传、异步Servlet、各类视图（jsp、velocity、freemarker、groovy）、拦截器，见MvcConfiguration

6. 控制器Controller举例，包括各类视图、json、xml、pdf、excel等

7. Spring + Mybatis举例，见RepositoryConfiguration

8. 定时任务，见 ScheduleServiceImpl

9. 数据库表结构、主键、索引描述接口，见 MysqlJdbcTemplate
