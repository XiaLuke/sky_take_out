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


### 编辑员工

业务规则：
- 点击编辑按钮，弹出编辑员工的模态框
- 修改员工信息后，点击保存按钮，关闭模态框，刷新列表

涉及接口
- 根据员工id查询信息（get /admin/employee/{id}）
- 修改员工信息(put /admin/employee)


## 分类

### 业务规则
-  分类名称需唯一
- 分类按照类型可分为菜品分类和套餐分类
- 新增分类默认状态为“禁用”

### 接口设计
- 新增分类(post /admin/category)
- 分页查询分类(get /admin/category/page?pageNum=1&pageSize=10)
- 启用禁用分类(put /admin/category/status/{status})
- 编辑分类(put /admin/category)
- 删除分类(delete /admin/category/{id})
- 根据类型查询分类(get /admin/category/list?type=1)
