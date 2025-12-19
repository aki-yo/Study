



![](assets/Pasted%20image%2020250721213033.png)




![](assets/Pasted%20image%2020250717175648.png)


//C_WORKFORCEBALANCEO15_NUM

//workforcebalanceO15Num

```
,  
nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' AND m.o15 = '1') THEN 1 ELSE 0 END),0) o15OnjobCnt
```

```
,  
CASE WHEN H.C_POS_LEVEL IN ('O7', 'O8', 'O9') OR OPM.C_POST_LEVEL IN ('B3','B4') OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15
```

```
,  
CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15
```

![](assets/Pasted%20image%2020250714164117.png)
![](assets/Pasted%20image%2020250714164412.png)





```
/**  
 * 获取外包延时出勤明细的接口  
 *  
 * @param requestInfo 请求参数  
 * @return 延时出勤列表  
 */  
@Log(title = "管理者自助-外包延时出勤明细", businessType = BusinessType.OTHER)  
@GetMapping("/details")  
public IPage<?> queryOutsourceOverTimeWorkByConfigId(OverTimeWorkRequestInfo requestInfo) {  
    LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());  
    SysUser user = loginUser.getUser();  
    //校验组织合法性以及是否为空,为空则自动获取主职对应组织，如果主职返回同样为空，则提示报错  
    String orgHid = managerCommonService.checkUnitInfoByUserCode(user,requestInfo.getOrgHidString());  
    requestInfo.setOrgHidString(orgHid);  
    return overTimeWorkService.overTimeWorkDetails(requestInfo);  
}
```


员工延时明细、员工组织信息、员工分类信息
工号不空
员工类型CODE 在034

员工分类信息
中C_LABOR_TYPE 用工方式没有外包

先按年份降序；
再按月份降序；
然后按延时出勤余额降序；
最后按体系、部门、工号升序排列。

```
 LEFT JOIN TB_STA_EMP_ORG EO  
    ON D.C_EMPLOYEE_ID = EO.C_EMPLOYEE_ID AND EO.C_BEGIN_DATE &lt;= TRUNC(SYSDATE) AND EO.C_END_DATE > TRUNC(SYSDATE) AND EO.C_DEPT_TYPE = '1'  
LEFT JOIN TB_STA_EMP_CLASS TC  
    ON EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID AND TC.C_BEGIN_DATE &lt;= TRUNC( CURRENT_DATE ) AND TC.C_END_DATE >= TRUNC( CURRENT_DATE ) --员工分类信息
```
![](assets/Pasted%20image%2020250708152322.png)


因为我要找主表也就是左表的大部分数据，所以要左连接，是嘛


“AND TC.C_LABOR_TYPE !='8' --剔除外包” 我还不知道这些字段什么作用

```sql
SELECT  
        D.C_WORKOVERTIME workOverTime, -- 工作日延时出勤  
        D.C_WEEKOVERTIME weekOverTime, -- 休息日延时出勤  
        D.C_LEAVETIME leaveTime, -- 调休  
        D.C_WEEKTRAVELTIME weekTravelTime, -- 周末出差延时出勤  
        D.C_LEGALOVERTIME legalOverTime, -- 法定延时出勤  
        D.C_BALANCETIME balanceTime -- 延时出勤余额  
        FROM  
        TB_TMG_EMPOVERTIMEDETAIL D  
        LEFT JOIN TB_STA_EMP_ORG EO  
            ON D.C_EMPLOYEE_ID = EO.C_EMPLOYEE_ID AND EO.C_BEGIN_DATE = TRUNC(SYSDATE) AND EO.C_END_DATE > TRUNC(SYSDATE) AND EO.C_DEPT_TYPE = '1'  
        LEFT JOIN TB_STA_EMP_CLASS TC  
            ON EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID AND TC.C_BEGIN_DATE = TRUNC( CURRENT_DATE ) AND TC.C_END_DATE >= TRUNC( CURRENT_DATE ) --员工分类信息  
        WHERE  
        D.C_EMPCODE IS NOT NULL  
        AND D.C_EMPTYPECODE IN ('0', '3', '4')  
--         AND TC.C_LABOR_TYPE !='8' --剔除外包  
  
ORDER BY  
D.C_YEAR DESC, D.C_MONTH DESC, D.C_BALANCETIME DESC,  
D.C_SYSTEMNAME, D.C_DEPTNAME, D.C_EMPCODE
```



![](assets/Pasted%20image%2020250708173025.png)
默认控制台

```
<!--外包延时出勤明细-->  
<select id="queryOutsourceAvgOverTimeWorkDetails" resultType="overTimeWorkDetailsVo">  
    SELECT  
    <include refid="overTimeWorkBaseSql"/>  
    D.C_WORKOVERTIME workOverTime, -- 工作日延时出勤  
    D.C_WEEKOVERTIME weekOverTime, -- 休息日延时出勤  
    D.C_LEAVETIME leaveTime, -- 调休  
    D.C_WEEKTRAVELTIME weekTravelTime, -- 周末出差延时出勤  
    D.C_LEGALOVERTIME legalOverTime, -- 法定延时出勤  
    D.C_BALANCETIME balanceTime -- 延时出勤余额  
    FROM  
    TB_TMG_EMPOVERTIMEDETAIL D    LEFT JOIN TB_STA_EMP_ORG EO    ON D.C_EMPLOYEE_ID = EO.C_EMPLOYEE_ID AND EO.C_BEGIN_DATE &lt;= TRUNC(SYSDATE) AND EO.C_END_DATE > TRUNC(SYSDATE) AND EO.C_DEPT_TYPE = '1'  
    LEFT JOIN TB_STA_EMP_CLASS TC    ON EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID AND TC.C_BEGIN_DATE &lt;= TRUNC( CURRENT_DATE ) AND TC.C_END_DATE >= TRUNC( CURRENT_DATE ) --员工分类信息  
    WHERE  
    D.C_EMPCODE IS NOT NULL    AND D.C_EMPTYPECODE IN ('0', '3', '4')    <if test="conditionSql != null and conditionSql !='' ">  
        AND ${conditionSql} -- C_EMPTYPE IN (0) 条件sql  
    </if>  
    <include refid="timeCondition"/>  
    <include refid="empCondition"/>  
    ${requestParam.params.dataScope}  
    ORDER BY    D.C_YEAR DESC, D.C_MONTH DESC, D.C_BALANCETIME DESC,    D.C_SYSTEMNAME, D.C_DEPTNAME, D.C_EMPCODE</select>
```



```
        # # 处理 Vue 开发服务器的静态资源（不代理，直接交给开发服务器）

        # location ^~ /lib-pmdp/static/ {

        #     proxy_set_header X-Real-IP $remote_addr;

        #     proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

        #     proxy_set_header Host $host;

        #     proxy_pass http://127.0.0.1:8011;

        # }

  

        # location ^~ /plan/atp/ {

        #     # 重写到正确的静态资源路径

        #     proxy_pass http://127.0.0.1:8011/lib-pmdp/;

        #     proxy_set_header Host $host;

        # }

  

        # # 其他 /lib-pmdp 下的路由（如页面路由）

        # location ^~ /lib-pmdp/ {

        #     proxy_set_header X-Real-IP $remote_addr;

        #     proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

        #     proxy_set_header Host $host;

        #     proxy_pass http://127.0.0.1:8011;

        # }

        # # 添加针对 /pmdp/config/ 的静态资源规则

        # location ^~ /pmdp/config/ {

        #     # 如果是开发环境，代理到 Vue 开发服务器

        #     proxy_pass http://127.0.0.1:8011/lib-pmdp/config/;

        #     proxy_set_header Host $host;

  

        #     # 如果是生产环境，直接指向文件路径（示例）

        #     # root /path/to/your/project/dist;

        #     # try_files $uri =404;

        # }
```


# 国际化翻译


我现在有一个
create table sys_language_config  
(  
    id                bigint auto_increment comment '主键'  
        primary key,  
    language_category varchar(32)   null comment '语言类别',  
    language_value    varchar(1000) null comment '语言值',  
    relation_id       varchar(100)  null comment '关联id',  
    relation_table    varchar(100)  null comment '关联需国际化的表名',  
    operate_time      datetime      null comment '操作时间'  
)  
    comment '国际化语言配置表';  
  
create index language_config_category  
    on sys_language_config (language_category);  
  
create index language_config_relationid  
    on sys_language_config (relation_id);


这样的表，

还有一个我放在最下方的 json 数据
我需要你生成许多插入的sql语句，要求如下

现在我需要你把下方数据中的dict_label列数据分别翻译为 英语 越南语 西班牙语 印度尼西亚语 作为 sys_language_config 表内的  language_value 

而 当英语的时候  sys_language_config 表 内的 language_category 为 en 
越南语的时候为 vi
西班牙语的时候为 es
印度尼西亚语的时候为 id

而 sys_language_config 表 内的 relation_id
则为 下方数据中的dict_label 数据 所对应的  dict_code

而 sys_language_config 表 内的 relation_table 固定为 sys_dict_data
而 sys_language_config 表 内的 operate_time 固定为 2025-08-07 17:51:04

要求如上，给我生成对应的sql语句来插入这些要求的数据 首先给我生成关于 下方 数据的 前十 个数据 的对应的40 条sql插入语句
另外要求每个数据要有一个注释包含 对应的dictcode dictlabel，注释下面是对应的四个翻译后的sql语句，还有每个sql语句就一行 
包含单引号的内容都已正确转义处理
```json

  {
    "dict_code": 65,
    "dict_label": "太原市"
  },
  {
    "dict_code": 66,
    "dict_label": "大同市"
  },
  {
    "dict_code": 67,
    "dict_label": "阳泉市"
  },
  {
    "dict_code": 68,
    "dict_label": "长治市"
  },
  {
    "dict_code": 69,
    "dict_label": "晋城市"
  },
  {
    "dict_code": 70,
    "dict_label": "朔州市"
  },
  {
    "dict_code": 71,
    "dict_label": "晋中市"
  },
  {
    "dict_code": 72,
    "dict_label": "运城市"
  },
  {
    "dict_code": 73,
    "dict_label": "忻州市"
  },
  {
    "dict_code": 74,
    "dict_label": "临汾市"
  },
  {
    "dict_code": 75,
    "dict_label": "吕梁市"
  },
  {
    "dict_code": 76,
    "dict_label": "空"
  },
  {
    "dict_code": 77,
    "dict_label": "呼和浩特市"
  },
  {
    "dict_code": 78,
    "dict_label": "包头市"
  },
  {
    "dict_code": 79,
    "dict_label": "乌海市"
  },
  {
    "dict_code": 80,
    "dict_label": "赤峰市"
  },
  {
    "dict_code": 81,
    "dict_label": "通辽市"
  },
  {
    "dict_code": 82,
    "dict_label": "鄂尔多斯市"
  },
```




```json
[
  
  {
    "dict_code": 480,
    "dict_label": "PARLIN"
  },
  {
    "dict_code": 481,
    "dict_label": "Empty"
  },
  {
    "dict_code": 482,
    "dict_label": "Austin"
  },
  {
    "dict_code": 483,
    "dict_label": "Dallas"
  },
  {
    "dict_code": 484,
    "dict_label": "Empty"
  },
  {
    "dict_code": 485,
    "dict_label": "Manchester"
  },
  {
    "dict_code": 486,
    "dict_label": "Portsmouth"
  },
  {
    "dict_code": 487,
    "dict_label": "Empty"
  },
  {
    "dict_code": 488,
    "dict_label": "Empty"
  },
  {
    "dict_code": 489,
    "dict_label": "Empty"
  },
  {
    "dict_code": 490,
    "dict_label": "Farlington"
  },
  {
    "dict_code": 491,
    "dict_label": "审核中"
  },
  {
    "dict_code": 492,
    "dict_label": "审核通过"
  },
  {
    "dict_code": 493,
    "dict_label": "审核不通过"
  },
  {
    "dict_code": 494,
    "dict_label": "身份证"
  },
  {
    "dict_code": 495,
    "dict_label": "护照"
  },
  {
    "dict_code": 496,
    "dict_label": "港澳台通行证"
  },
  {
    "dict_code": 697,
    "dict_label": "Sunnyvale"
  },
  {
    "dict_code": 699,
    "dict_label": "是"
  },
  {
    "dict_code": 700,
    "dict_label": "否"
  },
  {
    "dict_code": 701,
    "dict_label": "Belgium"
  },
  {
    "dict_code": 702,
    "dict_label": "Canada"
  }
]
```


