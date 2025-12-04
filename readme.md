# spring security
spring boot 3.3.4
spring security 6.3.3
## 特点
### 自动判断id和phone登录
### 首次登录接口，携带token的后续登录接口
## 提升（异常）
### 异常提示优化
token(jwt)解析失败直接抛出异常
### 登录逻辑异常
尽管每次登录后都会生成新的token，但被删除的旧的token依旧可以使用
原因解析：redis中存储登录凭证
键为  login:token:user_id
值为（String）  LoginUserDetails(JSON)
旧的token依旧可以解析出user_id