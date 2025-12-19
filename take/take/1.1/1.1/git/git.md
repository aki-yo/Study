
## 知识


![](assets/Pasted%20image%2020250701134056.png)



![](assets/Pasted%20image%2020250701133938.png)



## 过程

![](assets/Pasted%20image%2020250701114150.png)


.bashrc文件

#用于输出git提交日志 
alias git-log='git log --pretty=oneline --all --graph --abbrev-commit' 
#用于输出当前目录所有文件及基本信息 
alias ll='ls -al'


```

source ~/.bashrc


git config --global core.quotepath false
```

![](assets/Pasted%20image%2020250701115857.png)

这样不行，要在c盘的那个地方还是要
![](assets/Pasted%20image%2020250701133331.png)

像这样

然后才会全局生效

那个ll便捷指令

记得要加注释

git commit -m "Add test.txt for initial testing"



![](assets/Pasted%20image%2020250701144935.png)

这个地方冲突之后

文本内容会直接变成新的样子《《《《head
这个样子，然后直接修改这个玩意，然后把整段修改成你想要的样子，（这个样子随意，
之后add commit就好了
是直接修改之后的东西直接覆盖了之前冲突的东西，不是必须二选一之类的







#### 简易的命令行入门教程:

Git 全局设置:

git config --global user.name "maple"
git config --global user.email "2805982002@qq.com"

创建 git 仓库:

mkdir first
cd first
git init 
touch README.md
git add README.md
git commit -m "first commit"
git remote add origin git@gitee.com:AkiMomiji/first.git
git push -u origin "master"

已有仓库?

cd existing_git_repo
git remote add origin git@gitee.com:AkiMomiji/first.git
git push -u origin "master"

## 生成公钥

生成公钥
ssh-keygen -t rsa
![](assets/Pasted%20image%2020250701155812.png)

查看公钥
cat ~/.ssh/id_rsa.pub
![](assets/Pasted%20image%2020250701160112.png)

测试：
ssh -T git@gitee.com
![](assets/Pasted%20image%2020250701161405.png)




在 Git 命令 `git push origin master` 中：

- **`origin`**：这是你本地 Git 仓库中配置的 **远程仓库（remote）的名称**，默认情况下，当你使用 `git clone` 克隆一个仓库时，Git 会自动将这个远程仓库命名为 `origin`。  
  - 你可以通过 `git remote -v` 查看所有远程仓库及其对应的 URL。
  - 你也可以添加其他远程仓库，比如 `git remote add upstream <URL>`，然后使用 `git push upstream master`。

- **`master`**：这是你本地仓库的 **分支名称（branch name）**，通常代表主分支（在 Git 的早期版本中默认是 `master`，但现在许多新项目默认使用 `main`）。  
  - 当你运行 `git push origin master` 时，Git 会将本地的 `master` 分支推送到远程仓库 `origin` 的同名分支（即 `origin/master`）。
  - 如果远程分支名和本地分支名不同，你可以指定远程分支名，如 `git push origin local-branch:remote-branch`。

### **示例解析**
1. **`git push origin master`**  
   - 推送本地 `master` 分支到远程 `origin` 的 `master` 分支（如果远程没有 `master`，则会创建它）。

2. **`git push origin main`**  
   - 如果你的主分支是 `main`（GitHub/GitLab 等平台的现代默认分支），则应该使用这个命令。

3. **`git push origin dev:feature`**  
   - 推送本地 `dev` 分支到远程 `origin` 的 `feature` 分支（名称不同时使用）。

### **`--set-upstream` 的作用**
- 如果你第一次推送分支，可以使用 `-u`（`--set-upstream`）建立追踪关系，后续可以直接 `git push` 而不需要指定远程和分支名：
  ```bash
  git push -u origin master  # 第一次推送
  git push                  # 后续推送（自动关联到 origin/master）
  ```

### **总结**
- **`origin`** → 远程仓库的名称（默认是 `origin`，可自定义）。  
- **`master`** → 本地分支名（推送到远程的同名分支）。  
- 如果远程分支名不同，需用 `local-branch:remote-branch` 格式指定。

## 繁
每个仓库如果要连接远程仓库需要add remote

如果直接克隆下来的项目则是直接就关联好的



## idea

t提取就是fetch

拉取就是pull

![](assets/Pasted%20image%2020250701174709.png)




## end