```
{
    "dict_code": 1,
    "dict_label": "单位"
  },
  {
    "dict_code": 2,
    "dict_label": "个人"
  },
  {
    "dict_code": 3,
    "dict_label": "草稿"
  },
  {
    "dict_code": 4,
    "dict_label": "激活"
  },
  {
    "dict_code": 5,
    "dict_label": "未使用"
  },
  {
    "dict_code": 6,
    "dict_label": "中国"
  },
  {
    "dict_code": 8,
    "dict_label": "其他"
  },
  {
    "dict_code": 9,
    "dict_label": "供应商"
  },
  {
    "dict_code": 10,
    "dict_label": "客户"
  },
  {
    "dict_code": 11,
    "dict_label": "United States of America"
  },
  {
    "dict_code": 12,
    "dict_label": "United Kingdom of Great Britain and Northern Ireland"
  },
  {
    "dict_code": 13,
    "dict_label": "空"
  },
  {
    "dict_code": 14,
    "dict_label": "北京市"
  },
  {
    "dict_code": 15,
    "dict_label": "天津市"
  },
  {
    "dict_code": 16,
    "dict_label": "河北省"
  },
  {
    "dict_code": 17,
    "dict_label": "山西省"
  },
  {
    "dict_code": 18,
    "dict_label": "内蒙古自治区"
  },
  {
    "dict_code": 19,
    "dict_label": "辽宁省"
  },
  {
    "dict_code": 20,
    "dict_label": "吉林省"
  },
  {
    "dict_code": 21,
    "dict_label": "黑龙江省"
  },
  {
    "dict_code": 22,
    "dict_label": "上海市"
  },
  {
    "dict_code": 23,
    "dict_label": "江苏省"
  },
  {
    "dict_code": 24,
    "dict_label": "浙江省"
  },
  {
    "dict_code": 25,
    "dict_label": "安徽省"
  },
  {
    "dict_code": 26,
    "dict_label": "福建省"
  },
  {
    "dict_code": 27,
    "dict_label": "江西省"
  },
  {
    "dict_code": 28,
    "dict_label": "山东省"
  },
  {
    "dict_code": 29,
    "dict_label": "河南省"
  },
  {
    "dict_code": 30,
    "dict_label": "湖北省"
  },
  {
    "dict_code": 31,
    "dict_label": "湖南省"
  },
  {
    "dict_code": 32,
    "dict_label": "广东省"
  },
  {
    "dict_code": 33,
    "dict_label": "广西壮族自治区"
  },
  {
    "dict_code": 34,
    "dict_label": "海南省"
  },
  {
    "dict_code": 35,
    "dict_label": "重庆市"
  },
  {
    "dict_code": 36,
    "dict_label": "四川省"
  },
  {
    "dict_code": 37,
    "dict_label": "贵州省"
  },
  {
    "dict_code": 38,
    "dict_label": "云南省"
  },
  {
    "dict_code": 39,
    "dict_label": "西藏自治区"
  },
  {
    "dict_code": 40,
    "dict_label": "陕西省"
  },
  {
    "dict_code": 41,
    "dict_label": "甘肃省"
  },
  {
    "dict_code": 42,
    "dict_label": "青海省"
  },
  {
    "dict_code": 43,
    "dict_label": "宁夏回族自治区"
  },
  {
    "dict_code": 44,
    "dict_label": "新疆维吾尔自治区"
  },
  {
    "dict_code": 45,
    "dict_label": "澳门"
  },
  {
    "dict_code": 46,
    "dict_label": "台湾"
  },
  {
    "dict_code": 47,
    "dict_label": "香港"
  },
  {
    "dict_code": 48,
    "dict_label": "空"
  },
  {
    "dict_code": 49,
    "dict_label": "市辖区"
  },
  {
    "dict_code": 50,
    "dict_label": "空"
  },
  {
    "dict_code": 51,
    "dict_label": "市辖区"
  },
  {
    "dict_code": 52,
    "dict_label": "空"
  },
  {
    "dict_code": 53,
    "dict_label": "石家庄市"
  },
  {
    "dict_code": 54,
    "dict_label": "唐山市"
  },
  {
    "dict_code": 55,
    "dict_label": "秦皇岛市"
  },
  {
    "dict_code": 56,
    "dict_label": "邯郸市"
  },
  {
    "dict_code": 57,
    "dict_label": "邢台市"
  },
  {
    "dict_code": 58,
    "dict_label": "保定市"
  },
  {
    "dict_code": 59,
    "dict_label": "张家口市"
  },
  {
    "dict_code": 60,
    "dict_label": "承德市"
  },
  {
    "dict_code": 61,
    "dict_label": "沧州市"
  },
  {
    "dict_code": 62,
    "dict_label": "廊坊市"
  },
  {
    "dict_code": 63,
    "dict_label": "衡水市"
  },
  {
    "dict_code": 64,
    "dict_label": "空"
  },
  {
    "dict_code": 65,
    "dict_label": "太原市"
  },
  {
    "dict_code": 66,
    "dict_label": "大同市"
  },
  {
    "dict_code": 67,
    "dict_label": "阳泉市"
  },
  {
    "dict_code": 68,
    "dict_label": "长治市"
  },
  {
    "dict_code": 69,
    "dict_label": "晋城市"
  },
  {
    "dict_code": 70,
    "dict_label": "朔州市"
  },
  {
    "dict_code": 71,
    "dict_label": "晋中市"
  },
  {
    "dict_code": 72,
    "dict_label": "运城市"
  },
  {
    "dict_code": 73,
    "dict_label": "忻州市"
  },
  {
    "dict_code": 74,
    "dict_label": "临汾市"
  },
  {
    "dict_code": 75,
    "dict_label": "吕梁市"
  },
  {
    "dict_code": 76,
    "dict_label": "空"
  },
  {
    "dict_code": 77,
    "dict_label": "呼和浩特市"
  },
  {
    "dict_code": 78,
    "dict_label": "包头市"
  },
  {
    "dict_code": 79,
    "dict_label": "乌海市"
  },
  {
    "dict_code": 80,
    "dict_label": "赤峰市"
  },
  {
    "dict_code": 81,
    "dict_label": "通辽市"
  },
  {
    "dict_code": 82,
    "dict_label": "鄂尔多斯市"
  },
  {
    "dict_code": 83,
    "dict_label": "呼伦贝尔市"
  },
  {
    "dict_code": 84,
    "dict_label": "巴彦淖尔市"
  },
  {
    "dict_code": 85,
    "dict_label": "乌兰察布市"
  },
  {
    "dict_code": 86,
    "dict_label": "兴安盟"
  },
  {
    "dict_code": 87,
    "dict_label": "锡林郭勒盟"
  },
  {
    "dict_code": 88,
    "dict_label": "阿拉善盟"
  },
  {
    "dict_code": 89,
    "dict_label": "空"
  },
  {
    "dict_code": 90,
    "dict_label": "沈阳市"
  },
  {
    "dict_code": 91,
    "dict_label": "大连市"
  },
  {
    "dict_code": 92,
    "dict_label": "鞍山市"
  },
  {
    "dict_code": 93,
    "dict_label": "抚顺市"
  },
  {
    "dict_code": 94,
    "dict_label": "本溪市"
  },
  {
    "dict_code": 95,
    "dict_label": "丹东市"
  },
  {
    "dict_code": 96,
    "dict_label": "锦州市"
  },
  {
    "dict_code": 97,
    "dict_label": "营口市"
  },
  {
    "dict_code": 98,
    "dict_label": "阜新市"
  },
  {
    "dict_code": 99,
    "dict_label": "辽阳市"
  },
  {
    "dict_code": 100,
    "dict_label": "盘锦市"
  },
  {
    "dict_code": 101,
    "dict_label": "铁岭市"
  },
  {
    "dict_code": 102,
    "dict_label": "朝阳市"
  },
  {
    "dict_code": 103,
    "dict_label": "葫芦岛市"
  },
  {
    "dict_code": 104,
    "dict_label": "空"
  },
  {
    "dict_code": 105,
    "dict_label": "长春市"
  },
  {
    "dict_code": 106,
    "dict_label": "吉林市"
  },
  {
    "dict_code": 107,
    "dict_label": "四平市"
  },
  {
    "dict_code": 108,
    "dict_label": "辽源市"
  },
  {
    "dict_code": 109,
    "dict_label": "通化市"
  },
  {
    "dict_code": 110,
    "dict_label": "白山市"
  },
  {
    "dict_code": 111,
    "dict_label": "松原市"
  },
  {
    "dict_code": 112,
    "dict_label": "白城市"
  },
  {
    "dict_code": 113,
    "dict_label": "延边朝鲜族自治州"
  },
  {
    "dict_code": 114,
    "dict_label": "空"
  },
  {
    "dict_code": 115,
    "dict_label": "哈尔滨市"
  },
  {
    "dict_code": 116,
    "dict_label": "齐齐哈尔市"
  },
  {
    "dict_code": 117,
    "dict_label": "鸡西市"
  },
  {
    "dict_code": 118,
    "dict_label": "鹤岗市"
  },
  {
    "dict_code": 119,
    "dict_label": "双鸭山市"
  },
  {
    "dict_code": 120,
    "dict_label": "大庆市"
  },
  {
    "dict_code": 121,
    "dict_label": "伊春市"
  },
  {
    "dict_code": 122,
    "dict_label": "佳木斯市"
  },
  {
    "dict_code": 123,
    "dict_label": "七台河市"
  },
  {
    "dict_code": 124,
    "dict_label": "牡丹江市"
  },
  {
    "dict_code": 125,
    "dict_label": "黑河市"
  },
  {
    "dict_code": 126,
    "dict_label": "绥化市"
  },
  {
    "dict_code": 127,
    "dict_label": "大兴安岭地区"
  },
  {
    "dict_code": 128,
    "dict_label": "空"
  },
  {
    "dict_code": 129,
    "dict_label": "市辖区"
  },
  {
    "dict_code": 130,
    "dict_label": "空"
  },
  {
    "dict_code": 131,
    "dict_label": "南京市"
  },
  {
    "dict_code": 132,
    "dict_label": "无锡市"
  },
  {
    "dict_code": 133,
    "dict_label": "徐州市"
  },
  {
    "dict_code": 134,
    "dict_label": "常州市"
  },
  {
    "dict_code": 135,
    "dict_label": "苏州市"
  },
  {
    "dict_code": 136,
    "dict_label": "南通市"
  },
  {
    "dict_code": 137,
    "dict_label": "连云港市"
  },
  {
    "dict_code": 138,
    "dict_label": "淮安市"
  },
  {
    "dict_code": 139,
    "dict_label": "盐城市"
  },
  {
    "dict_code": 140,
    "dict_label": "扬州市"
  },
  {
    "dict_code": 141,
    "dict_label": "镇江市"
  },
  {
    "dict_code": 142,
    "dict_label": "泰州市"
  },
  {
    "dict_code": 143,
    "dict_label": "宿迁市"
  },
  {
    "dict_code": 144,
    "dict_label": "空"
  },
  {
    "dict_code": 145,
    "dict_label": "杭州市"
  },
  {
    "dict_code": 146,
    "dict_label": "宁波市"
  },
  {
    "dict_code": 147,
    "dict_label": "温州市"
  },
  {
    "dict_code": 148,
    "dict_label": "嘉兴市"
  },
  {
    "dict_code": 149,
    "dict_label": "湖州市"
  },
  {
    "dict_code": 150,
    "dict_label": "绍兴市"
  },
  {
    "dict_code": 151,
    "dict_label": "金华市"
  },
  {
    "dict_code": 152,
    "dict_label": "衢州市"
  },
  {
    "dict_code": 153,
    "dict_label": "舟山市"
  },
  {
    "dict_code": 154,
    "dict_label": "台州市"
  },
  {
    "dict_code": 155,
    "dict_label": "丽水市"
  },
  {
    "dict_code": 156,
    "dict_label": "空"
  },
  {
    "dict_code": 157,
    "dict_label": "合肥市"
  },
  {
    "dict_code": 158,
    "dict_label": "芜湖市"
  },
  {
    "dict_code": 159,
    "dict_label": "蚌埠市"
  },
  {
    "dict_code": 160,
    "dict_label": "淮南市"
  },
  {
    "dict_code": 161,
    "dict_label": "马鞍山市"
  },
  {
    "dict_code": 162,
    "dict_label": "淮北市"
  },

  {
    "dict_code": 163,
    "dict_label": "铜陵市"
  },
  {
    "dict_code": 164,
    "dict_label": "安庆市"
  },
  {
    "dict_code": 165,
    "dict_label": "黄山市"
  },
  {
    "dict_code": 166,
    "dict_label": "滁州市"
  },
  {
    "dict_code": 167,
    "dict_label": "阜阳市"
  },
  {
    "dict_code": 168,
    "dict_label": "宿州市"
  },
  {
    "dict_code": 169,
    "dict_label": "六安市"
  },
  {
    "dict_code": 170,
    "dict_label": "亳州市"
  },
  {
    "dict_code": 171,
    "dict_label": "池州市"
  },
  {
    "dict_code": 172,
    "dict_label": "宣城市"
  },
  {
    "dict_code": 173,
    "dict_label": "空"
  },
  {
    "dict_code": 174,
    "dict_label": "福州市"
  },
  {
    "dict_code": 175,
    "dict_label": "厦门市"
  },
  {
    "dict_code": 176,
    "dict_label": "莆田市"
  },
  {
    "dict_code": 177,
    "dict_label": "三明市"
  },
  {
    "dict_code": 178,
    "dict_label": "泉州市"
  },
  {
    "dict_code": 179,
    "dict_label": "漳州市"
  },
  {
    "dict_code": 180,
    "dict_label": "南平市"
  },
  {
    "dict_code": 181,
    "dict_label": "龙岩市"
  },
  {
    "dict_code": 182,
    "dict_label": "宁德市"
  },
  {
    "dict_code": 183,
    "dict_label": "空"
  },
  {
    "dict_code": 184,
    "dict_label": "南昌市"
  },
  {
    "dict_code": 185,
    "dict_label": "景德镇市"
  },
  {
    "dict_code": 186,
    "dict_label": "萍乡市"
  },
  {
    "dict_code": 187,
    "dict_label": "九江市"
  },
  {
    "dict_code": 188,
    "dict_label": "新余市"
  },
  {
    "dict_code": 189,
    "dict_label": "鹰潭市"
  },
  {
    "dict_code": 190,
    "dict_label": "赣州市"
  },
  {
    "dict_code": 191,
    "dict_label": "吉安市"
  },
  {
    "dict_code": 192,
    "dict_label": "宜春市"
  },
  {
    "dict_code": 193,
    "dict_label": "抚州市"
  },
  {
    "dict_code": 194,
    "dict_label": "上饶市"
  },
  {
    "dict_code": 195,
    "dict_label": "空"
  },
  {
    "dict_code": 196,
    "dict_label": "济南市"
  },
  {
    "dict_code": 197,
    "dict_label": "青岛市"
  },
  {
    "dict_code": 198,
    "dict_label": "淄博市"
  },
  {
    "dict_code": 199,
    "dict_label": "枣庄市"
  },
  {
    "dict_code": 200,
    "dict_label": "东营市"
  },
  {
    "dict_code": 201,
    "dict_label": "烟台市"
  },
  {
    "dict_code": 202,
    "dict_label": "潍坊市"
  },
  {
    "dict_code": 203,
    "dict_label": "济宁市"
  },
  {
    "dict_code": 204,
    "dict_label": "泰安市"
  },
  {
    "dict_code": 205,
    "dict_label": "威海市"
  },
  {
    "dict_code": 206,
    "dict_label": "日照市"
  },
  {
    "dict_code": 207,
    "dict_label": "临沂市"
  },
  {
    "dict_code": 208,
    "dict_label": "德州市"
  },
  {
    "dict_code": 209,
    "dict_label": "聊城市"
  },
  {
    "dict_code": 210,
    "dict_label": "滨州市"
  },
  {
    "dict_code": 211,
    "dict_label": "菏泽市"
  },
  {
    "dict_code": 212,
    "dict_label": "空"
  },
  {
    "dict_code": 213,
    "dict_label": "郑州市"
  },
  {
    "dict_code": 214,
    "dict_label": "开封市"
  },
  {
    "dict_code": 215,
    "dict_label": "洛阳市"
  },
  {
    "dict_code": 216,
    "dict_label": "平顶山市"
  },
  {
    "dict_code": 217,
    "dict_label": "安阳市"
  },
  {
    "dict_code": 218,
    "dict_label": "鹤壁市"
  },
  {
    "dict_code": 219,
    "dict_label": "新乡市"
  },
  {
    "dict_code": 220,
    "dict_label": "焦作市"
  },
  {
    "dict_code": 221,
    "dict_label": "濮阳市"
  },
  {
    "dict_code": 222,
    "dict_label": "许昌市"
  },
  {
    "dict_code": 223,
    "dict_label": "漯河市"
  },
  {
    "dict_code": 224,
    "dict_label": "三门峡市"
  },
  {
    "dict_code": 225,
    "dict_label": "南阳市"
  },
  {
    "dict_code": 226,
    "dict_label": "商丘市"
  },
  {
    "dict_code": 227,
    "dict_label": "信阳市"
  },
  {
    "dict_code": 228,
    "dict_label": "周口市"
  },
  {
    "dict_code": 229,
    "dict_label": "驻马店市"
  },
  {
    "dict_code": 230,
    "dict_label": "省直辖县级行政区划"
  },


  
  {
    "dict_code": 231,
    "dict_label": "空"
  },
  {
    "dict_code": 232,
    "dict_label": "武汉市"
  },
  {
    "dict_code": 233,
    "dict_label": "黄石市"
  },
  {
    "dict_code": 234,
    "dict_label": "十堰市"
  },
  {
    "dict_code": 235,
    "dict_label": "宜昌市"
  },
  {
    "dict_code": 236,
    "dict_label": "襄阳市"
  },
  {
    "dict_code": 237,
    "dict_label": "鄂州市"
  },
  {
    "dict_code": 238,
    "dict_label": "荆门市"
  },
  {
    "dict_code": 239,
    "dict_label": "孝感市"
  },
  {
    "dict_code": 240,
    "dict_label": "荆州市"
  },
  {
    "dict_code": 241,
    "dict_label": "黄冈市"
  },
  {
    "dict_code": 242,
    "dict_label": "咸宁市"
  },
  {
    "dict_code": 243,
    "dict_label": "随州市"
  },
  {
    "dict_code": 244,
    "dict_label": "恩施土家族苗族自治州"
  },
  {
    "dict_code": 245,
    "dict_label": "省直辖县级行政区划"
  },
  {
    "dict_code": 246,
    "dict_label": "空"
  },
  {
    "dict_code": 247,
    "dict_label": "长沙市"
  },
  {
    "dict_code": 248,
    "dict_label": "株洲市"
  },
  {
    "dict_code": 249,
    "dict_label": "湘潭市"
  },
  {
    "dict_code": 250,
    "dict_label": "衡阳市"
  },
  {
    "dict_code": 251,
    "dict_label": "邵阳市"
  },
  {
    "dict_code": 252,
    "dict_label": "岳阳市"
  },
  {
    "dict_code": 253,
    "dict_label": "常德市"
  },
  {
    "dict_code": 254,
    "dict_label": "张家界市"
  },
  {
    "dict_code": 255,
    "dict_label": "益阳市"
  },
  {
    "dict_code": 256,
    "dict_label": "郴州市"
  },
  {
    "dict_code": 257,
    "dict_label": "永州市"
  },
  {
    "dict_code": 258,
    "dict_label": "怀化市"
  },
  {
    "dict_code": 259,
    "dict_label": "娄底市"
  },
  {
    "dict_code": 260,
    "dict_label": "湘西土家族苗族自治州"
  },
  {
    "dict_code": 261,
    "dict_label": "空"
  },
  {
    "dict_code": 262,
    "dict_label": "广州市"
  },
  {
    "dict_code": 263,
    "dict_label": "韶关市"
  },
  {
    "dict_code": 264,
    "dict_label": "深圳市"
  },
  {
    "dict_code": 265,
    "dict_label": "珠海市"
  },
  {
    "dict_code": 266,
    "dict_label": "汕头市"
  },
  {
    "dict_code": 267,
    "dict_label": "佛山市"
  },
  {
    "dict_code": 268,
    "dict_label": "江门市"
  },
  {
    "dict_code": 269,
    "dict_label": "湛江市"
  },
  {
    "dict_code": 270,
    "dict_label": "茂名市"
  },
  {
    "dict_code": 271,
    "dict_label": "肇庆市"
  },
  {
    "dict_code": 272,
    "dict_label": "惠州市"
  },
  {
    "dict_code": 273,
    "dict_label": "梅州市"
  },
  {
    "dict_code": 274,
    "dict_label": "汕尾市"
  },
  {
    "dict_code": 275,
    "dict_label": "河源市"
  },
  {
    "dict_code": 276,
    "dict_label": "阳江市"
  },
  {
    "dict_code": 277,
    "dict_label": "清远市"
  },
  {
    "dict_code": 278,
    "dict_label": "东莞市"
  },
  {
    "dict_code": 279,
    "dict_label": "中山市"
  },
  {
    "dict_code": 280,
    "dict_label": "潮州市"
  },
  {
    "dict_code": 281,
    "dict_label": "揭阳市"
  },
  {
    "dict_code": 282,
    "dict_label": "云浮市"
  },
  {
    "dict_code": 283,
    "dict_label": "空"
  },
  {
    "dict_code": 284,
    "dict_label": "南宁市"
  },
  {
    "dict_code": 285,
    "dict_label": "柳州市"
  },
  {
    "dict_code": 286,
    "dict_label": "桂林市"
  },
  {
    "dict_code": 287,
    "dict_label": "梧州市"
  },
  {
    "dict_code": 288,
    "dict_label": "北海市"
  },
  {
    "dict_code": 289,
    "dict_label": "防城港市"
  },
  {
    "dict_code": 290,
    "dict_label": "钦州市"
  },

  {
    "dict_code": 291,
    "dict_label": "贵港市"
  },
  {
    "dict_code": 292,
    "dict_label": "玉林市"
  },
  {
    "dict_code": 293,
    "dict_label": "百色市"
  },
  {
    "dict_code": 294,
    "dict_label": "贺州市"
  },
  {
    "dict_code": 295,
    "dict_label": "河池市"
  },
  {
    "dict_code": 296,
    "dict_label": "来宾市"
  },
  {
    "dict_code": 297,
    "dict_label": "崇左市"
  },
  {
    "dict_code": 298,
    "dict_label": "空"
  },
  {
    "dict_code": 299,
    "dict_label": "海口市"
  },
  {
    "dict_code": 300,
    "dict_label": "三亚市"
  },
  {
    "dict_code": 301,
    "dict_label": "三沙市"
  },
  {
    "dict_code": 302,
    "dict_label": "儋州市"
  },
  {
    "dict_code": 303,
    "dict_label": "省直辖县级行政区划"
  },
  {
    "dict_code": 304,
    "dict_label": "空"
  },
  {
    "dict_code": 305,
    "dict_label": "市辖区"
  },
  {
    "dict_code": 306,
    "dict_label": "县"
  },
  {
    "dict_code": 307,
    "dict_label": "空"
  },
  {
    "dict_code": 308,
    "dict_label": "成都市"
  },
  {
    "dict_code": 309,
    "dict_label": "自贡市"
  },
  {
    "dict_code": 310,
    "dict_label": "攀枝花市"
  },
  {
    "dict_code": 311,
    "dict_label": "泸州市"
  },
  {
    "dict_code": 312,
    "dict_label": "德阳市"
  },
  {
    "dict_code": 313,
    "dict_label": "绵阳市"
  },
  {
    "dict_code": 314,
    "dict_label": "广元市"
  },
  {
    "dict_code": 315,
    "dict_label": "遂宁市"
  },
  {
    "dict_code": 316,
    "dict_label": "内江市"
  },
  {
    "dict_code": 317,
    "dict_label": "乐山市"
  },
  {
    "dict_code": 318,
    "dict_label": "南充市"
  },
  {
    "dict_code": 319,
    "dict_label": "眉山市"
  },
  {
    "dict_code": 320,
    "dict_label": "宜宾市"
  },
  {
    "dict_code": 321,
    "dict_label": "广安市"
  },
  {
    "dict_code": 322,
    "dict_label": "达州市"
  },
  {
    "dict_code": 323,
    "dict_label": "雅安市"
  },
  {
    "dict_code": 324,
    "dict_label": "巴中市"
  },
  {
    "dict_code": 325,
    "dict_label": "资阳市"
  },
  {
    "dict_code": 326,
    "dict_label": "阿坝藏族羌族自治州"
  },
  {
    "dict_code": 327,
    "dict_label": "甘孜藏族自治州"
  },
  {
    "dict_code": 328,
    "dict_label": "凉山彝族自治州"
  },
  {
    "dict_code": 329,
    "dict_label": "空"
  },
  {
    "dict_code": 330,
    "dict_label": "贵阳市"
  },
  {
    "dict_code": 331,
    "dict_label": "六盘水市"
  },
  {
    "dict_code": 332,
    "dict_label": "遵义市"
  },
  {
    "dict_code": 333,
    "dict_label": "安顺市"
  },
  {
    "dict_code": 334,
    "dict_label": "毕节市"
  },
  {
    "dict_code": 335,
    "dict_label": "铜仁市"
  },
  {
    "dict_code": 336,
    "dict_label": "黔西南布依族苗族自治州"
  },
  {
    "dict_code": 337,
    "dict_label": "黔东南苗族侗族自治州"
  },
  {
    "dict_code": 338,
    "dict_label": "黔南布依族苗族自治州"
  },
  {
    "dict_code": 339,
    "dict_label": "空"
  },
  {
    "dict_code": 340,
    "dict_label": "昆明市"
  },
  {
    "dict_code": 341,
    "dict_label": "曲靖市"
  },
  {
    "dict_code": 342,
    "dict_label": "玉溪市"
  },
  {
    "dict_code": 343,
    "dict_label": "保山市"
  },
  {
    "dict_code": 344,
    "dict_label": "昭通市"
  },
  {
    "dict_code": 345,
    "dict_label": "丽江市"
  },
  {
    "dict_code": 346,
    "dict_label": "普洱市"
  },
  {
    "dict_code": 347,
    "dict_label": "临沧市"
  },
  {
    "dict_code": 348,
    "dict_label": "楚雄彝族自治州"
  },
  {
    "dict_code": 349,
    "dict_label": "红河哈尼族彝族自治州"
  },
  {
    "dict_code": 350,
    "dict_label": "文山壮族苗族自治州"
  },
  {
    "dict_code": 351,
    "dict_label": "西双版纳傣族自治州"
  },
  {
    "dict_code": 352,
    "dict_label": "大理白族自治州"
  },
  {
    "dict_code": 353,
    "dict_label": "德宏傣族景颇族自治州"
  },
  {
    "dict_code": 354,
    "dict_label": "怒江傈僳族自治州"
  },
  {
    "dict_code": 355,
    "dict_label": "迪庆藏族自治州"
  },
  {
    "dict_code": 356,
    "dict_label": "空"
  },
  {
    "dict_code": 357,
    "dict_label": "拉萨市"
  },
  {
    "dict_code": 358,
    "dict_label": "日喀则市"
  },
  {
    "dict_code": 359,
    "dict_label": "昌都市"
  },
  {
    "dict_code": 360,
    "dict_label": "林芝市"
  },
  {
    "dict_code": 361,
    "dict_label": "山南市"
  },
  {
    "dict_code": 362,
    "dict_label": "那曲市"
  },
  {
    "dict_code": 363,
    "dict_label": "阿里地区"
  },
  {
    "dict_code": 364,
    "dict_label": "空"
  },
  {
    "dict_code": 365,
    "dict_label": "西安市"
  },
  {
    "dict_code": 366,
    "dict_label": "铜川市"
  },
  {
    "dict_code": 367,
    "dict_label": "宝鸡市"
  },
  {
    "dict_code": 368,
    "dict_label": "咸阳市"
  },
  {
    "dict_code": 369,
    "dict_label": "渭南市"
  },
  {
    "dict_code": 370,
    "dict_label": "延安市"
  },
  {
    "dict_code": 371,
    "dict_label": "汉中市"
  },
  {
    "dict_code": 372,
    "dict_label": "榆林市"
  },
  {
    "dict_code": 373,
    "dict_label": "安康市"
  },
  {
    "dict_code": 374,
    "dict_label": "商洛市"
  },
  {
    "dict_code": 375,
    "dict_label": "空"
  },
  {
    "dict_code": 376,
    "dict_label": "兰州市"
  },
  {
    "dict_code": 377,
    "dict_label": "嘉峪关市"
  },
  {
    "dict_code": 378,
    "dict_label": "金昌市"
  },
  {
    "dict_code": 379,
    "dict_label": "白银市"
  },
  {
    "dict_code": 380,
    "dict_label": "天水市"
  },
  {
    "dict_code": 381,
    "dict_label": "武威市"
  },
  {
    "dict_code": 382,
    "dict_label": "张掖市"
  },
  {
    "dict_code": 383,
    "dict_label": "平凉市"
  },
  {
    "dict_code": 384,
    "dict_label": "酒泉市"
  },
  {
    "dict_code": 385,
    "dict_label": "庆阳市"
  },
  {
    "dict_code": 386,
    "dict_label": "定西市"
  },
  {
    "dict_code": 387,
    "dict_label": "陇南市"
  },
  {
    "dict_code": 388,
    "dict_label": "临夏回族自治州"
  },
  {
    "dict_code": 389,
    "dict_label": "甘南藏族自治州"
  },
  {
    "dict_code": 390,
    "dict_label": "空"
  },
  {
    "dict_code": 391,
    "dict_label": "西宁市"
  },
  {
    "dict_code": 392,
    "dict_label": "海东市"
  },
  {
    "dict_code": 393,
    "dict_label": "海北藏族自治州"
  },
  {
    "dict_code": 394,
    "dict_label": "黄南藏族自治州"
  },
  {
    "dict_code": 395,
    "dict_label": "海南藏族自治州"
  },
  {
    "dict_code": 396,
    "dict_label": "果洛藏族自治州"
  },
  {
    "dict_code": 397,
    "dict_label": "玉树藏族自治州"
  },
  {
    "dict_code": 398,
    "dict_label": "海西蒙古族藏族自治州"
  },
  {
    "dict_code": 399,
    "dict_label": "空"
  },
  {
    "dict_code": 400,
    "dict_label": "银川市"
  },
  {
    "dict_code": 401,
    "dict_label": "石嘴山市"
  },
  {
    "dict_code": 402,
    "dict_label": "吴忠市"
  },
  {
    "dict_code": 403,
    "dict_label": "固原市"
  },
  {
    "dict_code": 404,
    "dict_label": "中卫市"
  },
  {
    "dict_code": 405,
    "dict_label": "空"
  },
  {
    "dict_code": 406,
    "dict_label": "乌鲁木齐市"
  },
  {
    "dict_code": 407,
    "dict_label": "克拉玛依市"
  },
  {
    "dict_code": 408,
    "dict_label": "吐鲁番市"
  },
  {
    "dict_code": 409,
    "dict_label": "哈密市"
  },
  {
    "dict_code": 410,
    "dict_label": "昌吉回族自治州"
  },
  {
    "dict_code": 411,
    "dict_label": "博尔塔拉蒙古自治州"
  },
  {
    "dict_code": 412,
    "dict_label": "巴音郭楞蒙古自治州"
  },
  {
    "dict_code": 413,
    "dict_label": "阿克苏地区"
  },
  {
    "dict_code": 414,
    "dict_label": "克孜勒苏柯尔克孜自治州"
  },
  {
    "dict_code": 415,
    "dict_label": "喀什地区"
  },
  {
    "dict_code": 416,
    "dict_label": "和田地区"
  },
  {
    "dict_code": 417,
    "dict_label": "伊犁哈萨克自治州"
  },
  {
    "dict_code": 418,
    "dict_label": "塔城地区"
  },
  {
    "dict_code": 419,
    "dict_label": "阿勒泰地区"
  },



  {
    "dict_code": 420,
    "dict_label": "自治区直辖县级行政区划"
  },
  {
    "dict_code": 421,
    "dict_label": "空"
  },
  {
    "dict_code": 422,
    "dict_label": "澳门半岛"
  },
  {
    "dict_code": 423,
    "dict_label": "氹仔"
  },
  {
    "dict_code": 424,
    "dict_label": "路氹城"
  },
  {
    "dict_code": 425,
    "dict_label": "路环"
  },
  {
    "dict_code": 426,
    "dict_label": "空"
  },
  {
    "dict_code": 427,
    "dict_label": "高雄市"
  },
  {
    "dict_code": 428,
    "dict_label": "花莲县"
  },
  {
    "dict_code": 429,
    "dict_label": "基隆市"
  },
  {
    "dict_code": 430,
    "dict_label": "嘉义市"
  },
  {
    "dict_code": 431,
    "dict_label": "嘉义县"
  },
  {
    "dict_code": 432,
    "dict_label": "苗栗县"
  },
  {
    "dict_code": 433,
    "dict_label": "南投县"
  },
  {
    "dict_code": 434,
    "dict_label": "澎湖县"
  },
  {
    "dict_code": 435,
    "dict_label": "屏东县"
  },
  {
    "dict_code": 436,
    "dict_label": "台北市"
  },
  {
    "dict_code": 437,
    "dict_label": "台东县"
  },
  {
    "dict_code": 438,
    "dict_label": "台南市"
  },
  {
    "dict_code": 439,
    "dict_label": "台中市"
  },
  {
    "dict_code": 440,
    "dict_label": "桃园市"
  },
  {
    "dict_code": 441,
    "dict_label": "新北市"
  },
  {
    "dict_code": 442,
    "dict_label": "新竹市"
  },
  {
    "dict_code": 443,
    "dict_label": "新竹县"
  },
  {
    "dict_code": 444,
    "dict_label": "宜兰县"
  },
  {
    "dict_code": 445,
    "dict_label": "云林县"
  },
  {
    "dict_code": 446,
    "dict_label": "彰化县"
  },
  {
    "dict_code": 447,
    "dict_label": "空"
  },
  {
    "dict_code": 448,
    "dict_label": "九龙半岛"
  },
  {
    "dict_code": 449,
    "dict_label": "香港岛"
  },
  {
    "dict_code": 450,
    "dict_label": "新界"
  },
  {
    "dict_code": 451,
    "dict_label": "空"
  },
  {
    "dict_code": 452,
    "dict_label": "Empty"
  },
  {
    "dict_code": 453,
    "dict_label": "Oregon"
  },
  {
    "dict_code": 454,
    "dict_label": "California"
  },
  {
    "dict_code": 455,
    "dict_label": "California"
  },
  {
    "dict_code": 456,
    "dict_label": "California"
  },
  {
    "dict_code": 457,
    "dict_label": "California"
  },
  {
    "dict_code": 458,
    "dict_label": "Washington"
  },
  {
    "dict_code": 459,
    "dict_label": "New York"
  },
  {
    "dict_code": 460,
    "dict_label": "New York"
  },
  {
    "dict_code": 461,
    "dict_label": "Delaware"
  },
  {
    "dict_code": 462,
    "dict_label": "COLORADO"
  },
  {
    "dict_code": 463,
    "dict_label": "COLORADO"
  },
  {
    "dict_code": 464,
    "dict_label": "Empty"
  },
  {
    "dict_code": 465,
    "dict_label": "Empty"
  },
  {
    "dict_code": 466,
    "dict_label": "Empty"
  },
  {
    "dict_code": 467,
    "dict_label": "San Jose"
  },
  {
    "dict_code": 468,
    "dict_label": "Silicon Valley"
  },
  {
    "dict_code": 469,
    "dict_label": "Empty"
  },
  {
    "dict_code": 470,
    "dict_label": "Empty"
  },
  {
    "dict_code": 471,
    "dict_label": "malta"
  },
  {
    "dict_code": 472,
    "dict_label": "Empty"
  },
  {
    "dict_code": 473,
    "dict_label": "Empty"
  },
  {
    "dict_code": 474,
    "dict_label": "SPRINGS"
  },
  {
    "dict_code": 475,
    "dict_label": "Empty"
  },
  {
    "dict_code": 476,
    "dict_label": "Empty"
  },
  {
    "dict_code": 477,
    "dict_label": "New Albany"
  },
  {
    "dict_code": 478,
    "dict_label": "Empty"
  },
  {
    "dict_code": 479,
    "dict_label": "Empty"
  },











```


