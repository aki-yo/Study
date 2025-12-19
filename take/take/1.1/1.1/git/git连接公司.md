1. 配置git环境：首先，需要在本地计算机上安装并配置git环境。可以从git官网（https://git-scm.com/）下载并安装最新的git版本。安装完成后，使用git config命令设置全局用户名和邮箱，例如：  
“`  
git config –global user.name “Your Name”  
git config –global user.email “your.email@example.com”  
“`

2. 生成SSH密钥：要连接公司的仓库，通常会使用SSH协议。生成SSH密钥对可以使用以下命令：  
“`  
ssh-keygen -t rsa -b 4096 -C “your.email@example.com”  
“`  
根据提示，可以选择密钥存储位置和输入密码。

3. 将SSH公钥添加到公司仓库：将生成的SSH公钥（通常位于用户主目录下的.ssh目录中，文件名为id_rsa.pub）添加到公司仓库的账户设置中。具体操作方法可以参考公司仓库提供的文档或联系相关人员。

4. 克隆仓库：在本地计算机上选择合适的目录，使用git clone命令克隆公司仓库。克隆命令的格式为：  
“`  
git clone git@<公司仓库地址>:<仓库名>.git  
“`  
将<公司仓库地址>和<仓库名>替换为实际的地址和名称。

5. 进行工作：克隆完成后，就可以在本地计算机上进行工作了。可以使用git add、git commit等命令来管理本地修改，并使用git push命令将本地修改推送到公司仓库。