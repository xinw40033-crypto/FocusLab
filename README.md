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
- [x] Git版本控制配置
- [ ] 用户系统完善
- [ ] 数据统计分析

## Git工作流程

### 分支策略

- **main**: 主分支，稳定版本，只接受来自develop或hotfix的合并
- **develop**: 开发分支，日常开发工作在此进行
- **feature/***: 特性分支，从develop创建，完成后合并回develop
- **hotfix/***: 紧急修复分支，从main创建，修复后合并到main和develop

### 提交规范

使用语义化提交信息，格式：`<类型>: <简短描述>`

**类型说明：**
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档变更
- `style`: 代码格式（不影响功能）
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具变动

**提交示例：**
```bash
feat: 添加训练页面倒计时功能
fix: 修复登录验证码不显示问题
docs: 更新API文档
```

### 开发流程

#### 1. 功能开发
```bash
# 从develop创建特性分支
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name

# 开发并提交
git add .
git commit  # 使用配置的提交模板

# 推送到远程
git push origin feature/your-feature-name

# 创建Pull Request合并到develop
```

#### 2. 发布版本
```bash
# develop合并到main
git checkout main
git pull origin main
git merge develop
git push origin main

# 打标签
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

#### 3. 紧急修复
```bash
# 从main创建hotfix分支
git checkout main
git checkout -b hotfix/critical-bug-fix

# 修复并提交
git add .
git commit -m "fix: 修复关键bug描述"

# 合并回main和develop
git checkout main
git merge hotfix/critical-bug-fix
git push origin main

git checkout develop
git merge hotfix/critical-bug-fix
git push origin develop

# 删除hotfix分支
git branch -d hotfix/critical-bug-fix
```

### Pre-commit检查

每次提交时自动执行以下检查：
- ✅ 提交信息格式验证
- ✅ 合并冲突标记检测
- ✅ 敏感文件防护
- ✅ 大文件警告（>5MB）

### 团队协作规范

1. **代码审查**: 所有合并到main/develop的代码必须经过Code Review
2. **分支清理**: 特性分支合并后及时删除
3. **同步更新**: 每日开始工作前先拉取最新代码
4. **避免force push**: 除非确实必要，否则不要使用`git push -f`
5. **小步提交**: 每个commit只做一件事，便于回滚和追踪

## 许可证
MIT License