# sql

```


  
select  
    sdd.dict_code,sdd.dict_sort,  
    case  
        when slc.language_value is not null then slc.language_value else sdd.dict_label end as dict_lable,  
#     slc.language_value as dict_label,  
    sdd.dict_value,sdd.dict_type,sdd.css_class,sdd.list_class,sdd.is_default,sdd.status,sdd.create_by,sdd.create_time,sdd.remark  
       from sys_dict_data sdd  
           left join  sys_language_config slc on  
           slc.relation_id = sdd.dict_code and  
           slc.language_category = 'en' -- 这里换成前端传入的数据  
           where sdd.dict_type = 'pmdp:main:type:001'  
  
  
  
select  
    sdd.dict_code,sdd.dict_sort,  
    case  
        when slc.language_value is not null then slc.language_value  
        else sdd.dict_label  
        end as dict_lable,  
    sdd.dict_value,sdd.dict_type,sdd.css_class,sdd.list_class,sdd.is_default,sdd.status,sdd.create_by,sdd.create_time,sdd.remark  
       from sys_dict_data sdd  
           left join  sys_language_config slc on  
           slc.relation_id = sdd.dict_code and  
           slc.language_category = #{lang} -- 这里换成前端传入的数据  
  
select  
          sdd.dict_code,sdd.dict_sort,  
          case  
             when slc.language_value is not null then slc.language_value  
             else sdd.dict_label  
             end as dict_lable,  
          sdd.dict_value,sdd.dict_type,sdd.css_class,sdd.list_class,sdd.is_default,sdd.status,sdd.create_by,sdd.create_time,sdd.remark  
       from sys_dict_data sdd  
              left join  sys_language_config slc on  
                slc.relation_id = sdd.dict_code and  
                slc.language_category = 'en'  
       where sdd.status = '0' and sdd.dict_type = 'pmdp:main:type:001' order by dict_sort asc  
  
  
-- 单位  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('en', 'Unit', '1', 'sys_dict_data', '2025-08-07 17:51:04');  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('vi', 'Đơn vị', '1', 'sys_dict_data', '2025-08-07 17:51:04');  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('es', 'Unidad', '1', 'sys_dict_data', '2025-08-07 17:51:04');  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('id', 'Unit', '1', 'sys_dict_data', '2025-08-07 17:51:04');  
  
  
  
SHOW TABLE STATUS LIKE 'sys_language_config';  
  
  
-- 浙江省  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('en', 'Zhejiang Province', '24', 'sys_dict_data', '2025-08-07 17:51:04');  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('vi', 'Tỉnh Chiết Giang', '24', 'sys_dict_data', '2025-08-07 17:51:04');  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('es', 'Provincia de Zhejiang', '24', 'sys_dict_data', '2025-08-07 17:51:04');  
INSERT INTO sys_language_config (language_category, language_value, relation_id, relation_table, operate_time) VALUES ('id', 'Provinsi Zhejiang', '24', 'sys_dict_data', '2025-08-07 17:51:04');


```










# end







```
        location ^~ /plan/portal/api {

            proxy_set_header X-Real-IP $remote_addr;

            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

            proxy_set_header Host $host;

            proxy_pass http://127.0.0.1:8110/;

        }
```

![](assets/Pasted%20image%2020250729192932.png)






ds说的有些文件不能
lingma找的一些不能

```
import i18n from '@/lang';
```

加载更多  
清除  
抱歉！您访问的页面  
失联  
啦 ...  
返回上一页  
进入首页
欢迎使用合作方主数据平台  
renren-fast-vue基于vue、element-ui构建开发，实现renren-fast后台管理前端功能，提供一套更优的前端解决方案。  
管理员登录  
登录  
布局设置  
'1. 此Demo只提供ECharts官方使用文档，入门部署和体验功能。具体使用请参考：http://echarts.baidu.com/index.html'
合作方
合作方-首页
物料批量查询
选中
个物料
导出文件失败！
ATP数据收集
间隔周期未维护
理货周期未维护
UPH未维护
帐号
密码
验证码
帐号不能为空
密码不能为空
验证码不能为空
导航条类型
侧边栏皮肤
提示：
折线图堆叠
邮件营销
联盟广告
视频广告
直接访问
搜索引擎
周一
周二
周三
周四
周五
周六
周日
邮件营销
总量
直接访问
百度
谷歌
必应
其他
广告
1990 与 2015 年各国家人均寿命与 GDP


'加载更多'  
'清除'  
'抱歉！您访问的页面'  
'失联'  
'啦 ...'  
'返回上一页'  
'进入首页'  
'欢迎使用合作方主数据平台'  
'renren-fast-vue基于vue、element-ui构建开发，实现renren-fast后台管理前端功能，提供一套更优的前端解决方案。'  
'管理员登录'  
'登录'  
'布局设置'  
'1. 此Demo只提供ECharts官方使用文档，入门部署和体验功能。具体使用请参考：http://echarts.baidu.com/index.html'  
'合作方'  
'合作方-首页'  
'物料批量查询'  
'选中'  
'个物料'  
'导出文件失败！'  
'ATP数据收集'  
'间隔周期未维护'  
'理货周期未维护'  
'UPH未维护'  
'帐号'  
'密码'  
'验证码'  
'帐号不能为空'  
'密码不能为空'  
'验证码不能为空'  
'导航条类型'  
'侧边栏皮肤'  
'提示：'  
'折线图堆叠'  
'邮件营销'  
'联盟广告'  
'视频广告'  
'直接访问'  
'搜索引擎'  
'周一'  
'周二'  
'周三'  
'周四'  
'周五'  
'周六'  
'周日'  
'邮件营销'  
'总量'  
'直接访问'  
'百度'  
'谷歌'  
'必应'  
'其他'  
'广告'  
'1990 与 2015 年各国家人均寿命与 GDP'

```java
loadMore: '加载更多',  
clear: '清除',  
pageNotFound: '抱歉！您访问的页面',  
disconnected: '失联',  
ellipsis: '啦 ...',  
goBack: '返回上一页',  
goHome: '进入首页',  
welcomeMessage: '欢迎使用合作方主数据平台',  
renrenFastVueDesc: 'renren-fast-vue基于vue、element-ui构建开发，实现renren-fast后台管理前端功能，提供一套更优的前端解决方案。',  
adminLogin: '管理员登录',  
login: '登录',  
layoutSettings: '布局设置',  
echartsDemoNote: '1. 此Demo只提供ECharts官方使用文档，入门部署和体验功能。具体使用请参考：http://echarts.baidu.com/index.html',  
partner: '合作方',  
partnerHome: '合作方-首页',  
batchMaterialQuery: '物料批量查询',  
selected: '选中',  
materialCount: '个物料',  
exportFailed: '导出文件失败！',  
atpDataCollection: 'ATP数据收集',  
intervalNotSet: '间隔周期未维护',  
tallyCycleNotSet: '理货周期未维护',  
uphNotSet: 'UPH未维护',  
username: '帐号',  
password: '密码',  
captcha: '验证码',  
usernameNotNull: '帐号不能为空',  
passwordNotNull: '密码不能为空',  
captchaNotNull: '验证码不能为空',  
navbarType: '导航条类型',  
sidebarSkin: '侧边栏皮肤',  
tip: '提示：',  
lineStack: '折线图堆叠',  
emailMarketing: '邮件营销',  
unionAd: '联盟广告',  
videoAd: '视频广告',  
directAccess: '直接访问',  
searchEngine: '搜索引擎',  
monday: '周一',  
tuesday: '周二',  
wednesday: '周三',  
thursday: '周四',  
friday: '周五',  
saturday: '周六',  
sunday: '周日',  
total: '总量',  
baidu: '百度',  
google: '谷歌',  
bing: '必应',  
others: '其他',  
advertisement: '广告',  
lifeExpectancyVsGDP: '1990 与 2015 年各国家人均寿命与 GDP'  
```
```
this.$t('manager.password')
```
```
this.$t('common.password')
```
```
{{ this.$t('common.loadMore') }}
```
```
{{ this.$t('manager.renrenFastVueDesc') }}
```
```javascript
common
loadMore: '加载更多',  
clear: '清除',  
pageNotFound: '抱歉！您访问的页面',  
disconnected: '失联',  
ellipsis: '啦 ...',  
goBack: '返回上一页',  
goHome: '进入首页',  
adminLogin: '管理员登录',  
login: '登录',  
username: '帐号',  
password: '密码',  
captcha: '验证码',  
usernameNotNull: '帐号不能为空',  
passwordNotNull: '密码不能为空',  
captchaNotNull: '验证码不能为空',  
monday: '周一',  
tuesday: '周二',  
wednesday: '周三',  
thursday: '周四',  
friday: '周五',  
saturday: '周六',  
sunday: '周日',
layoutSettings: '布局设置',  
navbarType: '导航条类型',  
sidebarSkin: '侧边栏皮肤',  
tip: '提示：',  

manager
welcomeMessage: '欢迎使用合作方主数据平台',  
renrenFastVueDesc: 'renren-fast-vue基于vue、element-ui构建开发，实现renren-fast后台管理前端功能，提供一套更优的前端解决方案。', 
echartsDemoNote: '1. 此Demo只提供ECharts官方使用文档，入门部署和体验功能。具体使用请参考：http://echarts.baidu.com/index.html',  
partner: '合作方',  
partnerHome: '合作方-首页',  
batchMaterialQuery: '物料批量查询',  
selected: '选中',  
materialCount: '个物料',  
exportFailed: '导出文件失败！',  

atpDataCollection: 'ATP数据收集',  

intervalNotSet: '间隔周期未维护',  
tallyCycleNotSet: '理货周期未维护',  
uphNotSet: 'UPH未维护',  

lineStack: '折线图堆叠',  
emailMarketing: '邮件营销',  
unionAd: '联盟广告',  
videoAd: '视频广告',  
directAccess: '直接访问',  
searchEngine: '搜索引擎', 
total: '总量',  
baidu: '百度',  
google: '谷歌',  
bing: '必应',  
others: '其他',  
advertisement: '广告',  
lifeExpectancyVsGDP: '1990 与 2015 年各国家人均寿命与 GDP' 
visitSource: '访问来源'
```

'邮件营销'  
'直接访问' 


```java
loadMore: 'Load More',  
clear: 'Clear',  
pageNotFound: 'Sorry, the page you visited',  
disconnected: 'Disconnected',  
ellipsis: '...',  
goBack: 'Go Back',  
goHome: 'Go to Home',  
adminLogin: 'Admin Login',  
login: 'Login', 
username: 'Username',  
password: 'Password',  
captcha: 'Verification Code',  
usernameNotNull: 'Username cannot be empty',  
passwordNotNull: 'Password cannot be empty',  
captchaNotNull: 'Verification code cannot be empty',  
monday: 'Monday',  
tuesday: 'Tuesday',  
wednesday: 'Wednesday',  
thursday: 'Thursday',  
friday: 'Friday',  
saturday: 'Saturday',  
sunday: 'Sunday',  
layoutSettings: 'Layout Settings',
navbarType: 'Navbar Type',  
sidebarSkin: 'Sidebar Skin',  
tip: 'Tip:', 

welcomeMessage: 'Welcome to Partner Master Data Platform',  
renrenFastVueDesc: 'renren-fast-vue is developed based on Vue and Element-UI, providing an optimized frontend solution for the renren-fast backend management system.',  
echartsDemoNote: '1. This Demo only provides ECharts official documentation, basic deployment, and demo features. For details, refer to: http://echarts.baidu.com/index.html',  
partner: 'Partner',  
partnerHome: 'Partner - Home',  
batchMaterialQuery: 'Batch Material Query',  
selected: 'Selected',  
materialCount: 'materials',  
exportFailed: 'Export Failed!',  
atpDataCollection: 'ATP Data Collection',  
intervalNotSet: 'Interval Not Set',  
tallyCycleNotSet: 'Tally Cycle Not Set',  
uphNotSet: 'UPH Not Set', 
lineStack: 'Line Stack',  
emailMarketing: 'Email Marketing',  
unionAd: 'Union Ad',  
videoAd: 'Video Ad',  
directAccess: 'Direct Access',  
searchEngine: 'Search Engine',  
total: 'Total',  
baidu: 'Baidu',  
google: 'Google',  
bing: 'Bing',  
others: 'Others',  
advertisement: 'Advertisement',  
lifeExpectancyVsGDP: 'Life Expectancy vs GDP by Country (1990 & 2015)'  
visitSource: 'Visit Source'

```

```
收料模板
收料模板列表
新增收料模板
编辑收料模板
收料模板详情
收料计划
收料计划列表
收料计划历史
系统管理
管理员列表
角色管理
菜单管理
SQL监控
定时任务
参数管理
文件上传
系统日志

```

```
'receivingTemplate': '收料模板',
'receivingTemplateList': '收料模板列表',
'addReceivingTemplate': '新增收料模板',
'editReceivingTemplate': '编辑收料模板',
'receivingTemplateDetail': '收料模板详情',
'receivingPlan': '收料计划',
'receivingPlanList': '收料计划列表',
'receivingPlanHistory': '收料计划历史',
'systemManagement': '系统管理',
'adminList': '管理员列表',
'roleManagement': '角色管理',
'menuManagement': '菜单管理',
'sqlMonitor': 'SQL监控',
'timedTask': '定时任务',
'parameterManagement': '参数管理',
'fileUpload': '文件上传',
'sysLog': '系统日志'
```
```
MessageUtils.message("")
```

```
'receivingTemplate': 'Receiving Template',
'receivingTemplateList': 'Receiving Template List',
'addReceivingTemplate': 'Add Receiving Template',
'editReceivingTemplate': 'Edit Receiving Template',
'receivingTemplateDetail': 'Receiving Template Details',
'receivingPlan': 'Receiving Plan',
'receivingPlanList': 'Receiving Plan List',
'receivingPlanHistory': 'Receiving Plan History',
'systemManagement': 'System Management',
'adminList': 'Administrator List',
'roleManagement': 'Role Management',
'menuManagement': 'Menu Management',
'sqlMonitor': 'SQL Monitor',
'timedTask': 'Scheduled Task',
'parameterManagement': 'Parameter Management',
'fileUpload': 'File Upload',
'sysLog': 'System Log'
```

```
this.$t('receive.password')
```


```
this.$t('sys.password')
```
```java
{
  receivingTemplate: '收料模板',
  receivingTemplateList: '收料模板列表',
  addReceivingTemplate: '新增收料模板',
  editReceivingTemplate: '编辑收料模板',
  receivingTemplateDetail: '收料模板详情',
  receivingPlan: '收料计划',
  receivingPlanList: '收料计划列表',
  receivingPlanHistory: '收料计划历史'

  systemManagement: '系统管理',
  adminList: '管理员列表',
  roleManagement: '角色管理',
  menuManagement: '菜单管理',
  sqlMonitor: 'SQL监控',
  timedTask: '定时任务',
  parameterManagement: '参数管理',
  fileUpload: '文件上传',
  sysLog: '系统日志'

  receivingTemplate: 'Receiving Template',
  receivingTemplateList: 'Receiving Template List',
  addReceivingTemplate: 'Add Receiving Template',
  editReceivingTemplate: 'Edit Receiving Template',
  receivingTemplateDetail: 'Receiving Template Details',
  receivingPlan: 'Receiving Plan',
  receivingPlanList: 'Receiving Plan List',
  receivingPlanHistory: 'Receiving Plan History'

  systemManagement: 'System Management',
  adminList: 'Administrator List',
  roleManagement: 'Role Management',
  menuManagement: 'Menu Management',
  sqlMonitor: 'SQL Monitor',
  timedTask: 'Scheduled Task',
  parameterManagement: 'Parameter Management',
  fileUpload: 'File Upload',
  sysLog: 'System Log'
}
```




# 职员、非制造O 编制数据返回  

```
package com.huaqin.hcm.entity.vo.plait;  
  
  
import lombok.Data;  
import lombok.EqualsAndHashCode;  
  
import java.io.Serializable;  
  
/**  
 * 职员、非制造O 编制数据返回  
 *  
 * @author 100563122  
 * @date 2025/7/21  
 */@Data  
@EqualsAndHashCode(callSuper = false)  
public class PlaitZyAndOlVo implements Serializable {  
  
    private final static long serialVersionUID = 1L;  
  
    /**  
     * 组织Id  
     */    private String orgHid;  
    /**  
     * 组织编码  
     */  
    private String orgCode;  
    /**  
     * 组织名称  
     */  
    private String orgName;  
    /**  
     * 组织层级编码  
     */  
    private String orgLevel;  
    /**  
     * 组织层级名称  
     */  
    private String orgLevelName;  
    /**  
     * 职员编制 数  
     */  
    private Long empNumZy;  
    /**  
     * 非制造O 编制 数  
     */  
    private Long empNumOl;  
  
}
```


