数据库配置
master:
url: jdbc:mysql://localhost:3306/fl?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
username: fluser
password: 123456
需要配置一个以上这样的数据库，数据库名为fl
先要把若依自带的数据库导进去（注意注意）（sql文件里的两个数据表）

要下载node，包在git仓库中，
https://blog.csdn.net/weixin_42017221/article/details/134166408?ops_request_misc=&request_id=&biz_id=102&utm_term=npm%20:%20%E6%97%A0%E6%B3%95%E5%B0%86%E2%80%9Cnpm%E2%80%9D%E9%A1%B9%E8%AF%86%E5%88%AB%E4%B8%BA%20cmdlet%E3%80%81%E5%87%BD%E6%95%B0%E3%80%81%E8%84%9A&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduweb~default-1-134166408.142^v102^pc_search_result_base3&spm=1018.2226.3001.4187
可以根据这个网址配置node

redis也得下载，只用启动redis_server.exe文件，出现cmd框架和图片即可


ps：启动框架前要打开数据库，redis，然后在命令行运行两行代码
cd ruoyi-ui
npm run dev
即可打开网页