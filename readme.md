# Orange Juice
An opensource API Server of Online Judge.

[![Build Status](https://travis-ci.org/function-x/Orange-Juice.svg)](https://travis-ci.org/function-x/Orange-Juice)

一个开源的在线评测系统 API 服务器。

不涉及任何前端部分。

***
## 安装

1. 安装运行需要的软件：

+ node.js 6.x
+ MongoDB

2. 打开 `config.js` 进行必要的配置。

3. 安装依赖

```bash
$ npm install
```

4. 启动服务器

```bash
$ npm start
```

如果你使用 `forever`:

```bash
$ forever start index.js
```

5. 向 `/system` 发送 POST 请求来进行系统安装：

```js
{
    username: String, // 系统管理员用户名 [必填]
    password: String, // 系统管理员密码 [必填]
    email: String, // 系统管理员邮箱 [必填]
}
```

请求系统：

+ 创建第一个用户。
+ 将其注册为系统管理员。
+ 创建一个公共用户组。
+ 在公共用户组下创建一个题单。

> 这个 API 是一次性的，成功使用过后产生的副作用可以保证此 API 无法被第二次调用。

## 文档

构建 HTML 格式的文档

```bash
$ npm run docs
```

+ JSDOC: `/docs/index.html`
+ APIDOC: `/docs/api/index.html`

> 代码覆盖测试通过 `npm run cover` 来生成，要求先满足所有运行条件，位于 `/docs/coverage/lcov_rep/index.html`

## 用户系统

我们定义了完善的角色系统，让用户无须注册众多账号，只需要注册一次，便能应对多种需求。

与操作系统中进程与线程的概念类似，用户的角色是用户权限管理的最小单位。

### 用户角色

+   系统管理员 (SA)

      在系统安装时生成，具有最高的权限。

      **系统管理员的数量极少但不唯一，彼此之间也是完全平权的存在。**

    + 授予/吊销 用户的各种权限。
    + (**危险**) 卸载系统(清空数据库)。

+   用户组所有者 (Owner)

    + 创建/删除/修改/查询 题目。
    + 创建/删除/修改/查询 题单。

+   用户组成员 (Member)

    + 查询 题目。
    + 查询 题单。

用户的登录凭证是用户名与密码。用户名作为创建角色时的第一个默认名字。

## 题库系统

每一个用户都能创建题目与题单。

每一个题目都有其所有者，只有其所有者才能将题目添加到题单中。

用户可以将题单私有或者公开，也可以将题单挂到自己管理的用户组里，只让组内成员看到。

其他用户只能从题单看见题目。

总的来说，用户可以先检索自己能看到的题单，然后获取内部所有的题目。

可见的题单：

+ 自己创建的所有题目组成的题单
+ 自己创建的题单
+ 自己所在组中的题单
+ 公开的题单

被判定为用户私有的题单将存放在用户文档中，用户组私有的题单将存放在用户组文档中，而只有公开的题单才会存放在 `ProblemList` 集合中。