```
<?xml version="1.0" encoding="UTF-8"?>  
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">  
<mapper namespace="com.huaqin.hcm.mapper.plait.PlaitFormMapper">  
  
    <select id="selectDeptHidList" resultType="String" fetchSize="10000">  
        SELECT  
            R.C_ORG_HID        FROM            TB_ORG_UNITRELATION R,            TB_ORG_UNITRELATION R1        WHERE            R.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
            AND R.C_END_DATE &gt; TRUNC(SYSDATE)  
            AND R.C_STATUS = '1'            AND R1.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
            AND R1.C_END_DATE > TRUNC(SYSDATE)            AND R1.C_STATUS = '1'            AND R.C_DIM_HID = '65ca64ab44274d789f8e958abbddc406'            AND R1.C_DIM_HID = '65ca64ab44274d789f8e958abbddc406'            AND INSTR(R.C_PATH_CODE, R1.C_PATH_CODE) > 0            <if test="orgHid != null and orgHid != ''">  
                AND R1.C_ORG_HID = #{orgHid}  
            </if>  
    </select>  
  
    <sql id="DEPT_HID_LIST">  
        <foreach collection="deptHidList" item="deptHid" separator="," index="index">  
            #{deptHid}  
        </foreach>  
    </sql>  
  
    <select id="listSchoolOfferCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
             NVL(OA.C_DEPT_HID,OA.C_HID) dept_hid             , PH.C_EMPLOYEE_TYPE employee_type             , COUNT(1) cnt        FROM TB_STA_PREPARE_HIRE PH,             TB_ORG_ORGUNITALL OA        WHERE PH.C_DEPT_HID = OA.C_HID          AND PH.C_STATUS = 'preparing'          AND PH.C_YONGGONGXINGSHI in('1','5')          AND PH.C_ZHAOPINLEIXING IN ('3', '4')        GROUP BY OA.C_DEPT_HID, OA.C_HID, PH.C_EMPLOYEE_TYPE    </select>  
  
    <select id="listSocialOfferEmpCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(OA.C_DEPT_HID,  OA.C_HID) dept_hid,            PH.C_EMPLOYEE_TYPE employee_type,            COUNT(1) cnt        FROM            TB_STA_PREPARE_HIRE PH,            TB_ORG_ORGUNITALL OA        WHERE PH.C_DEPT_HID = OA.C_HID          AND PH.C_STATUS = 'preparing'          AND PH.C_EMPLOYEE_TYPE = '0'          AND PH.C_YONGGONGXINGSHI in('1','5')          AND PH.C_ZHAOPINLEIXING NOT IN ('3', '4')        GROUP BY            OA.C_DEPT_HID,            OA.C_HID,            PH.C_EMPLOYEE_TYPE    </select>  
  
    <select id="listSocialOfferOEmpCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
        NVL(OAL.C_DEPT_HID,  OAL.C_HID) dept_hid,        H.C_EMPLOYEE_TYPE employee_type,        JC.C_NAME empRank,        POS.C_POST_LEVEL posLevel,        POS.C_NAME posName,        COUNT(1) cnt        FROM        TB_ORG_ORGUNIT U,        TB_ORG_ORGUNITALL OAL,        TB_STA_PREPARE_HIRE H        LEFT JOIN TB_ORG_POSITION POS ON H.C_POSITION_ID = POS.C_HID        LEFT JOIN TB_ORG_JOB_CLASS JC ON H.C_POS_LEVEL = JC.C_OID        WHERE OAL.C_HID = H.C_DEPT_HID        AND H.C_STATUS = 'preparing'        AND OAL.C_HID = U.C_HID        AND U.C_STATUS = '1'        AND U.C_BEGIN_DATE &lt;=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)        AND POS.C_BEGIN_DATE &lt;=TRUNC(SYSDATE)  
        AND POS.C_END_DATE>=TRUNC(SYSDATE)        AND (POS.C_EMP_TYPE = '4'AND (H.C_YONGGONGXINGSHI = '1' OR H.C_ZHAOPINQUDAO = '100006003' ))        GROUP BY        OAL.C_DEPT_HID,        OAL.C_HID,        H.C_EMPLOYEE_TYPE,        JC.C_NAME,        POS.C_POST_LEVEL,        POS.C_NAME    </select>  
  
    <select id="listCorePercent" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo" fetchSize="2000">  
        SELECT  
            NVL(EGA.C_DEPTHID, EGA.C_REALUNITHID) dept_hid,            EG.C_EMPLOYEE_TYPE employee_type,            EG.C_EMPTYPE  smallEmployeeType,            JG.C_VOCATIONAL_LEVEL vocational_level,            EC.C_LABOR_TYPE labor_type,            EG.C_WORKERS_TYPE workers_type,            EMP.C_ZHAOPINQUDAO zhao_pin_qu_dao,            EG.C_EMPLOYEE_STATUS employee_status,            ORGJOB.C_NAME job_name,            EG.C_O15 O15,            COUNT(1) cnt        FROM            TB_STA_EMP_ORGAPPEND EGA,            TB_STA_EMP_ORG EG,            TB_STA_EMP_CLASS EC,            TB_STA_POS_LEVEL SPL,            TB_ORG_JOB_CLASS JG,            TB_STA_EMP EMP,            TB_ORG_JOB ORGJOB        WHERE EG.C_OID = EGA.C_OID            AND  EMP.C_OID = EG.C_EMPLOYEE_ID            AND EG.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
            AND EG.C_END_DATE > TRUNC(SYSDATE)            AND EG.C_EMPLOYEE_STATUS IN ('2' , '11')            AND EG.C_DEPT_TYPE = '1'            AND EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID            AND SPL.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE)  
            AND SPL.C_END_DATE >= TRUNC(CURRENT_DATE)            AND SPL.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID            AND JG.C_OID = SPL.C_POS_LEVEL            AND EC.C_BEGIN_DATE &lt;= TRUNC(SYSDATE) AND EC.C_END_DATE >= TRUNC(SYSDATE)  
            AND ORGJOB.C_HID = EG.C_JOB_HID            AND ORGJOB.C_EFFECTIVE_DATE_BEGIN &lt;= TRUNC(SYSDATE)  
            AND ORGJOB.C_EFFECTIVE_DATE_END >= TRUNC(SYSDATE)            AND ORGJOB.C_STATUS = '1'        GROUP BY            EGA.C_DEPTHID,            EGA.C_REALUNITHID,            EG.C_EMPLOYEE_TYPE,            EG.C_EMPTYPE,            JG.C_VOCATIONAL_LEVEL,            EC.C_LABOR_TYPE,            EG.C_WORKERS_TYPE,            EMP.C_ZHAOPINLEIXING,            EG.C_EMPLOYEE_STATUS,            EMP.C_ZHAOPINQUDAO,            EG.C_O15,            ORGJOB.C_NAME    </select>  
  
    <select id="listDeptPlaitCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo" fetchSize="2000">  
        SELECT NVL(PU.C_DEPTHID,PU.C_RDEPTHID)  dept_hid,  
               NVL(SUM(CASE                           WHEN ((PU.C_RDEPTLEVEL IN ('4', '5') AND PA.C_TYPE = '0') OR                                 (PU.C_RDEPTLEVEL IN ('2', '3', '7') AND PA.C_TYPE = '1')) AND                                PA.C_CONTROL_OBJECT = '0' THEN                               PA.C_WORKFORCE_NUMBER                           ELSE                               0                   END),                   0) fobzs_cnt,               NVL(SUM(CASE                           WHEN ((PU.C_RDEPTLEVEL IN ('4', '5') AND PA.C_TYPE = '0') OR                                 (PU.C_RDEPTLEVEL IN ('2', '3', '7') AND PA.C_TYPE = '1')) AND                                PA.C_CONTROL_OBJECT = '4' THEN                               PA.C_WORKFORCE_NUMBER                           ELSE                               0                   END),                   0) obzs_cnt        FROM TB_ORG_WORKFORCEUNIT PU                 LEFT JOIN (SELECT                                OA.C_DEPT_HID,                                OA.C_HID,                                OW.C_TYPE,                                P.C_CONTROL_OBJECT,                                P.C_WORKFORCE_NUMBER                            FROM TB_ORG_DEPT_CONTROLITEM P,                                 TB_ORG_DEPT_WORKFORCE   OW,                                 TB_ORG_WORKFORCE_PLAN   PA,                                 TB_ORG_ORGUNITALL       OA                            WHERE P.C_DEPT_WORK_FORCE_ID = OW.C_OID                              AND OW.C_WORKFORCE_PLAN_ID = PA.C_HID                              AND PA.C_EFFECTIVE_STATUS = '1'                              AND P.C_DEPARTMENT_HID = OA.C_HID) PA                           ON PU.C_RDEPTHID = PA.C_HID        GROUP BY PU.C_DEPTHID, PU.C_RDEPTHID    </select>  
  
    <select id="listLastYearCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(EGA.C_DEPTHID,EGA.C_REALUNITHID) dept_hid,            EG.C_EMPLOYEE_TYPE employee_type,            COUNT(1) cnt        FROM            TB_STA_EMP_ORGAPPEND EGA,            TB_STA_EMP_ORG EG,            TB_STA_EMP_CLASS EC,            TB_STA_EMP EMP        WHERE EG.C_OID = EGA.C_OID          AND EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID          AND EMP.C_OID = EG.C_EMPLOYEE_ID          AND EG.C_BEGIN_DATE &lt;= TRUNC(SYSDATE, 'yyyy') -1  
          AND EG.C_END_DATE > TRUNC(SYSDATE, 'yyyy') -1          AND EG.C_DEPT_TYPE = '1'          AND EG.C_EMPLOYEE_STATUS in('2','11')          AND ((EG.C_EMPLOYEE_TYPE = '0'  AND EC.C_LABOR_TYPE in('1','5'))          OR (EG.C_EMPTYPE = '4' AND (EC.C_LABOR_TYPE = '1' OR EMP.C_ZHAOPINQUDAO = '100006003')))          AND EC.C_BEGIN_DATE &lt;= TRUNC(SYSDATE, 'yyyy') -1 AND EC.C_END_DATE >= TRUNC(SYSDATE, 'yyyy') -1  
        GROUP BY            EGA.C_DEPTHID,            EGA.C_REALUNITHID,            EG.C_EMPLOYEE_TYPE    </select>  
  
    <select id="listTranCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
               EG.C_EMPLOYEE_TYPE                    employee_type,               NVL(EGAIN.C_DEPT_HID, EGAIN.C_HID) new_dept,               NVL(EGAOUT.C_DEPT_HID, EGAOUT.C_HID) pre_dept,               EGAIN.c_Center_Hid new_center,               EGAIN.C_SYSTEM_HID new_system,               EGAIN.C_F_SYSTEM_HID new_f_system,               EGAOUT.C_CENTER_HID pre_center,               EGAOUT.C_SYSTEM_HID pre_system,               EGAOUT.C_F_SYSTEM_HID pre_f_system,               EG.C_O15 o15,               1 cnt        FROM TB_STA_INTRANSFERINFO TRAN        INNER JOIN TB_ORG_POSITION OPM ON TRAN.C_NEW_POSHID=OPM.C_HID AND OPM.C_BEGIN_DATE &lt;= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        INNER JOIN TB_ORG_ORGUNITALL EGAIN ON EGAIN.C_HID = TRAN.C_NEW_UNITHID        INNER JOIN TB_STA_EMP_ORG EG ON EG.C_EMPLOYEE_ID = TRAN.C_EMPLOYEE_ID        INNER JOIN TB_STA_EMP_CLASS EC ON EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        INNER JOIN TB_ORG_ORGUNITALL EGAOUT ON  EGAOUT.C_HID = TRAN.C_PRE_UNITHID        INNER JOIN TB_STA_EMP EMP ON EMP.C_OID = EG.C_EMPLOYEE_ID        WHERE EG.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
          AND EG.C_END_DATE > TRUNC(SYSDATE)          AND EG.C_DEPT_TYPE = '1'          and EG.C_EMPLOYEE_STATUS in('2','11')          AND ((EG.C_EMPLOYEE_TYPE = '0'  AND EC.C_LABOR_TYPE in('1','5'))          or(OPM.C_EMP_TYPE = '4' AND (EC.C_LABOR_TYPE = '1' OR EMP.C_ZHAOPINQUDAO = '100006003' ) )          )          AND NVL(EGAOUT.C_DEPT_HID, EGAOUT.C_HID) != NVL(EGAIN.C_DEPT_HID, EGAIN.C_HID)  
    </select>  
  
    <select id="listTranCntEffect" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT EG.C_EMPLOYEE_TYPE employee_type,  
        NVL(EGAIN.C_DEPT_HID, EGAIN.C_HID) new_dept,        NVL(EGAOUT.C_DEPT_HID, EGAOUT.C_HID) pre_dept,        EGAIN.c_Center_Hid new_center,        EGAIN.C_SYSTEM_HID new_system,        EGAIN.C_F_SYSTEM_HID new_f_system,        EGAOUT.C_CENTER_HID pre_center,        EGAOUT.C_SYSTEM_HID pre_system,        EGAOUT.C_F_SYSTEM_HID pre_f_system,        TRAN.c_employee_id,        EG.C_O15 o15,        1 cnt        FROM TB_STA_EMP_TURNOVER TRAN        INNER JOIN TB_STA_EMP_ORG EG        ON EG.C_EMPLOYEE_ID = TRAN.C_EMPLOYEE_ID        INNER JOIN TB_ORG_ORGUNITALL EGAIN        ON EGAIN.C_HID = TRAN.C_NEW_DEPT_HID        INNER JOIN TB_STA_EMP_CLASS EC        ON EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID        AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE)  
        AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)        INNER JOIN TB_ORG_ORGUNITALL EGAOUT        ON EGAOUT.C_HID = TRAN.C_PREV_DEPT_HID        INNER JOIN TB_STA_EMP EMP        ON EMP.C_OID = EG.C_EMPLOYEE_ID        WHERE TRAN.C_BEGIN_DATE > TRUNC(SYSDATE)        and EG.C_BEGIN_DATE > TRUNC(SYSDATE)        AND EG.C_END_DATE > TRUNC(SYSDATE)        AND EG.C_DEPT_TYPE = '1'        and EG.C_EMPLOYEE_STATUS in('2','11')        AND ((EG.C_EMPLOYEE_TYPE = '0'  AND EC.C_LABOR_TYPE in('1','5'))        or(EG.C_EMPTYPE = '4' AND (EC.C_LABOR_TYPE = '1' OR EMP.C_ZHAOPINQUDAO = '100006003' ) )        )        AND NVL(EGAOUT.C_DEPT_HID, EGAOUT.C_HID) !=        NVL(EGAIN.C_DEPT_HID, EGAIN.C_HID)    </select>  
  
    <select id="listHireCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(EGA.C_DEPTHID, EGA.C_REALUNITHID) dept_hid,            EG.C_EMPLOYEE_TYPE employee_type,            JG.C_VOCATIONAL_LEVEL vocational_level,            EG.C_O15 o15,            COUNT(1) cnt        FROM            (            select                distinct F.C_CREATOR C_EMPLOYEE_ID            from TB_INF_OA_FLOWINFO F            where F.C_STATUS = 1            AND F.C_FLOW_ID in (                <foreach collection="flowIdList" item="flowId" separator="," index="flowId">  
                    #{flowId}  
                </foreach>  
            )  
            AND F.C_NODE_NAME != '创建'  
            ) HIRE ,            TB_STA_EMP_ORGAPPEND EGA,            TB_STA_EMP_ORG EG,            TB_STA_EMP_CLASS EC,            TB_STA_POS_LEVEL SPL,            TB_ORG_JOB_CLASS JG,            TB_STA_EMP EMP        WHERE EG.C_OID = EGA.C_OID            AND EMP.C_OID = EG.C_EMPLOYEE_ID            AND EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID            AND EG.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
            AND EG.C_END_DATE > TRUNC(SYSDATE)            AND EG.C_DEPT_TYPE = '1'            and EG.C_EMPLOYEE_STATUS in('2','11')            AND ((EG.C_EMPLOYEE_TYPE = '0'  AND EC.C_LABOR_TYPE in('1','5'))            or(EG.C_EMPTYPE = '4' AND (EC.C_LABOR_TYPE = '1' OR EMP.C_ZHAOPINQUDAO = '100006003' ) )            )            AND HIRE.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID            AND SPL.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE)  
            AND SPL.C_END_DATE >= TRUNC(CURRENT_DATE)            AND SPL.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID            AND JG.C_OID = SPL.C_POS_LEVEL            AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        GROUP BY            EGA.C_DEPTHID,            EGA.C_REALUNITHID,            EG.C_EMPLOYEE_TYPE,            JG.C_VOCATIONAL_LEVEL,            EG.C_O15    </select>  
  
    <select id="listSubSysInfo" resultType="com.huaqin.hcm.entity.po.plait.OrgWorkForceUnit">  
        SELECT  
            O.C_F_SYSTEM_HID sub_system_hid,            O.C_F_SYSTEM_NAME sub_system_name,            O.C_F_SYSTEM_CODE sub_system_code,            W.C_RDEPTHID r_dept_hid        FROM TB_ORG_WORKFORCEUNIT W        INNER JOIN TB_ORG_ORGUNITALL O ON W.C_RDEPTHID = O.C_HID        WHERE O.C_F_SYSTEM_HID IS NOT NULL    </select>  
  
    <select id="listSchoolOfferAreaCnt"  
            resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(OA.C_DEPT_HID,OA.C_HID) dept_hid             , NVL(PH.C_YEWUQUYU, '10') business_area             , PH.C_EMPLOYEE_TYPE employee_type             , COUNT(1) cnt        FROM TB_STA_PREPARE_HIRE PH,             TB_ORG_ORGUNITALL OA        WHERE PH.C_DEPT_HID = OA.C_HID          AND PH.C_STATUS = 'preparing'          AND PH.C_YONGGONGXINGSHI in('1','5')          AND PH.C_ZHAOPINLEIXING IN ('3', '4')        GROUP BY OA.C_DEPT_HID, OA.C_HID, PH.C_YEWUQUYU, PH.C_EMPLOYEE_TYPE    </select>  
    <!--查询在途社招 区域（职员）-->  
    <select id="listSocialOfferAreaCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(OA.C_DEPT_HID,OA.C_HID) deptHid            , NVL(PH.C_YEWUQUYU, '10') businessArea            , PH.C_EMPLOYEE_TYPE employeeType            , COUNT(1) cnt        FROM TB_STA_PREPARE_HIRE PH        INNER JOIN TB_ORG_ORGUNITALL OA ON PH.C_DEPT_HID = OA.C_HID        WHERE  PH.C_STATUS = 'preparing'        AND PH.C_EMPLOYEE_TYPE = '0'        AND PH.C_YONGGONGXINGSHI in('1','5')        AND PH.C_ZHAOPINLEIXING NOT IN ('3', '4')        GROUP BY OA.C_DEPT_HID, OA.C_HID, PH.C_YEWUQUYU, PH.C_EMPLOYEE_TYPE    </select>  
  
    <!--查询在途社招 区域（非制造O）-->  
    <select id="listSocialOfferOAreaCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
        NVL(OAL.C_DEPT_HID,  OAL.C_HID) deptHid,        NVL(H.C_YEWUQUYU, '10') businessArea,        H.C_EMPLOYEE_TYPE employeeType,        COUNT(1) cnt        FROM        TB_ORG_ORGUNIT U,        TB_ORG_ORGUNITALL OAL,        TB_STA_PREPARE_HIRE H        LEFT JOIN TB_ORG_POSITION POS ON H.C_POSITION_ID = POS.C_HID        WHERE OAL.C_HID = H.C_DEPT_HID        AND H.C_STATUS = 'preparing'        AND OAL.C_HID = U.C_HID        AND U.C_STATUS = '1'        AND U.C_BEGIN_DATE &lt;=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)        AND POS.C_BEGIN_DATE &lt;=TRUNC(SYSDATE)  
        AND POS.C_END_DATE>=TRUNC(SYSDATE)        AND (POS.C_EMP_TYPE = '4'AND (H.C_YONGGONGXINGSHI = '1' OR H.C_ZHAOPINQUDAO = '100006003' ))        GROUP BY        OAL.C_DEPT_HID,        OAL.C_HID,        H.C_EMPLOYEE_TYPE,        H.C_YEWUQUYU    </select>  
  
    <select id="listCurrentAreaCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(EGA.C_DEPTHID, EGA.C_REALUNITHID)  dept_hid            , NVL(EG.C_YEWUQUYU, '10') business_area            , EG.C_EMPLOYEE_TYPE employee_type            , COUNT(1) cnt        FROM TB_STA_EMP_ORGAPPEND EGA        INNER JOIN TB_STA_EMP_ORG EG ON EG.C_OID = EGA.C_OID        INNER JOIN TB_STA_EMP EMP ON EMP.C_OID = EG.C_EMPLOYEE_ID        INNER JOIN TB_STA_EMP_CLASS EC ON EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        WHERE EG.C_BEGIN_DATE &lt;=  TRUNC(CURRENT_DATE)  
        AND EG.C_END_DATE >  TRUNC(CURRENT_DATE)        AND ((EG.C_EMPLOYEE_TYPE = '0'  AND EC.C_LABOR_TYPE in('1','5'))            or(EG.C_EMPTYPE = '4' AND (EC.C_LABOR_TYPE = '1' OR EMP.C_ZHAOPINQUDAO = '100006003' )))        AND EG.C_DEPT_TYPE = '1'        AND EG.C_EMPLOYEE_STATUS IN ('2' , '11')        GROUP BY EGA.C_DEPTHID, EGA.C_REALUNITHID, EG.C_YEWUQUYU, EG.C_EMPLOYEE_TYPE  
    </select>  
  
    <select id="listDeptPlaitAreaCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
             NVL(OA.C_DEPT_HID, OA.C_HID) dept_hid             , NVL(P.C_WORKFORCE_SPACE, '10') business_area             , P.C_EMPLOYEE_TYPE employee_type             , P.C_WORKFORCE_NUMBER cnt        FROM TB_ORG_WORKFORCE_PLAN PLA,             TB_INF_PREPARE_DECOMPOSITION P,             TB_ORG_ORGUNITALL OA        WHERE PLA.C_HID = P.C_PLAN_ID          AND PLA.C_EFFECTIVE_STATUS = '1'          AND P.C_DEPARTMENT_HID = OA.C_HID    </select>  
    <!--异动信息 在途-->  
    <select id="listTransAreaCnt" resultType="com.huaqin.hcm.entity.po.org.TransferInfoPO">  
        select NVL(P1.C_DEPT_HID, S.C_PRE_UNITHID) cPreDeptHid,  
               S.C_PRE_YEWUQUYU                    cPreYeWuQuYu,               NVL(P2.C_DEPT_HID, C_NEW_UNITHID)   cNewDeptHid,               S.C_NEW_YEWUQUYU                    cNewYeWuQuYu,               EG.C_EMPLOYEE_TYPE                  employeeType        from TB_STA_INTRANSFERINFO S        INNER JOIN TB_ORG_POSITION OPM ON S.C_NEW_POSHID=OPM.C_HID AND OPM.C_BEGIN_DATE &lt;= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
         INNER JOIN TB_ORG_ORGUNITALL P1 ON P1.C_HID = S.C_PRE_UNITHID         INNER JOIN TB_ORG_ORGUNITALL P2 ON P2.C_HID = S.C_NEW_UNITHID         INNER JOIN TB_STA_EMP_ORG EG ON EG.C_EMPLOYEE_ID = S.C_EMPLOYEE_ID         INNER JOIN TB_STA_EMP EMP ON EMP.C_OID = EG.C_EMPLOYEE_ID         INNER JOIN TB_STA_EMP_CLASS EC ON EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        WHERE EG.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
          AND EG.C_END_DATE > TRUNC(SYSDATE)          AND EG.C_DEPT_TYPE = '1'          and EG.C_EMPLOYEE_STATUS in('2','11')          AND ((EG.C_EMPLOYEE_TYPE = '0'  AND EC.C_LABOR_TYPE in('1','5'))          or(OPM.C_EMP_TYPE = '4' AND (EC.C_LABOR_TYPE = '1' OR EMP.C_ZHAOPINQUDAO = '100006003' ) )          )        AND NVL(P1.C_DEPT_HID, P1.C_HID) != NVL(P2.C_DEPT_HID, P2.C_HID)    </select>  
  
    <!--异动信息 未来生效-->  
    <select id="listTransAreaEffectCnt" resultType="com.huaqin.hcm.entity.po.org.TransferInfoPO">  
        select NVL(P1.C_DEPT_HID, S.C_PREV_DEPT_HID) cPreDeptHid,  
        EGOLD.C_YEWUQUYU                    cPreYeWuQuYu,        NVL(P2.C_DEPT_HID, S.C_NEW_DEPT_HID)   cNewDeptHid,        EG.C_YEWUQUYU                    cNewYeWuQuYu,        EG.C_EMPLOYEE_TYPE                  employeeType        from TB_STA_EMP_TURNOVER S        left JOIN TB_STA_EMP_ORG EGOLD ON EGOLD.C_EMPLOYEE_ID = S.C_EMPLOYEE_ID  AND EGOLD.C_BEGIN_DATE &lt;= TRUNC(SYSDATE) AND EGOLD.C_END_DATE > TRUNC(SYSDATE) AND EGOLD.C_DEPT_TYPE = '1' and EGOLD.C_EMPLOYEE_STATUS in('2','11')  
        INNER JOIN TB_ORG_ORGUNITALL P1 ON P1.C_HID = S.C_PREV_DEPT_HID        INNER JOIN TB_ORG_ORGUNITALL P2 ON P2.C_HID = S.C_NEW_DEPT_HID        INNER JOIN TB_STA_EMP_ORG EG ON EG.C_EMPLOYEE_ID = S.C_EMPLOYEE_ID        INNER JOIN TB_STA_EMP EMP ON EMP.C_OID = EG.C_EMPLOYEE_ID        INNER JOIN TB_STA_EMP_CLASS EC ON EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        WHERE EG.C_BEGIN_DATE > TRUNC(SYSDATE)        AND EG.C_END_DATE > TRUNC(SYSDATE)        AND EG.C_DEPT_TYPE = '1'        and EG.C_EMPLOYEE_STATUS in('2','11')        AND ((EG.C_EMPLOYEE_TYPE = '0'  AND EC.C_LABOR_TYPE in('1','5'))        or(EG.C_EMPTYPE = '4' AND (EC.C_LABOR_TYPE = '1' OR EMP.C_ZHAOPINQUDAO = '100006003' ) )        )        AND NVL(P1.C_DEPT_HID, P1.C_HID) != NVL(P2.C_DEPT_HID, P2.C_HID)    </select>  
  
    <select id="listPrepare" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        select * from (  
        SELECT decode(U.c_Rdeptlevel,                      '2',                      oa.c_center_hid,                      '3',                      oa.c_system_hid,                      '7',                      oa.c_f_system_hid,                      '4',                      oa.c_dept_hid,                      '5',                      oa.c_grup_hid) dept_hid,               U.c_Rdeptlevel dept_level,               FUN_GETSURPWF(U.C_RDEPTHID, '0') fobzs_cnt,               FUN_GETSURPWF(U.C_RDEPTHID, '4') obzs_cnt        FROM TB_ORG_WORKFORCEUNIT U        INNER JOIN TB_ORG_ORGUNITALL OA ON OA.C_HID = U.C_RDEPTHID        WHERE u.c_Rdeptlevel != '1'  
        union all  
        select            '6042d4ad0d654ddf819926f023b4d968' dept_hid,            '1' dept_level,            FUN_GETSURPWF('6042d4ad0d654ddf819926f023b4d968', '0') fobzs_cnt,            FUN_GETSURPWF('6042d4ad0d654ddf819926f023b4d968', '4') obzs_cnt        from dual)    </select>  
  
    <select id="listHireAreaCnt" resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
        NVL(EGA.C_DEPTHID, EGA.C_REALUNITHID) dept_hid,        EG.C_EMPLOYEE_TYPE employee_type,        NVL(EG.C_YEWUQUYU, '10') business_area,        COUNT(1) cnt        FROM        (            select                distinct F.C_CREATOR C_EMPLOYEE_ID            from TB_INF_OA_FLOWINFO F            where F.C_STATUS = 1            AND F.C_FLOW_ID in (                <foreach collection="flowIdList" item="flowId" separator="," index="flowId">  
                    #{flowId}  
                </foreach>  
            )  
            AND F.C_NODE_NAME != '创建'  
        ) HIRE ,        TB_STA_EMP_ORGAPPEND EGA,        TB_STA_EMP_ORG EG,        TB_STA_EMP_CLASS EC,        TB_STA_EMP EMP        WHERE EG.C_OID = EGA.C_OID        AND EMP.C_OID = EG.C_EMPLOYEE_ID        AND EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID        AND EG.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)        AND EG.C_DEPT_TYPE = '1'        AND ((EG.C_EMPLOYEE_STATUS = '2' AND EC.C_LABOR_TYPE = '1') OR (            EC.C_LABOR_TYPE = '7' AND            EG.C_EMPLOYEE_STATUS = '11' AND            EMP.C_ZHAOPINQUDAO = '100006003' AND            EGA.C_DEPTHID = '34ff4dea4e8e4ef9858069b925d27640')        )        AND HIRE.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID        AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        GROUP BY        EGA.C_DEPTHID,        EGA.C_REALUNITHID,        EG.C_EMPLOYEE_TYPE,        EG.C_YEWUQUYU    </select>  
  
    <select id="listProcessQFinish"  
            resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            GETDEPTHID(A.C_DEPT_HID) dept_hid,            A.C_EMPLOYEE_TYPE employee_type,            A.C_BUSINESS_AREA business_area,            NVL(SUM(A.C_REC_NUMBER),0) cnt        FROM (        SELECT        CASE WHEN (A.C_R_DEPT_LEVEL = '6' OR A.C_DEPT_HID IS NULL) THEN A.C_R_DEPT_HID ELSE A.C_DEPT_HID END C_DEPT_HID,        JC.C_EMPLOYEE_TYPE,        A.C_BUSINESS_AREA,        A.C_REC_NUMBER        FROM TB_ORG_RECRUITDEMANDINFO F        LEFT JOIN TB_ORG_RECRUITDEMANDAPPLY A ON A.C_APPLYID = F.C_OID        LEFT JOIN  TB_ORG_JOB J ON J.C_HID = A.C_JOB_HID        LEFT JOIN  TB_ORG_JOB_CATEGORY JC ON  JC.C_HID = J.C_OFFICE_FAMLILY_HID        WHERE  A.C_R_DEPT_HID IS NOT NULL        AND F.C_PROCSTATUS IN ('0','1','2')        AND A.C_STATUS IN ('1','2')        AND J.C_STATUS = '1'        AND J.C_EFFECTIVE_DATE_BEGIN &lt;= SYSDATE  
        AND J.C_EFFECTIVE_DATE_END >= SYSDATE        AND JC.C_STATUS = '1'        AND JC.C_EFFECTIVE_DATE_BEGIN &lt;= SYSDATE  
        AND JC.C_EFFECTIVE_DATE_END >= SYSDATE        AND F.C_OPERATE_TIME BETWEEN to_date(#{beginDate}, 'yyyy-mm-dd') AND to_date(#{endDate}, 'yyyy-mm-dd')        ) A        GROUP BY        A.C_DEPT_HID,        A.C_EMPLOYEE_TYPE,        A.C_BUSINESS_AREA    </select>  
    <select id="listCurrentAreaCntByUpdate"  
            resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
             NVL(EGA.C_DEPTHID, EGA.C_REALUNITHID)  dept_hid             , NVL(EG.C_YEWUQUYU, '10') business_area             , EG.C_EMPLOYEE_TYPE employee_type             , COUNT(1) cnt        FROM TB_STA_EMP_ORGAPPEND EGA        INNER JOIN TB_STA_EMP_ORG EG ON EG.C_OID = EGA.C_OID        INNER JOIN TB_STA_EMP EMP ON EMP.C_OID = EG.C_EMPLOYEE_ID        INNER JOIN TB_STA_EMP_CLASS EC ON EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        WHERE EG.C_BEGIN_DATE &lt;=  TRUNC(CURRENT_DATE)  
          AND EG.C_END_DATE >  TRUNC(CURRENT_DATE)          AND ((EG.C_EMPLOYEE_STATUS = '2' AND EC.C_LABOR_TYPE = '1') OR (                    EC.C_LABOR_TYPE = '7' AND                    EG.C_EMPLOYEE_STATUS = '11' AND                    EMP.C_ZHAOPINQUDAO = '100006003' AND                    EGA.C_DEPTHID = '34ff4dea4e8e4ef9858069b925d27640')            )          AND EG.C_DEPT_TYPE = '1'        GROUP BY EGA.C_DEPTHID, EGA.C_REALUNITHID, EG.C_YEWUQUYU, EG.C_EMPLOYEE_TYPE    </select>  
    <select id="listDeptPlaitAreaCntByUpdate"  
            resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(OA.C_DEPT_HID, OA.C_HID) dept_hid             , NVL(P.C_WORKFORCE_SPACE, '10') business_area             , P.C_EMPLOYEE_TYPE employee_type             , P.C_WORKFORCE_NUMBER cnt        FROM TB_ORG_WORKFORCE_PLAN PLA,             TB_INF_PREPARE_DECOMPOSITION P,             TB_ORG_ORGUNITALL OA        WHERE PLA.C_HID = P.C_PLAN_ID          AND PLA.C_EFFECTIVE_STATUS = '1'          AND P.C_DEPARTMENT_HID = OA.C_HID    </select>  
  
    <select id="listSchoolOfferAreaCntByUpdate"  
            resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(OA.C_DEPT_HID, OA.C_HID) dept_hid             , NVL(PH.C_YEWUQUYU, '10') business_area             , PH.C_EMPLOYEE_TYPE employee_type             , COUNT(1) cnt        FROM TB_STA_PREPARE_HIRE PH,             TB_ORG_ORGUNITALL OA        WHERE PH.C_DEPT_HID = OA.C_HID          AND PH.C_STATUS = 'preparing'          AND PH.C_ZHAOPINLEIXING IN ('3', '4', '100001018')        GROUP BY OA.C_DEPT_HID, OA.C_HID, PH.C_YEWUQUYU, PH.C_EMPLOYEE_TYPE    </select>  
  
    <select id="listSocialOfferAreaCntByUpdate"  
            resultType="com.huaqin.hcm.entity.dto.plait.PlaitCntInfo">  
        SELECT  
            NVL(OA.C_DEPT_HID, OA.C_HID) dept_hid             , NVL(PH.C_YEWUQUYU, '10') business_area             , PH.C_EMPLOYEE_TYPE employee_type             , COUNT(1) cnt        FROM TB_STA_PREPARE_HIRE PH                 INNER JOIN TB_ORG_ORGUNITALL OA ON PH.C_DEPT_HID = OA.C_HID        WHERE  PH.C_STATUS = 'preparing'          AND PH.C_YONGGONGXINGSHI = '1'          AND SUBSTR(PH.C_CODE, 1, 1) != 'X'        AND PH.C_ZHAOPINLEIXING NOT IN ('3', '4', '100001018')        AND PH.C_ZHAOPINQUDAO NOT IN ('100001018')        GROUP BY OA.C_DEPT_HID, OA.C_HID, PH.C_YEWUQUYU, PH.C_EMPLOYEE_TYPE    </select>  
  
    <select id="listTransAreaCntByUpdate"  
            resultType="com.huaqin.hcm.entity.po.org.TransferInfoPO">  
        select NVL(P1.C_DEPT_HID, S.C_PRE_UNITHID) cPreDeptHid,  
               S.C_PRE_YEWUQUYU                    cPreYeWuQuYu,               NVL(P2.C_DEPT_HID, S.C_NEW_UNITHID)   cNewDeptHid,               S.C_NEW_YEWUQUYU                    cNewYeWuQuYu,               EG.C_EMPLOYEE_TYPE                  employeeType        from TB_STA_INTRANSFERINFO S        INNER JOIN TB_ORG_ORGUNITALL P1 ON P1.C_HID = S.C_PRE_UNITHID        INNER JOIN TB_ORG_ORGUNITALL P2 ON P2.C_HID = S.C_NEW_UNITHID        INNER JOIN TB_STA_EMP_ORG EG ON EG.C_EMPLOYEE_ID = S.C_EMPLOYEE_ID        INNER JOIN TB_STA_EMP EMP ON EMP.C_OID = EG.C_EMPLOYEE_ID        INNER JOIN TB_STA_EMP_CLASS EC ON EG.C_EMPLOYEE_ID = EC.C_EMPLOYEE_ID AND EC.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND EC.C_END_DATE >= TRUNC(CURRENT_DATE)  
        WHERE EG.C_BEGIN_DATE &lt;= TRUNC(SYSDATE)  
          AND EG.C_END_DATE > TRUNC(SYSDATE)          AND EG.C_DEPT_TYPE = '1'          AND ((EG.C_EMPLOYEE_STATUS = '2' AND EC.C_LABOR_TYPE = '1') OR (                    EC.C_LABOR_TYPE = '7' AND EG.C_EMPLOYEE_STATUS = '11' AND                    EMP.C_ZHAOPINQUDAO = '100006003' AND                    (P1.C_DEPT_HID = '34ff4dea4e8e4ef9858069b925d27640' OR P2.C_DEPT_HID = '34ff4dea4e8e4ef9858069b925d27640') ) OR (                   EC.C_LABOR_TYPE = '3' AND EG.C_WORKERS_TYPE IN ('5', '6') ) OR (                   EC.C_LABOR_TYPE = '2' AND EG.C_WORKERS_TYPE = '5'                   )            )          AND NVL(P1.C_DEPT_HID, P1.C_HID) != NVL(P2.C_DEPT_HID, P2.C_HID)    </select>  
  
  
    <insert id="mergeOrgData0">  
        INSERT INTO TB_ORG_RECRUITDEMANDBUFFER(C_OID, C_OPERATE_TIME, C_OPERATOR, C_CENTERHID, C_CENTERNAME, C_SYSTEMHID,  
                                               C_SYSTEMNAME, C_DEPTHID, C_DEPTNAME, C_EMPLOYEE_TYPE, C_WORKFORCEBALANCENUM,                                               C_PREEXITNUM, C_BUFFER, C_CQNUM, C_RELEASENUM, C_BUSINESS_AREA)        SELECT            LOWER(SYS_GUID()),            SYSDATE,            'TB',            TOW.C_CENTERHID,            TOW.C_CENTERNAME,            TOW.C_SYSTEMHID,            TOW.C_SYSTEMNAME,            TOW.C_RDEPTHID,            TOW.C_RDEPTNAME,            '0',            0,            0,            0,            0,            0,            TCI.C_CODE        FROM TB_ORG_WORKFORCEUNIT TOW, TP_CODE_ITEM TCI        WHERE TCI.C_TYPE_CODE = 'YEWUQUYU'          AND NOT EXISTS   (            SELECT 1 FROM TB_ORG_RECRUITDEMANDBUFFER TOR WHERE TOR.C_DEPTHID = TOW.C_RDEPTHID AND TOR.C_BUSINESS_AREA = TCI.C_CODE AND TOR.C_EMPLOYEE_TYPE = '0'          )    </insert>  
    <insert id="mergeOrgData4">  
        INSERT INTO TB_ORG_RECRUITDEMANDBUFFER(C_OID, C_OPERATE_TIME, C_OPERATOR, C_CENTERHID, C_CENTERNAME, C_SYSTEMHID,  
                                               C_SYSTEMNAME, C_DEPTHID, C_DEPTNAME, C_EMPLOYEE_TYPE, C_WORKFORCEBALANCENUM,                                               C_PREEXITNUM, C_BUFFER, C_CQNUM, C_RELEASENUM, C_BUSINESS_AREA)        SELECT            LOWER(SYS_GUID()),            SYSDATE,            'TB',            TOW.C_CENTERHID,            TOW.C_CENTERNAME,            TOW.C_SYSTEMHID,            TOW.C_SYSTEMNAME,            TOW.C_RDEPTHID,            TOW.C_RDEPTNAME,            '4',            0,            0,            0,            0,            0,            TCI.C_CODE        FROM TB_ORG_WORKFORCEUNIT TOW, TP_CODE_ITEM TCI        WHERE TCI.C_TYPE_CODE = 'YEWUQUYU'          AND NOT EXISTS   (            SELECT 1 FROM TB_ORG_RECRUITDEMANDBUFFER TOR WHERE TOR.C_DEPTHID = TOW.C_RDEPTHID AND TOR.C_BUSINESS_AREA = TCI.C_CODE AND TOR.C_EMPLOYEE_TYPE = '4'          )    </insert>  
  
    <select id="listPlaitZyAndOl"  
            resultType="com.huaqin.hcm.entity.vo.plait.PlaitZyAndOlVo">  
        SELECT  
        OU.C_HID orgHid,        OU.C_CODE orgCode,        OU.C_NAME orgName,        OU.C_LEVEL orgLevel,        GETCODENAME(OU.C_LEVEL,'ORGANIZATION_LEVEL') orgLevelName,        PR.C_WORKFORCENUM empNumZy,        PROL.C_WORKFORCENUM empNumOl        FROM        TB_ORG_ORGUNIT OU,        TB_ORG_MANAGE_PLAIT_REPORT PR,TB_ORG_MANAGE_PLAIT_REPORT PROL        WHERE        OU.C_HID = PR.C_UNIT_HID AND        PR.C_EMPLOYEE_TYPE = '0' AND        OU.C_HID = PROL.C_UNIT_HID AND        PROL.C_EMPLOYEE_TYPE = '4' AND        OU.C_STATUS = '1' AND        OU.C_BEGIN_DATE &lt;= TRUNC(SYSDATE) AND  
        OU.C_END_DATE >= TRUNC(SYSDATE)        ORDER BY GETCODEVALUE(OU.C_LEVEL,'ORGANIZATION_LEVEL')    </select>  
  
</mapper>
```


