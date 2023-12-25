# sky_take_out
苍穹外卖


## 用户管理

### 分页查询

用户分页查询获取到的数据为：

其中createTime的展示时间格式异常
![img.png](md/img.png)

解决方式：
1. 在属性上添加注解，对日期进行格式话@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
2. 在SpringMvcConfiguration中添加配置，扩展SpringMvc的消息转换器，对日期统一处理

### 启用禁用用户

业务规则： 
- 可以对状态为“启用”的账号进行“禁用”操作
- 可以对状态为“禁用”的账号进行“启用”操作
- 状态为“禁用”的账号，不允许登录系统

