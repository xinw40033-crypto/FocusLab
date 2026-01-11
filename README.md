# 专注力训练系统

## 快速启动

### 1. 环境要求
- Java 11+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose（可选）

### 2. 本地开发启动

#### 步骤1：配置数据库
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE focus_training CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 步骤2：修改配置文件
编辑 `src/main/resources/application.properties`，修改数据库连接信息：
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### 步骤3：启动应用
```bash
mvn spring-boot:run
```

访问：http://localhost:8080

### 3. Docker部署

#### 一键启动（推荐）
```bash
docker-compose up -d
```

#### 查看日志
```bash
docker-compose logs -f app
```

#### 停止服务
```bash
docker-compose down
```

访问：http://localhost

## 项目结构
```
FocusLab/
├── src/
│   ├── main/
│   │   ├── java/com/focuslab/
│   │   │   ├── common/          # 通用组件
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── repository/  # 数据访问层
│   │   │   │   ├── service/     # 业务逻辑层
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   └── util/        # 工具类
│   │   │   └── training/        # 训练模块
│   │   │       └── validator/   # 验证器
│   │   └── resources/
│   │       ├── static/          # 静态资源
│   │       │   ├── js/          # JavaScript
│   │       │   └── images/      # 训练图片
│   │       ├── templates/       # HTML模板
│   │       └── application.properties
│   └── test/                    # 测试代码
├── Dockerfile
├── docker-compose.yml
├── nginx.conf
└── pom.xml
```

## 8种训练类型

1. **箭头追踪** - 跟随箭头路径训练
2. **数字重复查找** - 查找重复数字
3. **动物数字匹配** - 动物与数字配对
4. **寻宝游戏** - 隐藏对象查找
5. **数字对比** - 数字大小比较
6. **舒尔特方格** - 顺序点击训练
7. **符号矩阵** - 符号模式识别
8. **数字迁移** - 数字移动路径

## API接口

### 获取训练内容
```
GET /api/training/{id}
GET /api/training?type=ARROW_TRACKING&difficulty=1
```

### 提交训练结果
```
POST /api/submit
Content-Type: application/json

{
  "userId": 1,
  "contentId": 5,
  "answers": [[120,50], [150,60]],
  "totalTimeSpent": 25000
}
```

### 获取训练报告
```
GET /api/report/{userId}
```

## 技术栈

- **后端**: Spring Boot 3.2.0 + JPA + MySQL
- **前端**: HTML5 + Bootstrap 5 + Canvas API
- **部署**: Docker + Nginx
- **验证**: 策略模式 + 责任链模式

## 开发计划

- [x] 基础框架搭建
- [x] 数据库设计
- [x] 8种训练验证器实现
- [x] 前端页面开发
- [x] 数据导入工具
- [x] Docker部署配置
- [x] 移动端适配
- [ ] 用户系统完善
- [ ] 数据统计分析

## 许可证
MIT License