```
package com.huaqin.hcm.mapper.plait;  
  
import com.huaqin.hcm.entity.dto.plait.PlaitCntInfo;  
import com.huaqin.hcm.entity.po.org.TransferInfoPO;  
import com.huaqin.hcm.entity.po.plait.OrgWorkForceUnit;  
import com.baomidou.mybatisplus.core.mapper.BaseMapper;  
import com.huaqin.hcm.entity.vo.plait.PlaitZyAndOlVo;  
import org.apache.ibatis.annotations.Param;  
import java.util.List;  
  
/**  
 * description * 编制报表  
 * @author qianyongqiang 2022/07/12 10:13  
 */public interface PlaitFormMapper extends BaseMapper<PlaitCntInfo> {  
  
    /**  
     * 查询DeptHid列表  
     * @param orgHid  
     * @return  
     */  
    List<String> selectDeptHidList(@Param("orgHid") String orgHid);  
  
    /**  
     * 上年底部门在岗人数  
     * @return  
     */  
    List<PlaitCntInfo> listLastYearCnt();  
  
    /**  
     * 在途Offer数量(校招)  
     * @return  
     */  
    List<PlaitCntInfo> listSchoolOfferCnt();  
  
  
    /**  
     * 在途Offer数量(社招)(员工 职员)  
     * @return  
     */  
    List<PlaitCntInfo> listSocialOfferEmpCnt();  
  
    /**  
     * 在途Offer数量(社招)(O类) 调整为非制造O  
     * @return  
     */  
    List<PlaitCntInfo> listSocialOfferOEmpCnt();  
  
    /**  
     * 核心人才溶度  
     * @return  
     */  
    List<PlaitCntInfo> listCorePercent();  
  
    /**  
     * 部门编制  
     * @return  
     */  
    List<PlaitCntInfo> listDeptPlaitCnt();  
  
    /**  
     * 异动信息（在途）  
     * @return  
     */  
    List<PlaitCntInfo> listTranCnt();  
  
    /**  
     * 异动信息(未来生效)  
     * @return  
     */  
    List<PlaitCntInfo> listTranCntEffect();  
  
    /**  
     * 预离职信息  
     * @param flowIdList  
     * @return  
     */  
    List<PlaitCntInfo> listHireCnt(@Param("flowIdList") List<String> flowIdList);  
  
    /**  
     * 查询分体系  
     * @return  
     */  
    List<OrgWorkForceUnit> listSubSysInfo();  
  
    /**  
     * 查询校招区域  
     * @return  
     */  
    List<PlaitCntInfo> listSchoolOfferAreaCnt();  
  
    /**  
     * 查询社招区域（职员）  
     * @return  
     */  
    List<PlaitCntInfo> listSocialOfferAreaCnt();  
    /**  
     * 查询社招区域（非制造O）  
     * @return  
     */  
    List<PlaitCntInfo> listSocialOfferOAreaCnt();  
  
    /**  
     * 查询当前部门人数（区域）  
     * @return  
     */  
    List<PlaitCntInfo> listCurrentAreaCnt();  
  
    /**  
     * 查询当前部门编制数据（区域）  
     * @return  
     */  
    List<PlaitCntInfo> listDeptPlaitAreaCnt();  
  
    /**  
     * 查询在途异动（区域）  
     * @return  
     */  
    List<TransferInfoPO> listTransAreaCnt();  
   /**  
     * 查询未来生效 异动（区域）  
     * @return  
     */  
    List<TransferInfoPO> listTransAreaEffectCnt();  
  
    /**  
     * 待分配编制数  
     * @return  
     */  
    List<PlaitCntInfo> listPrepare();  
  
  
    /**  
     * 插入职员的组织数据  
     */  
    void mergeOrgData0();  
  
    /**  
     * O类员工的组织数据  
     */  
    void mergeOrgData4();  
  
    /**  
     * 区域 预离职数据  
     * @param departureFlowIdList  
     * @return  
     */  
    List<PlaitCntInfo> listHireAreaCnt(@Param("flowIdList") List<String> departureFlowIdList);  
  
    /**  
     * 本季度已完成需求  
     * @param beginDate  
     * @param endDate  
     * @return  
     */  
    List<PlaitCntInfo> listProcessQFinish(@Param("beginDate")String beginDate, @Param("endDate") String endDate);  
  
    /**  
     * 当前部门人数（区域，根据组织层级判断）  
     * @return  
     */  
    List<PlaitCntInfo> listCurrentAreaCntByUpdate();  
  
    /**  
     * 部门编制数（区域，根据组织层级判断）  
     * @return  
     */  
    List<PlaitCntInfo> listDeptPlaitAreaCntByUpdate();  
  
    /**  
     * 校招在途（区域，根据组织层级判断）  
     * @return  
     */  
    List<PlaitCntInfo> listSchoolOfferAreaCntByUpdate();  
  
    /**  
     * 社招在途（区域，根据组织层级判断）  
     * @return  
     */  
    List<PlaitCntInfo> listSocialOfferAreaCntByUpdate();  
  
    /**  
     * 异动在途（区域，根据组织层级判断）  
     * @return  
     */  
    List<TransferInfoPO> listTransAreaCntByUpdate();  
  
  
    /**  
     * 职员 O类编制  
     * @return  
     */  
    List<PlaitZyAndOlVo> listPlaitZyAndOl();  
}
```

