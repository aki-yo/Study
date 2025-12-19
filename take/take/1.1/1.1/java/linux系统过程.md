

## 初始
```powershell

#更新源yum

sudo mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

sudo curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo



#安装vim
yum install vim




#	安装 docker前置工具， 更换源
sudo yum install -y yum-utils device-mapper-persistent-data lvm2

sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

sudo sed -i 's+download.docker.com+mirrors.aliyun.com/docker-ce+' /etc/yum.repos.d/docker-ce.repo

#更新yum，建立缓存
sudo yum makecache fast




#安装docker

yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin


# 启动Docker
systemctl start docker

# 停止Docker
systemctl stop docker

# 重启
systemctl restart docker

# 设置开机自启
systemctl enable docker	#我已经设置为开机自启了

# 执行docker ps命令，如果不报错，说明安装启动成功
docker ps




#我也进行下面的步骤了

# 创建目录
mkdir -p /etc/docker

# 复制内容
tee /etc/docker/daemon.json <<-'EOF'
{
    "registry-mirrors": [
        "http://hub-mirror.c.163.com",
        "https://mirrors.tuna.tsinghua.edu.cn",
        "http://mirrors.sohu.com",
        "https://ustc-edu-cn.mirror.aliyuncs.com",
        "https://ccr.ccs.tencentyun.com",
        "https://docker.m.daocloud.io",
        "https://docker.awsl9527.cn"
    ]
}
EOF

# 重新加载配置
systemctl daemon-reload

# 重启Docker
systemctl restart docker




```





## 开始部署项目

```powershell
#mysql

mkdir /usr/local/app/bigevent/mysql

#上传初始文件也就是Init.sql

docker run -d \
  --name mysql \
  -v /usr/local/app/bigevent/mysql/init.sql:/docker-entrypoint-initdb.d/init.sql \
  -p 3307:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123456 \
  mysql:8

docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:latest  


# 1.首先通过命令创建一个网络 
docker network create ohh

# mysql redis 容器，加入 itheima 网络 
docker network connect ohh mysql
docker network connect ohh redis


#后端

#传入文件夹了进入这个文件夹
cd /root/bigevent
#上传资料
#根据dockerfile构建镜像
docker build -t bigevent:1.0 .


#查看这个jdk解压后的文件夹名字是什么
tar -tf jdk17.tar.gz | head -n 1  # 查看压缩包内的顶层目录名发现是jdk-17.0.15

#根据镜像创建容器
docker run -d --name bigevent-server --network ohh -p 8080:8080  bigevent:1.0


#前端

#创建前端文件静态资源文件夹#基于绝对路径进行创建
mkdir /usr/local/app/bigevent/bigevent-nginx-vue/html
#然后进行上传静态资源
#然后创建镜像容器

docker run -d \
--name bigevent-nginx-vue \
-v /usr/local/app/bigevent/bigevent-nginx-vue/html:/usr/share/nginx/html \
-v /usr/local/app/bigevent/bigevent-nginx-vue/conf/nginx.conf:/etc/nginx/nginx.conf \
--network ohh \
-p 80:80 \
nginx:1.20.2



```


## dockercompose


```
docker compose up -d
```

## end
```
end
```