```
package com.huaqin.hcm.service.plait;  
  
import com.huaqin.hcm.common.Constants;  
import com.huaqin.hcm.entity.dto.plait.PlaitAreaField;  
import com.huaqin.hcm.entity.dto.plait.PlaitCntInfo;  
import com.huaqin.hcm.entity.dto.plait.PlaitField;  
import com.huaqin.hcm.entity.dto.plait.PlaitTotalDataDTO;  
import com.huaqin.hcm.entity.po.org.PlaitFormHistoryPO;  
import com.huaqin.hcm.entity.po.plait.OrgWorkForceUnit;  
import com.huaqin.hcm.entity.vo.plait.PlaitZyAndOlVo;  
import com.huaqin.hcm.exception.BusinessException;  
import com.huaqin.hcm.mapper.employee.EmployeeOrgMapper;  
import com.huaqin.hcm.mapper.plait.PlaitFormMapper;  
import com.huaqin.hcm.mapper.plait.PlaitOrgWorkForceUnitMapper;  
import com.huaqin.hcm.mapper.roster.EmployeeRosterMapper;  
import com.huaqin.hcm.service.system.ParamService;  
import com.huaqin.hcm.utils.ExceptionUtil;  
import com.huaqin.hqtool.core.thread.ThreadUtil;  
  
import com.alibaba.excel.EasyExcel;  
import com.alibaba.excel.ExcelWriter;  
import com.alibaba.excel.support.ExcelTypeEnum;  
import com.alibaba.excel.write.metadata.WriteSheet;  
import com.alibaba.fastjson.JSON;  
import com.alibaba.fastjson.JSONObject;  
import com.baomidou.mybatisplus.core.toolkit.Wrappers;  
import org.springframework.core.io.ClassPathResource;  
import org.springframework.stereotype.Service;  
  
import java.io.File;  
import java.io.InputStream;  
import java.math.BigDecimal;  
import java.util.ArrayList;  
import java.util.Arrays;  
import java.util.Collection;  
import java.util.Comparator;  
import java.util.HashMap;  
import java.util.HashSet;  
import java.util.List;  
import java.util.Map;  
import java.util.Set;  
import java.util.concurrent.CountDownLatch;  
import java.util.concurrent.TimeUnit;  
import java.util.concurrent.atomic.AtomicReference;  
import java.util.function.Function;  
import java.util.stream.Collectors;  
  
import cn.hutool.core.bean.BeanUtil;  
import cn.hutool.core.collection.CollUtil;  
import cn.hutool.core.convert.Convert;  
import cn.hutool.core.map.MapUtil;  
import cn.hutool.core.util.CharUtil;  
import cn.hutool.core.util.ObjectUtil;  
import cn.hutool.core.util.StrUtil;  
import javafx.util.Pair;  
import lombok.RequiredArgsConstructor;  
import lombok.extern.slf4j.Slf4j;  
  
/**  
 * description * 编制报表  
 *  
 * @author qianyongqiang 2022/07/12 17:07  
 */@RequiredArgsConstructor  
@Service  
@Slf4j  
public class PlaitService {  
  
    private final PlaitFormMapper plaitFormMapper;  
  
    private final PlaitOrgWorkForceUnitMapper orgWorkForceUnitMapper;  
  
    private final EmployeeRosterMapper employeeRosterMapper;  
    private final EmployeeOrgMapper employeeOrgMapper;  
    private final ParamService paramService;  
  
    private final String OA_FIRE_WORKFLOW_ID = "OA_FIRE_WORKFLOW_ID";  
  
    /**  
     * 编制查询  
     *  
     * @param orgHid  
     * @return  
     */  
    public List<JSONObject> findAll(String orgHid, String empCode) {  
  
        // 查询下级组织 交集 组织约束  
        Collection<String> orgHidList;  
  
        if(Constants.HCM_ADMIN.contains(empCode)){  
            orgHidList = plaitFormMapper.selectDeptHidList(orgHid);  
        } else {  
            if(StrUtil.isEmpty(orgHid)){  
                orgHidList = employeeRosterMapper.listOrgDataRestrict(empCode) ;  
            } else {  
                orgHidList = CollUtil.intersection(plaitFormMapper.selectDeptHidList(orgHid),  
                        employeeRosterMapper.listOrgDataRestrict(empCode));  
            }  
  
            if(CollUtil.isEmpty(orgHidList)){  
                orgHidList = employeeOrgMapper.getOrgByEmpCode(empCode);  
            }  
        }  
  
        Set<String> orgLevelSet = new HashSet<>();  
  
        // 查询编制组织(去掉副部)  
        Collection<String> finalOrgHidList = orgHidList;  
        List<OrgWorkForceUnit> orgUnitList = orgWorkForceUnitMapper.selectList(Wrappers.lambdaQuery()).stream()  
                .filter(org -> finalOrgHidList.contains(org.getRDeptHid()))  
                .filter(org -> !"6".equals(org.getRDeptLevel()))  
                .collect(Collectors.toList());  
  
        Map<String, Map<String, BigDecimal>> dataMap = new HashMap<>(16);  
        for (OrgWorkForceUnit workForceUnit : orgUnitList) {  
            workForceUnit.setDeptHid(StrUtil.blankToDefault(workForceUnit.getDeptHid(), workForceUnit.getRDeptHid()));  
            workForceUnit.setDeptName(StrUtil.blankToDefault(workForceUnit.getDeptName(), workForceUnit.getRDeptName()));  
            dataMap.put(workForceUnit.getDeptHid(), new HashMap<>(16));  
            orgLevelSet.add(workForceUnit.getRDeptLevel());  
        }  
  
        CountDownLatch latch = new CountDownLatch(8);  
  
        // 去年年底在职  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processLastYear(orgUnitList, dataMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        // 计算在途校招  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processSchoolOffer(orgUnitList, dataMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        // 计算在途社招  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processSocialOffer(orgUnitList, dataMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        // 计算核心人才浓度  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processCorePercent(orgUnitList, dataMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        // 计算部门编制  体系中心分体系 取本组织的   部门取本组织及下级组织  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processDeptPlait(orgUnitList, dataMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        // 异动信息  
        Map<String, Pair<BigDecimal, BigDecimal>> fSystemInMap = new HashMap<>(16);  
        Map<String, Pair<BigDecimal, BigDecimal>> systemInMap = new HashMap<>(16);  
        Map<String, Pair<BigDecimal, BigDecimal>> centerInMap = new HashMap<>(16);  
        Map<String, Pair<BigDecimal, BigDecimal>> fSystemOutMap = new HashMap<>(16);  
        Map<String, Pair<BigDecimal, BigDecimal>> systemOutMap = new HashMap<>(16);  
        Map<String, Pair<BigDecimal, BigDecimal>> centerOutMap = new HashMap<>(16);  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processTran(orgUnitList, dataMap, fSystemInMap, systemInMap, centerInMap, fSystemOutMap, systemOutMap, centerOutMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        // 预离职信息  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processHire(orgUnitList, dataMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        // 待分配  
        Map<String, BigDecimal> preparePlaitMap = new HashMap<>(16);  
        Map<String, BigDecimal> preparePlaitOMap = new HashMap<>(16);  
        AtomicReference<BigDecimal> comPrepare = new AtomicReference<>(BigDecimal.ZERO);  
        AtomicReference<BigDecimal> comPrepareO=  new AtomicReference<>(BigDecimal.ZERO);  
        Map<String, BigDecimal> sysPrepareMap = new HashMap<>(16);  
        Map<String, BigDecimal> centerPrepareMap = new HashMap<>(16);  
        Map<String, BigDecimal> sysPrepareOMap = new HashMap<>(16);  
        Map<String, BigDecimal> centerPrepareOMap = new HashMap<>(16);  
        ThreadUtil.execute(() -> {  
            ExceptionUtil.tryCatch(  
                    () -> this.processPrepare(orgUnitList,  
                            preparePlaitMap, preparePlaitOMap, comPrepare, comPrepareO,  
                            sysPrepareMap, centerPrepareMap, sysPrepareOMap, centerPrepareOMap),  
                    () -> latch.countDown()  
            );  
        });  
  
        try {  
            latch.await(11, TimeUnit.MINUTES);  
        } catch (InterruptedException e) {  
            log.error("--------------------------------------");  
            log.error("查询出错");  
            log.error("--------------------------------------");  
            e.printStackTrace();  
        }  
  
        // 加上编制  
        for (Map.Entry<String, Map<String, BigDecimal>> dataEntry : dataMap.entrySet()) {  
            comPrepare.set(comPrepare.get().add(ObjectUtil.defaultIfNull(dataEntry.getValue().get(PlaitField.FOBZS), BigDecimal.ZERO)));  
            comPrepareO.set(comPrepareO.get().add(ObjectUtil.defaultIfNull(dataEntry.getValue().get(PlaitField.OBZS), BigDecimal.ZERO)));  
        }  
  
        dataMap.forEach((dept, detailMap) -> {  
  
            // 编制余额（职员）--在职已含 待异动出和待离职 因此不需要单独加待异动出  
            detailMap.put(PlaitField.PLAITREST,  
                    ObjectUtil.defaultIfNull(detailMap.get(PlaitField.FOBZS), BigDecimal.ZERO)  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.FOZZS), BigDecimal.ZERO))  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.FOFS), BigDecimal.ZERO))  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.XZZTOFS), BigDecimal.ZERO))  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.XZSXOFS), BigDecimal.ZERO))  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.TRANINS), BigDecimal.ZERO))  
//                            .add(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.TRANOUTS), BigDecimal.ZERO))  
            );  
  
            // 编制余额（职工）  
            detailMap.put(PlaitAreaField.OPLAITREST,  
                    ObjectUtil.defaultIfNull(detailMap.get(PlaitField.OBZS), BigDecimal.ZERO)  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.OZZS), BigDecimal.ZERO))  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.OOFS), BigDecimal.ZERO))  
                            .subtract(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.OTRANINS), BigDecimal.ZERO))  
//                            .add(ObjectUtil.defaultIfNull(detailMap.get(PlaitField.OTRANOUTS), BigDecimal.ZERO))  
            );  
        });  
  
        // 查询分体系  
        Map<String, OrgWorkForceUnit> subSystemMap =  
                plaitFormMapper.listSubSysInfo().stream()  
                        .collect(Collectors.toMap(OrgWorkForceUnit::getRDeptHid, Function.identity()));  
  
        // 中心、体系、分体系、部门  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            orgWorkForceUnit.setCenterCode(StrUtil.blankToDefault(orgWorkForceUnit.getCenterCode(), StrUtil.EMPTY));  
            orgWorkForceUnit.setSystemCode(StrUtil.blankToDefault(orgWorkForceUnit.getSystemCode(), StrUtil.EMPTY));  
            orgWorkForceUnit.setSubSystemCode(StrUtil.blankToDefault(orgWorkForceUnit.getSubSystemCode(), StrUtil.EMPTY));  
            orgWorkForceUnit.setDeptCode(StrUtil.blankToDefault(orgWorkForceUnit.getDeptCode(), StrUtil.EMPTY));  
            if (subSystemMap.containsKey(orgWorkForceUnit.getRDeptHid())) {  
                OrgWorkForceUnit subSystem = subSystemMap.get(orgWorkForceUnit.getRDeptHid());  
                orgWorkForceUnit.setSubSystemHid(subSystem.getSubSystemHid());  
                orgWorkForceUnit.setSubSystemCode(subSystem.getSubSystemCode());  
                orgWorkForceUnit.setSubSystemName(subSystem.getSubSystemName());  
            }  
        }  
  
        // 根据中心、体系、分体系、部门 排序  
        List<OrgWorkForceUnit> sortDeptWorkUnitList = orgUnitList.stream()  
                .sorted(Comparator.nullsLast(Comparator.comparing(OrgWorkForceUnit::getCenterCode))  
                        .thenComparing(Comparator.nullsFirst(Comparator.comparing(OrgWorkForceUnit::getSystemCode)))  
                        .thenComparing(Comparator.nullsFirst(Comparator.comparing(OrgWorkForceUnit::getSubSystemCode)))  
                        .thenComparing(Comparator.nullsFirst(Comparator.comparing(OrgWorkForceUnit::getDeptCode)))  
                ).collect(Collectors.toList());  
  
        Map<String, Map<String, BigDecimal>> subSystemTotalMap = new HashMap<>(16);  
        sortDeptWorkUnitList.stream()  
                .collect(Collectors.groupingBy(org ->  
                        StrUtil.join(StrUtil.COMMA,  
                                org.getCenterCode(),  
                                org.getSystemCode(),  
                                org.getSubSystemCode()  
                        )  
                )).forEach((key, subList) -> {  
            Map<String, BigDecimal> sameMap = new HashMap<>(16);  
            for (OrgWorkForceUnit orgWorkForceUnit : subList) {  
                Map<String, BigDecimal> sameDeptMap = dataMap.get(orgWorkForceUnit.getDeptHid());  
                sameDeptMap.forEach((k, v) -> {  
                    sameMap.put(k, ObjectUtil.defaultIfNull(v, BigDecimal.ZERO).add(  
                            sameMap.getOrDefault(k, BigDecimal.ZERO)  
                    ));  
                });  
            }  
            subSystemTotalMap.put(key, sameMap);  
        });  
  
        Map<String, Map<String, BigDecimal>> systemTotalMap = new HashMap<>(16);  
        sortDeptWorkUnitList.stream()  
                .collect(Collectors.groupingBy(org ->  
                        StrUtil.join(StrUtil.COMMA,  
                                org.getCenterCode(),  
                                org.getSystemCode()  
                        )  
                )).forEach((key, subList) -> {  
            Map<String, BigDecimal> sameMap = new HashMap<>(16);  
            for (OrgWorkForceUnit orgWorkForceUnit : subList) {  
                Map<String, BigDecimal> sameDeptMap = dataMap.get(orgWorkForceUnit.getDeptHid());  
                sameDeptMap.forEach((k, v) -> {  
                    sameMap.put(k, ObjectUtil.defaultIfNull(v, BigDecimal.ZERO).add(  
                            sameMap.getOrDefault(k, BigDecimal.ZERO)  
                    ));  
                });  
            }  
            systemTotalMap.put(key, sameMap);  
        });  
  
        Map<String, Map<String, BigDecimal>> centerTotalMap = new HashMap<>(16);  
        sortDeptWorkUnitList.stream()  
                .collect(Collectors.groupingBy(org ->  
                        StrUtil.join(StrUtil.COMMA, org.getCenterCode())  
                )).forEach((key, subList) -> {  
                    Map<String, BigDecimal> sameMap = new HashMap<>(16);  
                    for (OrgWorkForceUnit orgWorkForceUnit : subList) {  
                        Map<String, BigDecimal> sameDeptMap = dataMap.get(orgWorkForceUnit.getDeptHid());  
                        sameDeptMap.forEach((k, v) -> {  
                            sameMap.put(k, ObjectUtil.defaultIfNull(v, BigDecimal.ZERO).add(  
                                    sameMap.getOrDefault(k, BigDecimal.ZERO)  
                            ));  
                        });  
                    }  
                    centerTotalMap.put(key, sameMap);  
                });  
  
        List<JSONObject> resultList = new ArrayList<>();  
        OrgWorkForceUnit beforeUnit = CollUtil.getFirst(sortDeptWorkUnitList) == null ? new OrgWorkForceUnit() : CollUtil.getFirst(sortDeptWorkUnitList);  
  
        String beforeSubSystemKey = StrUtil.join(StrUtil.COMMA,  
                beforeUnit.getCenterCode(),  
                beforeUnit.getSystemCode(),  
                beforeUnit.getSubSystemCode()  
        );  
        String beforeSystemKey = StrUtil.join(StrUtil.COMMA,  
                beforeUnit.getCenterCode(),  
                beforeUnit.getSystemCode()  
        );  
        String beforeCenterKey = StrUtil.join(StrUtil.COMMA,  
                beforeUnit.getCenterCode()  
        );  
  
        for (OrgWorkForceUnit workForceUnit : sortDeptWorkUnitList) {  
            String subSystemKey = StrUtil.join(StrUtil.COMMA,  
                    workForceUnit.getCenterCode(),  
                    workForceUnit.getSystemCode(),  
                    workForceUnit.getSubSystemCode()  
            );  
  
            String systemKey = StrUtil.join(StrUtil.COMMA,  
                    workForceUnit.getCenterCode(),  
                    workForceUnit.getSystemCode()  
            );  
  
            String centerKey = StrUtil.join(StrUtil.COMMA,  
                    workForceUnit.getCenterCode()  
            );  
  
            if (!StrUtil.equals(beforeSubSystemKey, subSystemKey) && orgLevelSet.contains("7")) {  
                // 待分配编制  
                // 如果没分体系的话，这不显示待分配编制  
                if(!StrUtil.isBlank(beforeUnit.getSubSystemHid())) {  
                    this.calcTotalAndPrepare(  
                            beforeUnit,  
                            preparePlaitMap.get(beforeUnit.getSubSystemHid()),  
                            preparePlaitOMap.get(beforeUnit.getSubSystemHid()),  
                            beforeSubSystemKey,  
                            subSystemTotalMap,  
                            resultList,  
                            "分体系待分配编制",  
                            "分体系总计",  
                            false,  
                            false,  
                            null,  
                            null,  
                            fSystemInMap.get(beforeUnit.getSubSystemHid()),  
                            fSystemOutMap.get(beforeUnit.getSubSystemHid())  
                    );  
                }  
            }  
  
            if (!StrUtil.equals(beforeSystemKey, systemKey) && orgLevelSet.contains("3")) {  
                // 如果没分体系的话，这不显示待分配编制  
                if(!StrUtil.isBlank(beforeUnit.getSystemHid())) {  
                    // 体系维度  
                    this.calcTotalAndPrepare(  
                            beforeUnit,  
                            preparePlaitMap.get(beforeUnit.getSystemHid()),  
                            preparePlaitOMap.get(beforeUnit.getSystemHid()),  
                            beforeSystemKey,  
                            systemTotalMap,  
                            resultList,  
                            "体系待分配编制",  
                            "体系总计",  
                            true,  
                            false,  
                            sysPrepareMap.get(beforeUnit.getSystemHid()),  
                            sysPrepareOMap.get(beforeUnit.getSystemHid()),  
                            systemInMap.get(beforeUnit.getSystemHid()),  
                            systemOutMap.get(beforeUnit.getSystemHid()));  
                }  
            }  
  
  
            if (!StrUtil.equals(beforeCenterKey, centerKey) && orgLevelSet.contains("2")) {  
                if(StrUtil.isNotBlank(beforeUnit.getCenterHid())){  
                    // 中心维度  
                    this.calcTotalAndPrepare(  
                            beforeUnit,  
                            preparePlaitMap.get(beforeUnit.getCenterHid()),  
                            preparePlaitOMap.get(beforeUnit.getCenterHid()),  
                            beforeCenterKey,  
                            centerTotalMap,  
                            resultList,  
                            "中心待分配编制",  
                            "中心总计",  
                            true,  
                            true,  
                            centerPrepareMap.get(beforeUnit.getCenterHid()),  
                            centerPrepareOMap.get(beforeUnit.getCenterHid()),  
                            centerInMap.get(beforeUnit.getCenterHid()),  
                            centerOutMap.get(beforeUnit.getCenterHid()));  
                }  
            }  
  
            Map<String, BigDecimal> deptMap = dataMap.get(workForceUnit.getDeptHid());  
            Map baseMap = BeanUtil.beanToMap(workForceUnit);  
            baseMap.putAll(deptMap);  
  
            if (MapUtil.isNotEmpty(deptMap)) {  
                if (ObjectUtil.defaultIfNull(deptMap.get(PlaitField.ZZZS), BigDecimal.ZERO).compareTo(BigDecimal.ZERO) != 0) {  
                    baseMap.put(PlaitField.GGZZS_ZZZS, ObjectUtil.defaultIfNull(deptMap.get(PlaitField.GGZZS), BigDecimal.ZERO)  
                            .divide(deptMap.get(PlaitField.ZZZS), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                            .setScale(2)+ "%");  
                } else {  
                    baseMap.put(PlaitField.GGZZS_ZZZS, "-");  
                }  
  
                if (ObjectUtil.defaultIfNull(deptMap.get(PlaitField.SNZZS), BigDecimal.ZERO).compareTo(BigDecimal.ZERO) != 0) {  
                    baseMap.put(PlaitField.FOZZS_SNZZS, ObjectUtil.defaultIfNull(deptMap.get(PlaitField.FOZZS), BigDecimal.ZERO)  
                            .subtract(ObjectUtil.defaultIfNull(deptMap.get(PlaitField.SNZZS), BigDecimal.ZERO))  
                            .divide(deptMap.get(PlaitField.SNZZS), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                            .setScale(2)+ "%");  
                } else {  
                    baseMap.put(PlaitField.FOZZS_SNZZS, "-");  
                }  
  
                if (ObjectUtil.defaultIfNull(deptMap.get(PlaitField.OSNZZS), BigDecimal.ZERO).compareTo(BigDecimal.ZERO) != 0) {  
                    baseMap.put(PlaitField.OBZS_OSNZZS, ObjectUtil.defaultIfNull(deptMap.get(PlaitField.OBZS), BigDecimal.ZERO).subtract(deptMap.get(PlaitField.OSNZZS))  
                            .divide(deptMap.get(PlaitField.OSNZZS), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                            .setScale(2)+ "%");  
                } else {  
                    baseMap.put(PlaitField.OBZS_OSNZZS, "-");  
                }  
            } else {  
                baseMap.put(PlaitField.GGZZS_ZZZS, "-");  
                baseMap.put(PlaitField.FOZZS_SNZZS, "-");  
                baseMap.put(PlaitField.OBZS_OSNZZS, "-");  
            }  
  
  
            resultList.add((JSONObject) JSON.toJSON(baseMap));  
  
            beforeSubSystemKey = subSystemKey;  
            beforeSystemKey = systemKey;  
            beforeCenterKey = centerKey;  
            beforeUnit = workForceUnit;  
        }  
  
        if (CollUtil.isNotEmpty(sortDeptWorkUnitList)) {  
            // 待分配编制  
            // 如果没分体系的话，这不显示待分配编制  
            if(!StrUtil.isBlank(beforeUnit.getSubSystemHid()) && orgLevelSet.contains("7")) {  
                this.calcTotalAndPrepare(  
                        beforeUnit,  
                        preparePlaitMap.get(beforeUnit.getSubSystemHid()),  
                        preparePlaitOMap.get(beforeUnit.getSubSystemHid()),  
                        beforeSubSystemKey,  
                        subSystemTotalMap,  
                        resultList,  
                        "分体系待分配编制",  
                        "分体系总计",  
                        false,  
                        false,  
                        null,  
                        null,  
                        fSystemInMap.get(beforeUnit.getSubSystemHid()),  
                        fSystemOutMap.get(beforeUnit.getSubSystemHid()));  
            }  
  
            // 体系维度  
            if(!StrUtil.isBlank(beforeUnit.getSystemHid()) && orgLevelSet.contains("3")) {  
                this.calcTotalAndPrepare(  
                        beforeUnit,  
                        preparePlaitMap.get(beforeUnit.getSystemHid()),  
                        preparePlaitOMap.get(beforeUnit.getSystemHid()),  
                        beforeSystemKey,  
                        systemTotalMap,  
                        resultList,  
                        "体系待分配编制",  
                        "体系总计",  
                        true,  
                        false,  
                        sysPrepareMap.get(beforeUnit.getSystemHid()),  
                        sysPrepareOMap.get(beforeUnit.getSystemHid()),  
                        systemInMap.get(beforeUnit.getSystemHid()),  
                        systemOutMap.get(beforeUnit.getSystemHid()));  
            }  
  
            // 中心  
            if(!StrUtil.isBlank(beforeUnit.getCenterHid()) && orgLevelSet.contains("2")){  
                this.calcTotalAndPrepare(  
                        beforeUnit,  
                        preparePlaitMap.get(beforeUnit.getCenterHid()),  
                        preparePlaitOMap.get(beforeUnit.getCenterHid()),  
                        beforeCenterKey,  
                        centerTotalMap,  
                        resultList,  
                        "中心待分配编制",  
                        "中心总计",  
                        true,  
                        true,  
                        centerPrepareMap.get(beforeUnit.getCenterHid()),  
                        centerPrepareOMap.get(beforeUnit.getCenterHid()),  
                        centerInMap.get(beforeUnit.getCenterHid()),  
                        centerOutMap.get(beforeUnit.getCenterHid()));  
            }  
  
            // 包含公司权限  
                if(orgHidList.contains("6042d4ad0d654ddf819926f023b4d968")){  
                    // 总合计  
                    OrgWorkForceUnit allUnit = new OrgWorkForceUnit();  
                    allUnit.setDeptName("总合计");  
                    Map allMap = BeanUtil.beanToMap(allUnit);  
                    Map<String, BigDecimal> allCenterMap = new HashMap<>(16);  
                    centerTotalMap.forEach((systemId, deptMap) -> {  
                        deptMap.forEach((k, v) -> {  
                            allCenterMap.put(k, ObjectUtil.defaultIfNull(v, BigDecimal.ZERO).add(  
                                    allCenterMap.getOrDefault(k, BigDecimal.ZERO)  
                            ));  
                        });  
                    });  
  
                    allCenterMap.put(PlaitField.FOBZS, comPrepare.get());  
                    allCenterMap.put(PlaitField.OBZS, comPrepareO.get());  
                    //公司总计来说，异动出和异动入 数量一定是0，目前没有从西勤《==》华勤 公司间异动的场景  
                    allCenterMap.put(PlaitField.TRANINS,BigDecimal.ZERO);  
                    allCenterMap.put(PlaitField.TRANOUTS,BigDecimal.ZERO);  
                    allCenterMap.put(PlaitField.OTRANINS,BigDecimal.ZERO);  
                    allCenterMap.put(PlaitField.OTRANOUTS,BigDecimal.ZERO);  
  
                    allMap.putAll(allCenterMap);  
                    // 编制余额(职员)  
                    allMap.put(PlaitField.PLAITREST,  
                            ((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.FOBZS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.FOZZS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.FOFS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.XZZTOFS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.XZSXOFS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.TRANINS), BigDecimal.ZERO))  
    //                                .add((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.TRANOUTS), BigDecimal.ZERO))  
                    );  
  
                    // 编制余额（O类）  
                    allMap.put(PlaitAreaField.OPLAITREST,  
                            ((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.OBZS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.OZZS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.OOFS), BigDecimal.ZERO))  
                                    .subtract((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.OTRANINS), BigDecimal.ZERO))  
    //                                .add((BigDecimal)ObjectUtil.defaultIfNull(allMap.get(PlaitField.OTRANOUTS), BigDecimal.ZERO))  
                    );  
  
                    // 核心人才浓度  
                    BigDecimal allGgzzs = new BigDecimal(allMap.get(PlaitField.GGZZS).toString());  
                    BigDecimal allZzzs = new BigDecimal(allMap.get(PlaitField.ZZZS).toString());  
                    if (allGgzzs.compareTo(BigDecimal.ZERO) > 0) {  
                        allMap.put(PlaitField.GGZZS_ZZZS, allGgzzs.divide(allZzzs, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2) + "%");  
                    } else {  
                        allMap.put(PlaitField.GGZZS_ZZZS, "-");  
                    }  
  
                    // 人员增长率 (职员)  
                    BigDecimal allSnzzs = new BigDecimal(allMap.get(PlaitField.SNZZS).toString());  
                    BigDecimal allFozzs = new BigDecimal(allMap.get(PlaitField.FOZZS).toString());  
                    if (allSnzzs.compareTo(BigDecimal.ZERO) != 0) {  
                        allMap.put(PlaitField.FOZZS_SNZZS,  
                                allFozzs.subtract(allSnzzs)  
                                        .divide(allSnzzs, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                                        .setScale(2)+ "%");  
                    } else {  
                        allMap.put(PlaitField.FOZZS_SNZZS, "-");  
                    }  
  
                    // 人员增长率（O类员工）  
                    BigDecimal allOsnzzs = new BigDecimal(allMap.get(PlaitField.OSNZZS).toString());  
                    BigDecimal allObzs = new BigDecimal(allMap.get(PlaitField.OBZS).toString());  
                    if (allOsnzzs.compareTo(BigDecimal.ZERO) != 0) {  
                        allMap.put(PlaitField.OBZS_OSNZZS, allObzs.subtract(allOsnzzs)  
                                .divide(allOsnzzs, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                                .setScale(2)+ "%");  
                    } else {  
                        allMap.put(PlaitField.OBZS_OSNZZS, "-");  
                    }  
                    resultList.add((JSONObject) JSON.toJSON(allMap));  
                }  
        }  
  
        return resultList;  
    }  
  
    /**  
     * 处理待分配编制  
     * @param orgUnitList  
     * @param preparePlaitMap  
     * @param preparePlaitOMap  
     * @param comPrepare  
     * @param comPrepareO  
     * @param sysPrepareMap  
     * @param centerPrepareMap  
     * @param sysPrepareOMap  
     * @param centerPrepareOMap  
     */  
    private void processPrepare(List<OrgWorkForceUnit> orgUnitList, Map<String, BigDecimal> preparePlaitMap,  
                                Map<String, BigDecimal> preparePlaitOMap,  
                                AtomicReference<BigDecimal> comPrepare,  
                                AtomicReference<BigDecimal> comPrepareO,  
                                Map<String, BigDecimal> sysPrepareMap,  
                                Map<String, BigDecimal> centerPrepareMap,  
                                Map<String, BigDecimal> sysPrepareOMap,  
                                Map<String, BigDecimal> centerPrepareOMap) {  
        plaitFormMapper.listPrepare().stream()  
            .forEach(r -> {  
                preparePlaitMap.put(r.getDeptHid(), r.getFobzsCnt() == null ? BigDecimal.ZERO : r.getFobzsCnt());  
                preparePlaitOMap.put(r.getDeptHid(), r.getObzsCnt() == null ? BigDecimal.ZERO : r.getObzsCnt());  
  
  
                // 如果是公司  
                if(Arrays.asList("1", "2", "3", "7").contains(r.getDeptLevel())){  
                    comPrepare.set(comPrepare.get().add(ObjectUtil.defaultIfNull(r.getFobzsCnt(), BigDecimal.ZERO)));  
                    comPrepareO.set(comPrepareO.get().add(ObjectUtil.defaultIfNull(r.getObzsCnt(), BigDecimal.ZERO)));  
                }  
            });  
  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String systemHid = orgWorkForceUnit.getSystemHid();  
            String centerHid = orgWorkForceUnit.getCenterHid();  
            // 如果是分体系的话，加给体系  
            if("7".equals(orgWorkForceUnit.getRDeptLevel())){  
                if(StrUtil.isNotBlank(systemHid)){  
                    sysPrepareMap.put(systemHid,  
                            ObjectUtil.defaultIfNull(sysPrepareMap.get(systemHid), BigDecimal.ZERO)  
                                    .add(ObjectUtil.defaultIfNull(preparePlaitMap.get(orgWorkForceUnit.getRDeptHid()), BigDecimal.ZERO)));  
                    sysPrepareOMap.put(systemHid,  
                            ObjectUtil.defaultIfNull(sysPrepareOMap.get(systemHid), BigDecimal.ZERO)  
                                    .add(ObjectUtil.defaultIfNull(preparePlaitOMap.get(orgWorkForceUnit.getRDeptHid()), BigDecimal.ZERO)));  
                }  
                if(StrUtil.isNotBlank(centerHid)){  
                    centerPrepareMap.put(centerHid,  
                            ObjectUtil.defaultIfNull(centerPrepareMap.get(centerHid), BigDecimal.ZERO)  
                                    .add(ObjectUtil.defaultIfNull(preparePlaitMap.get(orgWorkForceUnit.getRDeptHid()), BigDecimal.ZERO)));  
                    centerPrepareOMap.put(centerHid,  
                            ObjectUtil.defaultIfNull(centerPrepareOMap.get(centerHid), BigDecimal.ZERO)  
                                    .add(ObjectUtil.defaultIfNull(preparePlaitOMap.get(orgWorkForceUnit.getRDeptHid()), BigDecimal.ZERO)));  
                }  
            }  
            // 如果是体系的话，加给中心  
            if("3".equals(orgWorkForceUnit.getRDeptLevel())){  
                if(StrUtil.isNotBlank(centerHid)){  
                    centerPrepareMap.put(centerHid,  
                            ObjectUtil.defaultIfNull(centerPrepareMap.get(centerHid), BigDecimal.ZERO)  
                                    .add(ObjectUtil.defaultIfNull(preparePlaitMap.get(orgWorkForceUnit.getRDeptHid()), BigDecimal.ZERO)));  
  
                    centerPrepareOMap.put(centerHid,  
                            ObjectUtil.defaultIfNull(centerPrepareOMap.get(centerHid), BigDecimal.ZERO)  
                                    .add(ObjectUtil.defaultIfNull(preparePlaitOMap.get(orgWorkForceUnit.getRDeptHid()), BigDecimal.ZERO)));  
                }  
            }  
        }  
  
  
    }  
  
    private void calcTotalAndPrepare(OrgWorkForceUnit beforeUnit, BigDecimal prepareVal, BigDecimal prepareOVal, String beforeKey,  
                                     Map<String, Map<String, BigDecimal>> totalMap, List<JSONObject> resultList,  
                                     String pKey,  
                                     String tKey,  
                                     boolean resetSubSystem,  
                                     boolean resetSystem,  
                                     BigDecimal prepareVal1,  
                                     BigDecimal prepareOVal1,  
                                     Pair<BigDecimal, BigDecimal> inPair,  
                                     Pair<BigDecimal, BigDecimal> outPair) {  
        // 待分配编制  
        OrgWorkForceUnit preparePlaitUnit = new OrgWorkForceUnit();  
        BeanUtil.copyProperties(beforeUnit, preparePlaitUnit);  
        preparePlaitUnit.setDeptName(pKey);  
        if (resetSubSystem) {  
            preparePlaitUnit.setSubSystemName("");  
        }  
        if(resetSystem){  
            preparePlaitUnit.setSystemName("");  
        }  
        Map prepareMap = BeanUtil.beanToMap(preparePlaitUnit);  
        totalMap.get(beforeKey).forEach((k, v) -> {  
            prepareMap.put(k, null);  
        });  
        // FOBZS SNZZS  
        prepareMap.put(PlaitField.FOBZS, ObjectUtil.defaultIfNull(prepareVal, BigDecimal.ZERO));  
        // OBZS OSNZZS  
        prepareMap.put(PlaitField.OBZS, ObjectUtil.defaultIfNull(prepareOVal, BigDecimal.ZERO));  
        resultList.add((JSONObject) JSON.toJSON(prepareMap));  
  
        // 合计  
        OrgWorkForceUnit systemSumUnit = new OrgWorkForceUnit();  
        BeanUtil.copyProperties(beforeUnit, systemSumUnit);  
        systemSumUnit.setDeptName(tKey);  
        if (resetSubSystem) {  
            systemSumUnit.setSubSystemName("");  
        }  
        if(resetSystem){  
            systemSumUnit.setSystemName("");  
        }  
        Map sumMap = BeanUtil.beanToMap(systemSumUnit);  
        sumMap.putAll(totalMap.get(beforeKey));  
  
        Map<String, BigDecimal> deptTotalMap = totalMap.get(beforeKey);  
        // 总计值+待分配编制  
        sumMap.put(PlaitField.FOBZS, ObjectUtil.defaultIfNull(deptTotalMap.get(PlaitField.FOBZS), BigDecimal.ZERO).add(  
                ObjectUtil.defaultIfNull(prepareVal, BigDecimal.ZERO)).add(  
                        ObjectUtil.defaultIfNull(prepareVal1, BigDecimal.ZERO)  
        ));  
        sumMap.put(PlaitField.OBZS, ObjectUtil.defaultIfNull(deptTotalMap.get(PlaitField.OBZS), BigDecimal.ZERO).add(  
                ObjectUtil.defaultIfNull(prepareOVal, BigDecimal.ZERO)).add(  
                ObjectUtil.defaultIfNull(prepareOVal1, BigDecimal.ZERO)  
        ));  
        deptTotalMap.put(PlaitField.FOBZS, ObjectUtil.defaultIfNull(deptTotalMap.get(PlaitField.FOBZS), BigDecimal.ZERO).add(  
                ObjectUtil.defaultIfNull(prepareVal, BigDecimal.ZERO)).add(  
                ObjectUtil.defaultIfNull(prepareVal1, BigDecimal.ZERO)  
        ));  
        deptTotalMap.put(PlaitField.OBZS, ObjectUtil.defaultIfNull(deptTotalMap.get(PlaitField.OBZS), BigDecimal.ZERO).add(  
                ObjectUtil.defaultIfNull(prepareOVal, BigDecimal.ZERO)).add(  
                ObjectUtil.defaultIfNull(prepareOVal1, BigDecimal.ZERO)  
        ));  
  
        inPair = inPair == null ? new Pair(BigDecimal.ZERO, BigDecimal.ZERO) : inPair;  
        outPair = outPair == null ? new Pair(BigDecimal.ZERO, BigDecimal.ZERO) : outPair;  
        // 职员异动入  
        sumMap.put(PlaitField.TRANINS, ObjectUtil.defaultIfNull(inPair.getKey(), BigDecimal.ZERO));  
        // 职员异动出  
        sumMap.put(PlaitField.TRANOUTS, ObjectUtil.defaultIfNull(outPair.getKey(), BigDecimal.ZERO));  
        // O类异动入  
        sumMap.put(PlaitField.OTRANINS, ObjectUtil.defaultIfNull(inPair.getValue(), BigDecimal.ZERO));  
        // O类异动出  
        sumMap.put(PlaitField.OTRANOUTS, ObjectUtil.defaultIfNull(outPair.getValue(), BigDecimal.ZERO));  
  
        // 编制余额（职员）  
        sumMap.put(PlaitField.PLAITREST,  
            ((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.FOBZS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.FOZZS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.FOFS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.XZZTOFS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.XZSXOFS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.TRANINS), BigDecimal.ZERO))  
//                .add((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.TRANOUTS), BigDecimal.ZERO))  
        );  
  
        // 编制余额（O类）  
        sumMap.put(PlaitAreaField.OPLAITREST,  
            ((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.OBZS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.OZZS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.OOFS), BigDecimal.ZERO))  
                .subtract((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.OTRANINS), BigDecimal.ZERO))  
//                .add((BigDecimal)ObjectUtil.defaultIfNull(sumMap.get(PlaitField.OTRANOUTS), BigDecimal.ZERO))  
        );  
  
        // 核心人才浓度  
        BigDecimal ggzzs = ObjectUtil.isEmpty(sumMap.get(PlaitField.GGZZS)) ? BigDecimal.ZERO : new BigDecimal(sumMap.get(PlaitField.GGZZS).toString());  
        BigDecimal zzzs = ObjectUtil.isEmpty(sumMap.get(PlaitField.ZZZS)) ? BigDecimal.ZERO : new BigDecimal(sumMap.get(PlaitField.ZZZS).toString());  
        if (ggzzs.compareTo(BigDecimal.ZERO) > 0) {  
            sumMap.put(PlaitField.GGZZS_ZZZS, ggzzs.divide(zzzs, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                    .setScale(2)+ "%");  
        } else {  
            sumMap.put(PlaitField.GGZZS_ZZZS, "-");  
        }  
  
        // 人员增长率 (职员)  
        BigDecimal snzzs = ObjectUtil.isEmpty(sumMap.get(PlaitField.SNZZS)) ? BigDecimal.ZERO : new BigDecimal(sumMap.get(PlaitField.SNZZS).toString());  
        BigDecimal fozzs = ObjectUtil.isEmpty(sumMap.get(PlaitField.FOZZS)) ? BigDecimal.ZERO : new BigDecimal(sumMap.get(PlaitField.FOZZS).toString());  
        if (snzzs.compareTo(BigDecimal.ZERO) != 0) {  
            sumMap.put(PlaitField.FOZZS_SNZZS,  
                    fozzs.subtract(snzzs)  
                            .divide(snzzs, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                            .setScale(2)+ "%");  
        } else {  
            sumMap.put(PlaitField.FOZZS_SNZZS, "-");  
        }  
  
        // 人员增长率（O类员工）  
        BigDecimal osnzzs = ObjectUtil.isEmpty(sumMap.get(PlaitField.OSNZZS)) ? BigDecimal.ZERO : new BigDecimal(sumMap.get(PlaitField.OSNZZS).toString());  
        BigDecimal obzs = ObjectUtil.isEmpty(sumMap.get(PlaitField.OBZS)) ? BigDecimal.ZERO : new BigDecimal(sumMap.get(PlaitField.OBZS).toString());  
        if (osnzzs.compareTo(BigDecimal.ZERO) != 0) {  
            sumMap.put(PlaitField.OBZS_OSNZZS, obzs.subtract(osnzzs)  
                    .divide(osnzzs, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                    .setScale(2)+ "%");  
        } else {  
            sumMap.put(PlaitField.OBZS_OSNZZS, "-");  
        }  
        resultList.add((JSONObject) JSON.toJSON(sumMap));  
    }  
  
    /**  
     * 获取预离职信息  
     * HIRES: 职员  
     * OHIRES: O类  
     *  
     * @param orgUnitList  
     * @param dataMap  
     */  
    private void processHire(List<OrgWorkForceUnit> orgUnitList, Map<String, Map<String, BigDecimal>> dataMap) {  
  
        Map<String, List<PlaitCntInfo>> deptHidCntMap = plaitFormMapper.listHireCnt(  
                StrUtil.split(paramService.getStringParam(OA_FIRE_WORKFLOW_ID), CharUtil.COMMA)).stream()  
                .filter(s -> StrUtil.isNotBlank(s.getDeptHid()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getDeptHid));  
  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
  
            List<PlaitCntInfo> offerList = deptHidCntMap.getOrDefault(deptHid, new ArrayList());  
  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
            deptMap.put(PlaitField.HIRES, deptMap.getOrDefault(PlaitField.HIRES, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            deptMap.put(PlaitField.OHIRES, deptMap.getOrDefault(PlaitField.OHIRES, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            //预离职单数量（O类员工）15薪  
            deptMap.put(PlaitField.OHIRES_O15, deptMap.getOrDefault(PlaitField.OHIRES_O15, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()) && "1".equals(offer.getO15()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            deptMap.put(PlaitField.GGHIRES, deptMap.getOrDefault(PlaitField.GGHIRES, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()) && CollUtil.newArrayList("1", "2", "3")  
                            .contains(offer.getVocationalLevel()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
    }  
  
  
    /**  
     * 异动编制  
     * @param orgUnitList  
     * @param dataMap  
     * @param fSystemInMap  
     * @param systemInMap  
     * @param centerInMap  
     * @param fSystemOutMap  
     * @param systemOutMap  
     * @param centerOutMap  
     */  
    private void processTran(List<OrgWorkForceUnit> orgUnitList, Map<String, Map<String, BigDecimal>> dataMap,  
                             Map<String, Pair<BigDecimal, BigDecimal>> fSystemInMap,  
                             Map<String, Pair<BigDecimal, BigDecimal>> systemInMap,  
                             Map<String, Pair<BigDecimal, BigDecimal>> centerInMap,  
                             Map<String, Pair<BigDecimal, BigDecimal>> fSystemOutMap,  
                             Map<String, Pair<BigDecimal, BigDecimal>> systemOutMap,  
                             Map<String, Pair<BigDecimal, BigDecimal>> centerOutMap) {  
  
        List<PlaitCntInfo> plaitCntInfoList = plaitFormMapper.listTranCnt();  
        List<PlaitCntInfo> plaitCntInfoEffectList = plaitFormMapper.listTranCntEffect();  
  
        if(CollUtil.isNotEmpty(plaitCntInfoEffectList)){  
            plaitCntInfoList.addAll(plaitCntInfoEffectList);  
        }  
  
        // 部门异动入  
        Map<String, List<PlaitCntInfo>> deptRHidCntMap = plaitCntInfoList.stream()  
                .filter(s -> StrUtil.isNotBlank(s.getNewDept()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getNewDept));  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
            List<PlaitCntInfo> offerList = deptRHidCntMap.getOrDefault(deptHid, new ArrayList());  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
            deptMap.put(PlaitField.TRANINS, deptMap.getOrDefault(PlaitField.TRANINS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            deptMap.put(PlaitField.OTRANINS, deptMap.getOrDefault(PlaitField.OTRANINS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            //待异动入人数-15薪  
            deptMap.put(PlaitField.OTRANINS_O15, deptMap.getOrDefault(PlaitField.OTRANINS_O15, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()) && "1".equals(offer.getO15()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
  
        // 部门异动出  
        deptRHidCntMap = plaitCntInfoList.stream()  
                .filter(s -> StrUtil.isNotBlank(s.getPreDept()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getPreDept));  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
            List<PlaitCntInfo> offerList = deptRHidCntMap.getOrDefault(deptHid, new ArrayList());  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
            deptMap.put(PlaitField.TRANOUTS, deptMap.getOrDefault(PlaitField.TRANOUTS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            deptMap.put(PlaitField.OTRANOUTS, deptMap.getOrDefault(PlaitField.OTRANOUTS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            //待异动出人数-15薪  
            deptMap.put(PlaitField.OTRANOUTS_O15, deptMap.getOrDefault(PlaitField.OTRANOUTS_O15, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()) && "1".equals(offer.getO15()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
  
        // 分体系异动入  
        plaitCntInfoList.stream()  
            .filter(s -> !StrUtil.equals(  
                    StrUtil.join(StrUtil.COMMA, s.getNewCenter(), s.getNewSystem(), s.getNewFSystem()),  
                    StrUtil.join(StrUtil.COMMA, s.getPreCenter(), s.getPreSystem(), s.getPreFSystem())  
            )).filter(s -> StrUtil.isNotBlank(s.getNewFSystem()))  
            .collect(Collectors.groupingBy(PlaitCntInfo::getNewFSystem)).forEach((fSystem, subList) -> {  
                fSystemInMap.put(fSystem,  
                    new Pair(  
                        subList.stream()  
                            .filter(offer -> "0".equals(offer.getEmployeeType()))  
                            .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add),  
                        subList.stream()  
                            .filter(offer -> "4".equals(offer.getEmployeeType()))  
                            .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)  
                    )  
                );  
            });  
  
        // 分体系异动出  
        plaitCntInfoList.stream()  
                .filter(s -> !StrUtil.equals(  
                        StrUtil.join(StrUtil.COMMA, s.getNewCenter(), s.getNewSystem(), s.getNewFSystem()),  
                        StrUtil.join(StrUtil.COMMA, s.getPreCenter(), s.getPreSystem(), s.getPreFSystem())  
                )).filter(s -> StrUtil.isNotBlank(s.getPreFSystem()))  
            .collect(Collectors.groupingBy(PlaitCntInfo::getPreFSystem)).forEach((fSystem, subList) -> {  
                fSystemOutMap.put(fSystem,  
                        new Pair(  
                            subList.stream()  
                                .filter(offer -> "0".equals(offer.getEmployeeType()))  
                                .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add),  
                            subList.stream()  
                                .filter(offer -> "4".equals(offer.getEmployeeType()))  
                                .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)  
                        )  
                );  
            });  
  
        // 体系异动入  
        plaitCntInfoList.stream()  
                .filter(s -> !StrUtil.equals(  
                        StrUtil.join(StrUtil.COMMA, s.getNewCenter(), s.getNewSystem()),  
                        StrUtil.join(StrUtil.COMMA, s.getPreCenter(), s.getPreSystem())  
                )).filter(s -> StrUtil.isNotBlank(s.getNewSystem()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getNewSystem)).forEach((system, subList) -> {  
                    systemInMap.put(system,  
                        new Pair(  
                            subList.stream()  
                                .filter(offer -> "0".equals(offer.getEmployeeType()))  
                                .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add),  
                            subList.stream()  
                                .filter(offer -> "4".equals(offer.getEmployeeType()))  
                                .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)  
                        )  
                    );  
                });  
  
        // 体系异动出  
        plaitCntInfoList.stream()  
                .filter(s -> !StrUtil.equals(  
                        StrUtil.join(StrUtil.COMMA, s.getNewCenter(), s.getNewSystem()),  
                        StrUtil.join(StrUtil.COMMA, s.getPreCenter(), s.getPreSystem())  
                )).filter(s -> StrUtil.isNotBlank(s.getPreSystem()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getPreSystem)).forEach((system, subList) -> {  
                    systemOutMap.put(system,  
                        new Pair(  
                            subList.stream()  
                                .filter(offer -> "0".equals(offer.getEmployeeType()))  
                                .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add),  
                            subList.stream()  
                                .filter(offer -> "4".equals(offer.getEmployeeType()))  
                                .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)  
                        )  
                    );  
                });  
  
        // 中心异动入  
        plaitCntInfoList.stream()  
                .filter(s -> !StrUtil.equals(  
                        StrUtil.join(StrUtil.COMMA, s.getNewCenter()),  
                        StrUtil.join(StrUtil.COMMA, s.getPreCenter())  
                )).filter(s -> StrUtil.isNotBlank(s.getNewCenter()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getNewCenter)).forEach((center, subList) -> {  
                    centerInMap.put(center,  
                            new Pair(  
                                subList.stream()  
                                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add),  
                                subList.stream()  
                                    .filter(offer -> "4".equals(offer.getEmployeeType()))  
                                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)  
                            )  
                    );  
                });  
  
        // 中心异动出  
        plaitCntInfoList.stream()  
                .filter(s -> !StrUtil.equals(  
                        StrUtil.join(StrUtil.COMMA, s.getNewCenter()),  
                        StrUtil.join(StrUtil.COMMA, s.getPreCenter())  
                )).filter(s -> StrUtil.isNotBlank(s.getPreCenter()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getPreCenter)).forEach((center, subList) -> {  
                    centerOutMap.put(center,  
                            new Pair(  
                                subList.stream()  
                                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add),  
                                subList.stream()  
                                    .filter(offer -> "4".equals(offer.getEmployeeType()))  
                                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)  
                            )  
                    );  
                });  
    }  
  
    /**  
     * 去年年底在岗人员  
     * SNZZS：职员（废弃）  
     * OSNZZS：O类  
     *  
     * @param orgUnitList  
     * @param dataMap  
     */  
    private void processLastYear(List<OrgWorkForceUnit> orgUnitList, Map<String, Map<String, BigDecimal>> dataMap) {  
  
        Map<String, List<PlaitCntInfo>> deptHidCntMap = plaitFormMapper.listLastYearCnt().stream()  
                .filter(s -> StrUtil.isNotBlank(s.getDeptHid()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getDeptHid));  
  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
  
            List<PlaitCntInfo> offerList = deptHidCntMap.getOrDefault(deptHid, new ArrayList());  
  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
  
            deptMap.put(PlaitField.SNZZS, deptMap.getOrDefault(PlaitField.SNZZS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            deptMap.put(PlaitField.OSNZZS, deptMap.getOrDefault(PlaitField.OSNZZS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
    }  
  
    /**  
     * 计算部门编制  
     * <p>  
     * FOBZS：职员  
     * OBZS：O类  
     *  
     * @param orgUnitList  
     * @param dataMap  
     */  
    private void processDeptPlait(List<OrgWorkForceUnit> orgUnitList, Map<String, Map<String, BigDecimal>> dataMap) {  
  
        Map<String, List<PlaitCntInfo>> deptHidCntMap = plaitFormMapper.listDeptPlaitCnt().stream()  
                .filter(s -> StrUtil.isNotBlank(s.getDeptHid()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getDeptHid));  
  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
            List<PlaitCntInfo> offerList = deptHidCntMap.getOrDefault(deptHid, new ArrayList());  
  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
  
            deptMap.put(PlaitField.FOBZS, deptMap.getOrDefault(PlaitField.FOBZS, BigDecimal.ZERO).add(offerList.stream()  
                    .map(PlaitCntInfo::getFobzsCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            deptMap.put(PlaitField.OBZS, deptMap.getOrDefault(PlaitField.OBZS, BigDecimal.ZERO).add(offerList.stream()  
                    .map(PlaitCntInfo::getObzsCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
    }  
  
    /**  
     * 核心人才浓度  
     * <p>  
     * FOZZS: 部门在岗数(职员)  
     * 在岗数（非O）=用工方式-正式工+员工类型-职员  
     * OZZS: 部门在岗数(O类)  
     * OZZS_O15: 部门在岗数(O类)15薪  
     * <p>  
     * 1）用工方式=正式工+员工类型=O类员工  
     * 2）用工方式=实习生+招聘渠道=QT实习生+部门=评测中心  
     * 3）员工类型=O类员工+用工方式=学生工+职工类型=DL1 or DL2  
     * 4）员工类型=O类员工+用工方式=派遣工+职工类型=DL1  
     * <p>  
     * v2=GGZZS,w2=ZZZS  
     * case(v2>0,NumRound(v2/w2*100,0.1) + '%','-')     *     * @param orgUnitList  
     * @param dataMap  
     */  
    private void processCorePercent(List<OrgWorkForceUnit> orgUnitList, Map<String, Map<String, BigDecimal>> dataMap) {  
  
        Map<String, List<PlaitCntInfo>> deptHidCntMap = plaitFormMapper.listCorePercent().stream()  
                .filter(s -> StrUtil.isNotBlank(s.getDeptHid()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getDeptHid));  
  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
  
            List<PlaitCntInfo> offerList = deptHidCntMap.getOrDefault(deptHid, new ArrayList());  
  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
  
            // 部门在岗人数(职员)  员工类型=职员，用工方式=（正式工、顾问），员工状态=在职  
            deptMap.put(PlaitField.FOZZS, deptMap.getOrDefault(PlaitField.FOZZS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()) &&  
                            ("1".equals(offer.getLaborType()) || "5".equals(offer.getLaborType())))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            // 部门在岗人数(非制造O)  小员工类型=非制造O类 且 (招聘渠道=QT实习生 或者 用工方式=正式工）且 当天员工状态=在职、实习  
            deptMap.put(PlaitField.OZZS, deptMap.getOrDefault(PlaitField.OZZS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getSmallEmployeeType()) &&  
                            ("1".equals(offer.getLaborType()) || "100006003".equals(offer.getZhaoPinQuDao())))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            // 部门在岗人数-15薪(非制造O)  小员工类型=非制造O类 且 15薪 且 (招聘渠道=QT实习生 或者 用工方式=正式工）且 当天员工状态=在职、实习  
            deptMap.put(PlaitField.OZZS_O15, deptMap.getOrDefault(PlaitField.OZZS_O15, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getSmallEmployeeType()) && "1".equals(offer.getO15()) &&  
                            ("1".equals(offer.getLaborType()) || "100006003".equals(offer.getZhaoPinQuDao())))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            // 骨干浓度  
            deptMap.put(PlaitField.GGZZS, deptMap.getOrDefault(PlaitField.GGZZS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()) && CollUtil.newArrayList("0", "1", "2", "3")  
                            .contains(offer.getVocationalLevel()) &&  
                            !StrUtil.blankToDefault(offer.getJobName(), StrUtil.EMPTY).endsWith(PlaitField.OUT_JOB)  
                    )  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            deptMap.put(PlaitField.ZZZS, deptMap.getOrDefault(PlaitField.ZZZS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()) && CollUtil.newArrayList("0", "1", "2", "3", "4")  
                            .contains(offer.getVocationalLevel()) &&  
                            !StrUtil.blankToDefault(offer.getJobName(), StrUtil.EMPTY).endsWith(PlaitField.OUT_JOB)  
                    )  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            if (deptMap.get(PlaitField.GGZZS).compareTo(BigDecimal.ZERO) > 0) {  
                deptMap.put(PlaitField.GGZZS + "_" + PlaitField.ZZZS,  
                        deptMap.get(PlaitField.GGZZS).divide(deptMap.get(PlaitField.ZZZS), 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))  
                                .setScale(2));  
            }  
        }  
    }  
  
  
    /**  
     * 计算在途实习生社招  
     * OOFS 社招在途offer(O类)  
     * OOFS_O15 社招在途offer(O类)15薪  
     * FOFS 社招在途offer(职员)  
     */    private void processSocialOffer(List<OrgWorkForceUnit> orgUnitList, Map<String, Map<String, BigDecimal>> dataMap) {  
  
        // 职员  
        Map<String, List<PlaitCntInfo>> deptHidCntMap = plaitFormMapper.listSocialOfferEmpCnt().stream()  
                .filter(s -> StrUtil.isNotBlank(s.getDeptHid()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getDeptHid));  
  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
  
            List<PlaitCntInfo> offerList = deptHidCntMap.getOrDefault(deptHid, new ArrayList());  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
            deptMap.put(PlaitField.FOFS, deptMap.getOrDefault(PlaitField.FOFS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
  
        // O类  
        deptHidCntMap = plaitFormMapper.listSocialOfferOEmpCnt().stream()  
                .filter(s -> StrUtil.isNotBlank(s.getDeptHid()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getDeptHid));  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
            List<PlaitCntInfo> offerList = deptHidCntMap.getOrDefault(deptHid, new ArrayList());  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
            deptMap.put(PlaitField.OOFS, deptMap.getOrDefault(PlaitField.OOFS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
  
            //社招在途offer(O类)15薪,15薪=岗位价值是B3或B4且职级O7,O8,O9"  或者  "岗位名称是xxx班组长  
            deptMap.put(PlaitField.OOFS_O15, deptMap.getOrDefault(PlaitField.OOFS_O15, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "4".equals(offer.getEmployeeType()) &&  
                            ((CollUtil.newArrayList("B3","B4").contains(offer.getPosLevel()) &&  
                                    CollUtil.newArrayList("O7","O8","O9").contains(offer.getEmpRank())) ||  
                                    StrUtil.endWith(offer.getPosName(),"班组长")))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
    }  
  
    /**  
     * 计算在途校招 （校招没有O类）  
     * XZSXOFS 校招实习生数(职员)  
     * XZZTOFS 应届生在途数(职员)  
     */    private void processSchoolOffer(List<OrgWorkForceUnit> orgUnitList, Map<String, Map<String, BigDecimal>> dataMap) {  
  
        Map<String, List<PlaitCntInfo>> deptHidCntMap = plaitFormMapper.listSchoolOfferCnt().stream()  
                .filter(s -> StrUtil.isNotBlank(s.getDeptHid()))  
                .collect(Collectors.groupingBy(PlaitCntInfo::getDeptHid));  
  
        for (OrgWorkForceUnit orgWorkForceUnit : orgUnitList) {  
            String deptHid = orgWorkForceUnit.getDeptHid();  
  
            List<PlaitCntInfo> offerList = deptHidCntMap.getOrDefault(deptHid, new ArrayList<>());  
  
            Map<String, BigDecimal> deptMap = dataMap.get(deptHid);  
  
            deptMap.put(PlaitField.XZSXOFS, BigDecimal.ZERO);  
            deptMap.put(PlaitField.XZZTOFS, deptMap.getOrDefault(PlaitField.XZZTOFS, BigDecimal.ZERO).add(offerList.stream()  
                    .filter(offer -> "0".equals(offer.getEmployeeType()))  
                    .map(PlaitCntInfo::getCnt).reduce(BigDecimal.ZERO, BigDecimal::add)));  
        }  
    }  
  
  
    public File down(String orgHid, String empCode) {  
  
        // 模板excel  
        String templatePath = "file_templates/plait.xlsx";  
        //写入到临时文件  
        String fileName = "总编制" + ExcelTypeEnum.XLSX.getValue();  
        String filePath = System.getProperty("java.io.tmpdir") + StrUtil.SLASH + fileName;  
  
        // 生成数据  
        List<JSONObject> dataList = this.findAll(orgHid, empCode);  
        // 分体系合并  
        Map<Integer, Integer> subSystemMap = new HashMap<>(16);  
        Integer beforeCnt = 1;  
        for (int i = 0; i < dataList.size(); i++) {  
            JSONObject dataJson = dataList.get(i);  
            if ("总计".equals(dataJson.get("deptName"))) {  
                subSystemMap.put(i + 1, beforeCnt);  
                beforeCnt = i + 2;  
            }  
            if ("全体系总计".equals(dataJson.get("deptName"))) {  
                subSystemMap.put(i + 1, beforeCnt);  
                beforeCnt = i + 2;  
            }  
        }  
  
        // 体系合并  
        Map<Integer, Integer> systemMap = new HashMap<>(16);  
        beforeCnt = 1;  
        String beforeValue = CollUtil.isNotEmpty(dataList) ? dataList.get(0).getString("systemName") : "";  
        for (int i = 0; i < dataList.size(); i++) {  
            JSONObject dataJson = dataList.get(i);  
            if (!StrUtil.equals(beforeValue, dataJson.getString("systemName"))) {  
                systemMap.put(i, beforeCnt);  
                beforeCnt = i + 1;  
                beforeValue = dataJson.getString("systemName");  
            }  
        }  
  
        // 中心合并  
        Map<Integer, Integer> centerMap = new HashMap<>(16);  
        beforeCnt = 1;  
        beforeValue = CollUtil.isNotEmpty(dataList) ? dataList.get(0).getString("centerName") : "";  
        for (int i = 0; i < dataList.size(); i++) {  
            JSONObject dataJson = dataList.get(i);  
            if (!StrUtil.equals(beforeValue, dataJson.getString("centerName"))) {  
                centerMap.put(i, beforeCnt);  
                beforeCnt = i + 1;  
                beforeValue = dataJson.getString("centerName");  
            }  
        }  
  
        List<Integer> totalIdxList = new ArrayList<>();  
        List<Integer> prepareIdxList = new ArrayList<>();  
        List<Integer> prepareOIdxList = new ArrayList<>();  
        List<Integer> sysTotalIdxList = new ArrayList<>();  
        List<Integer> sysPrepareIdxList = new ArrayList<>();  
        List<Integer> sysPrepareOIdxList = new ArrayList<>();  
        for (int i = 0; i < dataList.size(); i++) {  
            JSONObject dataJson = dataList.get(i);  
            if ("总计".equals(dataJson.get("deptName"))) {  
                totalIdxList.add(i + 1);  
            }  
            if ("待分配编制(职员)".equals(dataJson.get("deptName"))) {  
                prepareIdxList.add(i + 1);  
            }  
            if ("待分配编制(非制造O)".equals(dataJson.get("deptName"))) {  
                prepareOIdxList.add(i + 1);  
            }  
            if ("全体系总计".equals(dataJson.get("deptName"))) {  
                sysTotalIdxList.add(i + 1);  
            }  
            if ("全体系待分配编制(职员)".equals(dataJson.get("deptName"))) {  
                sysPrepareIdxList.add(i + 1);  
            }  
            if ("全体系待分配编制(非制造O)".equals(dataJson.get("deptName"))) {  
                sysPrepareOIdxList.add(i + 1);  
            }  
        }  
  
        ExcelWriter excelWriter = null;  
        try {  
            excelWriter = EasyExcel  
                    .write(filePath)  
                    .withTemplate(getTemplateInputStream(templatePath))  
                    // .registerWriteHandler(new PlaitExcelMergeUtil(subSystemMap, systemMap, centerMap))  
                    // .registerWriteHandler(new PlaitCellStyleStrategy(totalIdxList, prepareIdxList, prepareOIdxList, sysTotalIdxList, sysPrepareIdxList, sysPrepareOIdxList))                    .build();  
            WriteSheet recordSheet = EasyExcel.writerSheet(0, "总编制").build();  
            List<PlaitTotalDataDTO> dtoList = Convert.toList(PlaitTotalDataDTO.class, dataList);  
            excelWriter.fill(dtoList, recordSheet);  
        } catch (Exception ex) {  
            ex.printStackTrace();  
            throw new BusinessException(ex.getMessage());  
        } finally {  
            if (excelWriter != null) {  
                excelWriter.finish();  
            }  
        }  
        return new File(filePath);  
    }  
  
    /**  
     * 获取文件路径  
     *  
     * @param relativeFilePath  
     * @return  
     * @throws Exception  
     */    private InputStream getTemplateInputStream(String relativeFilePath) throws Exception {  
        InputStream stream = getClass().getClassLoader().getResourceAsStream(relativeFilePath);  
        if (stream == null) {  
            ClassPathResource classPathResource = new ClassPathResource(relativeFilePath);  
            stream = classPathResource.getInputStream();  
        }  
        return stream;  
    }  
  
    /**  
     * 职员O类编制  
     */  
    public List<PlaitZyAndOlVo> listPlaitZyAndOl() {  
        return plaitFormMapper.listPlaitZyAndOl();  
    }  
}
```

```
package com.huaqin.hcm.controller.esb.plait;  
  
import com.google.common.collect.ImmutableMap;  
import com.huaqin.hcm.entity.dto.esb.EsbRequestDTO;  
import com.huaqin.hcm.entity.dto.esb.EsbResponseDTO;  
import com.huaqin.hcm.service.plait.PlaitService;  
import lombok.RequiredArgsConstructor;  
import lombok.extern.slf4j.Slf4j;  
import org.apache.commons.lang3.exception.ExceptionUtils;  
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.RequestBody;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  
  
import javax.annotation.Resource;  
  
/**  
 * esb 获得编制相关数据  
 *  
 * @author 100563122  
 * @date 2025/7/21  
 */@Slf4j  
@RequiredArgsConstructor  
@RestController  
@RequestMapping("/esb/plait")  
public class EsbPlaitController {  
  
    @Resource  
    private PlaitService plaitService;  
  
    /**  
     * 查询职员和O类 的编制数据  
     * @param esbRequestDTO  
     * @return  
     */  
    @PostMapping("/listPlaitZyAndOl")  
    public EsbResponseDTO<Object> listPlaitZyAndOl(@RequestBody EsbRequestDTO<Object> esbRequestDTO){  
        try{  
            return EsbResponseDTO.success(esbRequestDTO.getReqHeader().getRequestId(),plaitService.listPlaitZyAndOl());  
        }catch (Exception e){  
            log.error(ExceptionUtils.getStackTrace(e));  
            return EsbResponseDTO.failed(esbRequestDTO.getReqHeader().getRequestId(), ImmutableMap.of("result","failed","message",e.getMessage()));  
        }  
    }  
  
  
}
```