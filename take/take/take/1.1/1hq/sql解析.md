

# 2
## 组科室异动
组科室异动 在途和未来生效 用来生成编制报表

```sql
-- 组科室 异动
SELECT  depthid,  
        nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
FROM  
    (  
        SELECT  
            OAAFT.C_GRUP_HID  depthid,--组科室待异动入 在途  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE C_EMPTYPE,  
            EG.C_O15 o15  
        FROM
        -- 异动在途信息集 pre连接全组织、new连接全组织、新岗位id连接岗位表、 员工Id连接员工分类表、员工id连接员工表主键
        -- 员工组织表eid 连接员工表主键
		 TB_STA_INTRANSFERINFO YD,  
             TB_ORG_POSITION OPM,  
             TB_STA_EMP_ORG EG,  
             TB_ORG_ORGUNITALL OABEF,  
             TB_ORG_ORGUNITALL OAAFT,  
             TB_STA_EMP E,  
             TB_STA_EMP_CLASS TC  
        WHERE YD.C_PRE_UNITHID = OABEF.C_HID  
          AND YD.C_NEW_POSHID=OPM.C_HID  
          AND YD.C_NEW_UNITHID = OAAFT.C_HID  
          AND YD.C_EMPLOYEE_ID = E.C_OID  
          AND YD.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        AND EG.C_EMPLOYEE_ID = E.C_OID  
        AND EG.C_BEGIN_DATE <= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)  
        AND EG.C_DEPT_TYPE = '1'  
        and EG.C_EMPLOYEE_STATUS in('2','11')  
        AND (OAAFT.C_GRUP_HID IS NOT NULL AND (OABEF.C_GRUP_HID IS NULL OR OAAFT.C_GRUP_HID <>  
OABEF.C_GRUP_HID))  


UNION ALL  


SELECT  
    OAAFT.C_GRUP_HID  depthid,--组科室待异动入 未来生效  
    EO.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EO.C_EMPTYPE,  
    EO.C_O15 o15  
FROM  
--     人员流动信息表  主键 连接员工组织表的 异动id 员工组织连接 员工类型 还连接员工 流动表连接其前后的全组织   
TB_STA_EMP_TURNOVER TU,  
    TB_ORG_ORGUNITALL OABEF,  
    TB_ORG_ORGUNITALL OAAFT,  
    TB_STA_EMP E,  
    TB_STA_EMP_ORG EO,  
    TB_STA_EMP_CLASS TC  
WHERE  
        EO.C_TURNOVER_ID = TU.C_OID  
  AND E.C_OID = EO.C_EMPLOYEE_ID  
  AND EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
  AND EO.C_DEPT_TYPE = '1'  
  AND EO.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EO.C_END_DATE > TRUNC(SYSDATE)  
  AND EO.C_DEPT_TYPE = '1'  
  AND TU.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND TU.C_PREV_DEPT_HID = OABEF.C_HID  
  AND TU.C_NEW_DEPT_HID = OAAFT.C_HID  
  AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND (OAAFT.C_GRUP_HID IS NOT NULL AND (OABEF.C_GRUP_HID IS NULL OR OAAFT.C_GRUP_HID <>  
OABEF.C_GRUP_HID))  
) m  
GROUP BY m.depthid
```
注意最后面有一个不等判断什么的
是用来判断，组级上的异动才算有效
组内异动不算


## 公司预入职
```sql
-- 公司 预入职
SELECT  
    DISTINCT deptHid,  
             nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
             nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
             nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_COMPANY_HID  deptHid, --公司预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
--             CASE WHEN ( OPM.C_CLASS_ID IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
--             CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
            CASE WHEN ( jc.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
--          预入职表  里的部门id link  组织单元表 全组织表  岗位id link 左连接岗位表  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
                LEFT JOIN TB_ORG_JOB_CLASS jc on H.C_POS_LEVEL = jc.C_OID  
        WHERE OAL.C_HID = H.C_DEPT_HID --最小组织为部门 连接 预入职表的部门  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
	        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
          and jc.C_STATUS = '1'  
          
        UNION ALL  

SELECT  
    EG.C_COMPANY_HID depthid, ---公司预入职 未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
-- 员工 主键连接 员工id的员工组织  这个的主键 连接 员工组织append信息  eid连接 eid员工类比 
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
    ) m GROUP BY m.deptHid
```
注意字典管理中的职级和需求中的职级可能不一样
因为前者可能是主键，后者是名称O7之类的

v1
```sql
SELECT  
DISTINCT deptHid,  
nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
SELECT  
OAL.C_COMPANY_HID  deptHid, --公司预入职在途  
H.C_EMPLOYEE_TYPE ,  
H.C_YONGGONGXINGSHI,  
H.C_ZHAOPINQUDAO,  
OPM.C_EMP_TYPE,  
H.C_CODE,  
CASE WHEN ( JC.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
FROM  
--          预入职表  里的部门id link   全组织表hid   岗位id link 左连接岗位表hid  职级poslevel连接 jobclass oid 
--全组织表hid link 组织表hid 
TB_ORG_ORGUNIT U,  
TB_ORG_ORGUNITALL OAL,  
TB_STA_PREPARE_HIRE H  
LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE &lt;= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
LEFT JOIN TB_ORG_JOB_CLASS JC ON H.C_POS_LEVEL = JC.C_OID AND JC.C_STATUS = '1'  
WHERE OAL.C_HID = H.C_DEPT_HID  
AND H.C_STATUS = 'preparing'  
AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
AND OAL.C_HID = U.C_HID  
AND U.C_STATUS = '1'  
AND U.C_BEGIN_DATE &lt;=  TRUNC(SYSDATE)  
AND U.C_END_DATE >=  TRUNC(SYSDATE)  
UNION ALL  
SELECT  
EG.C_COMPANY_HID depthid, ---公司预入职 未来生效  
EG.C_EMPLOYEE_TYPE ,  
TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
E.C_ZHAOPINQUDAO,  
EG.C_EMPTYPE,  
E.C_CODE,  
EG.C_O15 o15  
FROM  
-- 员工表主键 连员工组织表员工id 
-- 员工组织表员工Id 连 员工类别表 员工id； 员工组织表主键 连 全组织表主键 
TB_STA_EMP E,  
TB_STA_EMP_CLASS TC,  
TB_STA_EMP_ORG EG,  
TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
AND EG.c_end_date > TRUNC(SYSDATE)  
AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
AND EG.C_DEPT_TYPE ='1'  
AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
AND EOA.C_OID = EG.C_OID  
) m GROUP BY m.deptHid
```

## 公司在职

公司下的  在职   职员，非制造o，15薪

```sql

  
SELECT  
    EO.C_COMPANY_HID deptHid,--公司  
    nvl(sum(CASE WHEN  EO.C_EMPLOYEE_TYPE = '0'  AND TC.C_LABOR_TYPE in('1','5')  THEN 1 ELSE 0 END),0) zyOnjobCnt,  
    nvl(sum(CASE WHEN  EO.C_EMPTYPE = '4' AND (TC.C_LABOR_TYPE = '1' OR E.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) oOnjobCnt,  
    nvl(sum(CASE WHEN  EO.C_EMPTYPE = '4' AND (TC.C_LABOR_TYPE = '1' OR E.C_ZHAOPINQUDAO = '100006003' ) AND EO.C_O15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt,  
    COUNT(1) amount  
FROM  
	-- e.pk link eo.eid; oa.pk link eo.pk  
	-- 员工主键 连 员工组织员工id ； 全组织主键 连 员工组织主键；
	-- 员工类别员工id 连 员工组织员工id 
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORGAPPEND OA,  
    TB_STA_EMP_ORG EO  
WHERE E.C_OID=EO.C_EMPLOYEE_ID AND OA.C_OID=EO.C_OID  
  AND EO.C_BEGIN_DATE <= TRUNC(SYSDATE) AND EO.C_END_DATE > TRUNC(SYSDATE) AND EO.C_DEPT_TYPE='1'  
        AND EO.C_EMPLOYEE_STATUS in('2','11')  
       -- AND EO.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND TC.C_EMPLOYEE_ID = EO.C_EMPLOYEE_ID  
        AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
  
        AND (EO.C_DEPT_HID IN (
        -- 选出连接完之后的 组织id 并且进行递归向下查找 目的找出所有位于华勤下的所有组织 
         SELECT R1.C_HID  
        FROM (  
        -- 
        SELECT U1.C_HID,R1.C_ORG_HID, R1.C_SUPERIOR_HID  
        -- 组织表 hid 连 组织隶属hid 
        FROM TB_ORG_ORGUNIT U1, TB_ORG_UNITRELATION R1  
        WHERE U1.C_HID = R1.C_ORG_HID  
AND U1.C_STATUS = '1'  
AND U1.C_BEGIN_DATE <= TRUNC(SYSDATE)  
AND U1.C_END_DATE >= TRUNC(SYSDATE)  
        AND R1.C_BEGIN_DATE <= TRUNC(SYSDATE)  
AND R1.C_END_DATE >= TRUNC(SYSDATE)  
AND r1.C_STATUS = '1'  
AND r1.C_DIM_HID = '65ca64ab44274d789f8e958abbddc406'  
) R1  
START WITH R1.C_ORG_HID = '6042d4ad0d654ddf819926f023b4d968'  
CONNECT BY PRIOR R1.C_ORG_HID = R1.C_SUPERIOR_HID))  
  
GROUP BY C_COMPANY_HID
```



# 同步人员  人员同步

<!--获取当前所有有效的员工信息 用于同步员工信息到自助库-->
```sql
select distinct e.c_code userName,  
e.c_name nickName,  
c.c_business_email email,  
c.c_mobile_tel phonenumber,  
to_char(to_number(e.c_gender) - 1) sex,  
'0' delFlag,  
'0' status,  
eg.c_employee_type bigEmptype,--员工大类  
eg.c_emptype userType,--员工小类  
j.c_code postCode,--岗位编码  
j.c_name postName,--岗位名称  
jc.C_VOCATIONAL_LEVEL empLevel,--员工层级  
jc.C_NAME empRank,--职级  
jca.c_code postFamily,--岗位族  
jcc.c_code postClass, --岗位类  
eg.C_DEPT_HID  empOrgHid,--员工最小组织hid 主岗  
rbm.c_code empOrgCode,--员工最小组织code 主岗  
rbm.c_name empOrgName, --员工最小组织名称 主岗  
eg.c_workers_type workersType,  --职工类型  
tc.c_labor_type laborType,  --用工方式  
eg.c_yewuquyu businessArea, --业务区域  
eg.c_gongzuochengshi workCity,  --工作城市  
eg.c_o15 ifFifteen,  --是否15薪  
GS.C_CODE companyCode, ---公司code  
GS.c_name companyName, --公司名称  
exp.C_STATUS abroadStatus, --外派状态  
abc.c_country countryOfCity--工作城市所在国家  
from  
    -- 员工组织表连接 其公司 的 对应公司组织 还 eid连接 国际外派信息 gongzuochnegshi连接 基础海外配置 code  
tb_sta_emp                     e,  
tb_sta_emp_org                 eg  
LEFT JOIN TB_ORG_ORGUNIT GS ON GS.C_BEGIN_DATE &lt;=TRUNC(SYSDATE) AND GS.C_END_DATE>=TRUNC(SYSDATE) AND GS.C_HID=eg.C_COMPANY_HID  -- 选择公司hid 的组织  
left join TB_STA_EXPATRIATE exp  -- 国际外派信息  
          on exp.c_employee_id=eg.c_employee_id and exp.C_BEGIN_DATE &lt;=TRUNC(SYSDATE) AND exp.C_END_DATE>=TRUNC(SYSDATE)  
left join TB_ABROAD_BASE_CONFIG abc -- TB_ABROAD_BASE_CONFIG 基础数据海外配置  
          on abc.C_CODE=eg.c_gongzuochengshi and abc.c_type='2',  
tb_sta_emp_class               tc,  -- TB_STA_EMP_CLASS 员工分类信息  
tb_sta_communication           c, -- TB_STA_COMMUNICATION 员工通讯信息  
tb_org_position                op, -- TB_ORG_POSITION 岗位基本信息  
tb_sta_pos_level               pl, -- TB_STA_POS_LEVEL 员工职级信息  
TB_ORG_JOB_CLASS               jc, -- TB_ORG_JOB_CLASS 职级  
tb_org_job_category jca, -- 岗位族 -- TB_ORG_JOB_CATEGORY 岗位族类  
tb_org_job j, -- TB_ORG_JOB 标准岗位基本信息  
tb_org_job_category jcc,  --岗位类 -- TB_ORG_JOB_CATEGORY 岗位族类  
tb_org_orgunit rbm --组织单元  
where -- 员工 oid 连 员工组织 eid  oid 连   通讯信息eid  
    -- 员工组织 岗位hid 连 岗位基本信息 hid  
    -- 员工oid 连 员工职级信息 员工eid  
    -- 员工职级poslevel 连 jobclass oid  
    -- 岗位表 jobhid 连 标准岗位hid  
    e.c_oid = eg.c_employee_idand e.c_oid = c.c_employee_id  
and eg.C_POSITION_HID = op.c_hid  
and e.c_oid = pl.c_employee_id  
and pl. C_POS_LEVEL = jc.c_oid  
and op.c_job_hid = j.c_hid  
  and rbm.c_hid=eg.c_dept_hid  and e.c_oid = tc.c_employee_id  AND jcc.c_hid=j.c_job_family_hid -- C_JOB_FAMILY_HID岗位类  C_OFFICE_FAMLILY_HID岗位族  
  and op.c_office_famlily_hid = jca.c_hid  -- 岗位表 岗位族hid = 岗位族表  

-- job标准岗位 有效
and j.c_status='1'  
and j.c_effective_date_begin &lt; =TRUNC(SYSDATE)  
and j.c_effective_date_end>=TRUNC(SYSDATE)  
--Job class 有效
and jc. C_STATUS = '1'  
-- 员工职级信息 有效
and pl.c_begin_date &lt;= TRUNC(SYSDATE) and pl.c_end_date >= TRUNC(SYSDATE)  
-- 组织岗位，岗位表 有效
and op.c_begin_date &lt;= TRUNC(SYSDATE) and op.c_end_date >= TRUNC(SYSDATE)  
and op.c_status = '1'  
-- 岗位族类 有效 
and jca.c_effective_date_begin &lt;=TRUNC(SYSDATE) and jca.c_effective_date_end>=TRUNC(SYSDATE)  
and jca.c_effective_status='1'  
and jcc.c_effective_date_begin &lt;=TRUNC(SYSDATE) and jcc.c_effective_date_end>=TRUNC(SYSDATE)  
and jcc.c_effective_status='1'  
-- 员工组织 有效
and eg.C_BEGIN_DATE &lt; = TRUNC(SYSDATE) and eg.c_end_date > TRUNC(SYSDATE)  
and eg.c_dept_type = '1'  
and eg.c_employee_status in('2','11')  
-- 组织单元有效 少写（ C_STATUS = '1'
AND rbm.c_begin_date &lt;=TRUNC(CURRENT_DATE) and rbm.c_end_date>=TRUNC(CURRENT_DATE)

-- 员工分类信息 有效
and tc.c_begin_date &lt;= trunc(SYSDATE) and tc.c_end_date >= trunc(SYSDATE)  
and tc.c_labor_type != '8'  

AND eg.C_DEPT_HID not in(  
SELECT R1.C_ORG_HID  
FROM (SELECT U.C_CODE,U.C_NAME, R.C_ORG_HID, R.C_SUPERIOR_HID,R.C_DISPLAYNO,U.C_LEVEL  
FROM TB_ORG_UNITRELATION R, TB_ORG_ORGUNIT U  
WHERE R.C_DIM_HID = '65ca64ab44274d789f8e958abbddc406' AND U.C_HID = R.C_ORG_HID  
AND U.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND U.C_END_DATE >= TRUNC(CURRENT_DATE)  
AND R.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND R.C_END_DATE >= TRUNC(CURRENT_DATE)  
AND R.C_STATUS = '1'  
) R1  
START WITH R1.C_ORG_HID in('4e7ce6dcda23464da3dff565abe9561f')  
CONNECT BY  R1.C_SUPERIOR_HID = PRIOR R1.C_ORG_HID  
)
```

```
-- relation表的有效性
AND R.C_BEGIN_DATE &lt;= TRUNC(CURRENT_DATE) AND R.C_END_DATE >= TRUNC(CURRENT_DATE)  
AND R.C_STATUS = '1'
```


# default sql

```
  
  
  
  
  
  
  
SELECT  
    EO.C_DEPT_HID deptHid,--公司  
    nvl(sum(CASE WHEN  EO.C_EMPLOYEE_TYPE = '0'  AND TC.C_LABOR_TYPE in('1','5')  THEN 1 ELSE 0 END),0) zyOnjobCnt,  
    nvl(sum(CASE WHEN  EO.C_EMPTYPE = '4' AND (TC.C_LABOR_TYPE = '1' OR E.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) oOnjobCnt,  
    nvl(sum(CASE WHEN  EO.C_EMPTYPE = '4' AND (TC.C_LABOR_TYPE = '1' OR E.C_ZHAOPINQUDAO = '100006003' ) AND EO.C_O15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt,  
    COUNT(1) amount  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORGAPPEND OA,  
    TB_STA_EMP_ORG EO  
WHERE E.C_OID=EO.C_EMPLOYEE_ID AND OA.C_OID=EO.C_OID  
  AND EO.C_BEGIN_DATE <= TRUNC(SYSDATE) AND EO.C_END_DATE > TRUNC(SYSDATE) AND EO.C_DEPT_TYPE='1'  
        AND EO.C_EMPLOYEE_STATUS in('2','11')  
       -- AND EO.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND TC.C_EMPLOYEE_ID = EO.C_EMPLOYEE_ID  
        AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
  
        AND (EO.C_DEPT_HID IN ( SELECT R1.C_HID  
        FROM (  
        SELECT U1.C_HID,R1.C_ORG_HID, R1.C_SUPERIOR_HID  
        FROM TB_ORG_ORGUNIT U1, TB_ORG_UNITRELATION R1  
        WHERE U1.C_HID = R1.C_ORG_HID  
        AND R1.C_BEGIN_DATE <= TRUNC(SYSDATE)  
AND R1.C_END_DATE >= TRUNC(SYSDATE)  
AND U1.C_STATUS = '1'  
AND U1.C_BEGIN_DATE <= TRUNC(SYSDATE)  
AND U1.C_END_DATE >= TRUNC(SYSDATE)  
AND r1.C_DIM_HID = '65ca64ab44274d789f8e958abbddc406'  
AND r1.C_STATUS = '1'  
) R1  
START WITH R1.C_ORG_HID = '6042d4ad0d654ddf819926f023b4d968'  
CONNECT BY PRIOR R1.C_ORG_HID = R1.C_SUPERIOR_HID))  
  
GROUP BY C_DEPT_HID  
  
  
--测试v3改正  
SELECT  
    DISTINCT deptHid,  
             nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
             nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
             nvl(sum(CASE WHEN  m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_COMPANY_HID  deptHid, --公司预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( JC.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        LEFT JOIN TB_ORG_JOB_CLASS JC ON H.C_POS_LEVEL = JC.C_OID AND JC.C_STATUS = '1'  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        UNION ALL  
SELECT  
    EG.C_COMPANY_HID depthid, ---公司预入职 未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_EMPTYPE = '4' AND (TC.C_LABOR_TYPE = '1' OR E.C_ZHAOPINQUDAO = '100006003') AND EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
    ) m GROUP BY m.deptHid  
  
  
SELECT  
    OAL.C_DEPT_HID  deptHid,--部门预入职在途  
    H.C_EMPLOYEE_TYPE ,  
    H.C_YONGGONGXINGSHI,  
    H.C_ZHAOPINQUDAO,  
    OPM.C_EMP_TYPE,  
    H.C_CODE,  
    CASE WHEN  OPM.C_EMP_TYPE = '4' AND (H.C_YONGGONGXINGSHI = '1' OR H.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END odydCnt1111, -- 员工类型小类  用工方式 招聘渠道  
    CASE WHEN ( JC.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
FROM  
--             预入职表  里的部门id link  组织单元表 全组织表  岗位id link 左连接岗位表  
TB_ORG_ORGUNIT U,  
TB_ORG_ORGUNITALL OAL,  
TB_STA_PREPARE_HIRE H  
    LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
    LEFT JOIN TB_ORG_JOB_CLASS JC ON H.C_POS_LEVEL = JC.C_OID AND JC.C_STATUS = '1'  
WHERE OAL.C_HID = H.C_DEPT_HID  
  AND H.C_STATUS = 'preparing'  
  AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND OAL.C_HID = U.C_HID  
  AND U.C_STATUS = '1'  
  AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
  AND U.C_END_DATE >=  TRUNC(SYSDATE)  
  AND OAL.C_DEPT_HID IS NOT NULL  
  
-- 1.1  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN  m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_DEPT_HID  deptHid,--部门预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN  OPM.C_EMP_TYPE = '4' AND (H.C_YONGGONGXINGSHI = '1' OR H.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END odydCnt1111, -- 员工类型小类  用工方式 招聘渠道  
            CASE WHEN ( JC.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
--             预入职表  里的部门id link  组织单元表 全组织表  岗位id link 左连接岗位表  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        LEFT JOIN TB_ORG_JOB_CLASS JC ON H.C_POS_LEVEL = JC.C_OID AND JC.C_STATUS = '1'  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_DEPT_HID IS NOT NULL  
  
        UNION ALLSELECT  
    EOA.C_DEPTHID  depthid,--部门预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_EMPTYPE = '4' AND (TC.C_LABOR_TYPE = '1' OR E.C_ZHAOPINQUDAO = '100006003') AND EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_DEPTHID  IS NOT NULL  
    ) m GROUP BY m.deptHid  
  
  
-- 异动  
SELECT  depthid,  
        nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
FROM  
    (  
        SELECT  
            OAAFT.C_CENTER_HID  depthid,--中心待异动入 在途  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE C_EMPTYPE,  
            EG.C_O15 o15  
        FROM TB_STA_INTRANSFERINFO YD,  
             TB_ORG_POSITION OPM,  
             TB_STA_EMP_ORG EG,  
             TB_ORG_ORGUNITALL OABEF,  
             TB_ORG_ORGUNITALL OAAFT,  
             TB_STA_EMP E,  
             TB_STA_EMP_CLASS TC  
        WHERE YD.C_PRE_UNITHID = OABEF.C_HID  
          AND YD.C_NEW_POSHID=OPM.C_HID  
          AND YD.C_NEW_UNITHID = OAAFT.C_HID  
          AND YD.C_EMPLOYEE_ID = E.C_OID  
          AND YD.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        AND EG.C_EMPLOYEE_ID = E.C_OID  
        AND EG.C_BEGIN_DATE <= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)  
        AND EG.C_DEPT_TYPE = '1'  
        and EG.C_EMPLOYEE_STATUS in('2','11')  
        AND (OAAFT.C_CENTER_HID IS NOT NULL AND (OABEF.C_CENTER_HID IS NULL OR OAAFT.C_CENTER_HID <>  
OABEF.C_CENTER_HID))  
UNION ALL  
SELECT  
    OAAFT.C_CENTER_HID  depthid,--中心待异动入 未来生效  
    EO.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EO.C_EMPTYPE,  
    EO.C_O15 o15  
FROM  
    TB_STA_EMP_TURNOVER TU,  
    TB_ORG_ORGUNITALL OABEF,  
    TB_ORG_ORGUNITALL OAAFT,  
    TB_STA_EMP E,  
    TB_STA_EMP_ORG EO,  
    TB_STA_EMP_CLASS TC  
WHERE  
        EO.C_TURNOVER_ID = TU.C_OID  
  AND E.C_OID = EO.C_EMPLOYEE_ID  
  AND EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
  AND EO.C_DEPT_TYPE = '1'  
  AND EO.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EO.C_END_DATE > TRUNC(SYSDATE)  
  AND EO.C_DEPT_TYPE = '1'  
  AND TU.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND TU.C_PREV_DEPT_HID = OABEF.C_HID  
  AND TU.C_NEW_DEPT_HID = OAAFT.C_HID  
  AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND (OAAFT.C_CENTER_HID IS NOT NULL AND (OABEF.C_CENTER_HID IS NULL OR OAAFT.C_CENTER_HID <>  
OABEF.C_CENTER_HID))  
) m  
GROUP BY m.depthid  
  
UNION ALL  
  
SELECT  depthid,  
        nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
FROM  
    (  
        SELECT  
            OAAFT.C_SYSTEM_HID  depthid,--体系待异动入 在途  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE C_EMPTYPE,  
            EG.C_O15 o15  
        FROM TB_STA_INTRANSFERINFO YD,  
             TB_ORG_POSITION OPM,  
             TB_STA_EMP_ORG EG,  
             TB_ORG_ORGUNITALL OABEF,  
             TB_ORG_ORGUNITALL OAAFT,  
             TB_STA_EMP E,  
             TB_STA_EMP_CLASS TC  
        WHERE YD.C_PRE_UNITHID = OABEF.C_HID  
          AND YD.C_NEW_POSHID=OPM.C_HID  
          AND YD.C_NEW_UNITHID = OAAFT.C_HID  
          AND YD.C_EMPLOYEE_ID = E.C_OID  
          AND YD.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        AND EG.C_EMPLOYEE_ID = E.C_OID  
        AND EG.C_BEGIN_DATE <= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)  
        AND EG.C_DEPT_TYPE = '1'  
        and EG.C_EMPLOYEE_STATUS in('2','11')  
        AND (OAAFT.C_SYSTEM_HID IS NOT NULL AND (OABEF.C_SYSTEM_HID IS NULL OR OAAFT.C_SYSTEM_HID <>  
OABEF.C_SYSTEM_HID))  
UNION ALL  
SELECT  
    OAAFT.C_SYSTEM_HID  depthid,--体系待异动入 未来生效  
    EO.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EO.C_EMPTYPE,  
    EO.C_O15 o15  
FROM  
    TB_STA_EMP_TURNOVER TU,  
    TB_ORG_ORGUNITALL OABEF,  
    TB_ORG_ORGUNITALL OAAFT,  
    TB_STA_EMP E,  
    TB_STA_EMP_ORG EO,  
    TB_STA_EMP_CLASS TC  
WHERE  
        EO.C_TURNOVER_ID = TU.C_OID  
  AND E.C_OID = EO.C_EMPLOYEE_ID  
  AND EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
  AND EO.C_DEPT_TYPE = '1'  
  AND EO.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EO.C_END_DATE > TRUNC(SYSDATE)  
  AND EO.C_DEPT_TYPE = '1'  
  AND TU.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND TU.C_PREV_DEPT_HID = OABEF.C_HID  
  AND TU.C_NEW_DEPT_HID = OAAFT.C_HID  
  AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND (OAAFT.C_SYSTEM_HID IS NOT NULL AND (OABEF.C_SYSTEM_HID IS NULL OR OAAFT.C_SYSTEM_HID <>  
OABEF.C_SYSTEM_HID))  
) m  
GROUP BY m.depthid  
  
UNION ALL  
  
SELECT  depthid,  
        nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
FROM  
    (  
        SELECT  
            OAAFT.C_F_SYSTEM_HID  depthid,--分体系待异动入 在途  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE C_EMPTYPE,  
            EG.C_O15 o15  
        FROM TB_STA_INTRANSFERINFO YD,  
             TB_ORG_POSITION OPM,  
             TB_STA_EMP_ORG EG,  
             TB_ORG_ORGUNITALL OABEF,  
             TB_ORG_ORGUNITALL OAAFT,  
             TB_STA_EMP E,  
             TB_STA_EMP_CLASS TC  
        WHERE YD.C_PRE_UNITHID = OABEF.C_HID  
          AND YD.C_NEW_POSHID=OPM.C_HID  
          AND YD.C_NEW_UNITHID = OAAFT.C_HID  
          AND YD.C_EMPLOYEE_ID = E.C_OID  
          AND YD.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        AND EG.C_EMPLOYEE_ID = E.C_OID  
        AND EG.C_BEGIN_DATE <= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)  
        AND EG.C_DEPT_TYPE = '1'  
        and EG.C_EMPLOYEE_STATUS in('2','11')  
        AND (OAAFT.C_F_SYSTEM_HID IS NOT NULL AND (OABEF.C_F_SYSTEM_HID IS NULL OR OAAFT.C_F_SYSTEM_HID <>  
OABEF.C_F_SYSTEM_HID))  
UNION ALL  
SELECT  
    OAAFT.C_F_SYSTEM_HID  depthid,--分体系待异动入 未来生效  
    EO.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EO.C_EMPTYPE,  
    EO.C_O15 o15  
FROM  
    TB_STA_EMP_TURNOVER TU,  
    TB_ORG_ORGUNITALL OABEF,  
    TB_ORG_ORGUNITALL OAAFT,  
    TB_STA_EMP E,  
    TB_STA_EMP_ORG EO,  
    TB_STA_EMP_CLASS TC  
WHERE  
        EO.C_TURNOVER_ID = TU.C_OID  
  AND E.C_OID = EO.C_EMPLOYEE_ID  
  AND EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
  AND EO.C_DEPT_TYPE = '1'  
  AND EO.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EO.C_END_DATE > TRUNC(SYSDATE)  
  AND EO.C_DEPT_TYPE = '1'  
  AND TU.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND TU.C_PREV_DEPT_HID = OABEF.C_HID  
  AND TU.C_NEW_DEPT_HID = OAAFT.C_HID  
  AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND (OAAFT.C_F_SYSTEM_HID IS NOT NULL AND (OABEF.C_F_SYSTEM_HID IS NULL OR OAAFT.C_F_SYSTEM_HID <>  
OABEF.C_F_SYSTEM_HID))  
) m  
GROUP BY m.depthid  
  
UNION  ALL  
  
SELECT  depthid,  
        nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
FROM  
    (  
        SELECT  
            OAAFT.C_DEPT_HID  depthid,--部门待异动入 在途  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE C_EMPTYPE,  
            EG.C_O15 o15  
        FROM TB_STA_INTRANSFERINFO YD,  
             TB_ORG_POSITION OPM,  
             TB_STA_EMP_ORG EG,  
             TB_ORG_ORGUNITALL OABEF,  
             TB_ORG_ORGUNITALL OAAFT,  
             TB_STA_EMP E,  
             TB_STA_EMP_CLASS TC  
        WHERE YD.C_PRE_UNITHID = OABEF.C_HID  
          AND YD.C_NEW_POSHID=OPM.C_HID  
          AND YD.C_NEW_UNITHID = OAAFT.C_HID  
          AND YD.C_EMPLOYEE_ID = E.C_OID  
          AND YD.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        AND EG.C_EMPLOYEE_ID = E.C_OID  
        AND EG.C_BEGIN_DATE <= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)  
        AND EG.C_DEPT_TYPE = '1'  
        and EG.C_EMPLOYEE_STATUS in('2','11')  
        AND (OAAFT.C_DEPT_HID IS NOT NULL AND (OABEF.C_DEPT_HID IS NULL OR OAAFT.C_DEPT_HID <>  
OABEF.C_DEPT_HID))  
UNION ALL  
SELECT  
    OAAFT.C_DEPT_HID  depthid,--部门待异动入 未来生效  
    EO.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EO.C_EMPTYPE,  
    EO.C_O15 o15  
FROM  
    TB_STA_EMP_TURNOVER TU,  
    TB_ORG_ORGUNITALL OABEF,  
    TB_ORG_ORGUNITALL OAAFT,  
    TB_STA_EMP E,  
    TB_STA_EMP_ORG EO,  
    TB_STA_EMP_CLASS TC  
WHERE  
        EO.C_TURNOVER_ID = TU.C_OID  
  AND E.C_OID = EO.C_EMPLOYEE_ID  
  AND EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
  AND EO.C_DEPT_TYPE = '1'  
  AND EO.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EO.C_END_DATE > TRUNC(SYSDATE)  
  AND EO.C_DEPT_TYPE = '1'  
  AND TU.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND TU.C_PREV_DEPT_HID = OABEF.C_HID  
  AND TU.C_NEW_DEPT_HID = OAAFT.C_HID  
  AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND (OAAFT.C_DEPT_HID IS NOT NULL AND (OABEF.C_DEPT_HID IS NULL OR OAAFT.C_DEPT_HID <>  
OABEF.C_DEPT_HID))  
) m  
GROUP BY m.depthid  
  
UNION  ALL  
  
SELECT  depthid,  
        nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
FROM  
    (  
        SELECT  
            OAAFT.C_SUBDEPT_HID  depthid,--副部待异动入 在途  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE C_EMPTYPE,  
            EG.C_O15 o15  
        FROM TB_STA_INTRANSFERINFO YD,  
             TB_ORG_POSITION OPM,  
             TB_STA_EMP_ORG EG,  
             TB_ORG_ORGUNITALL OABEF,  
             TB_ORG_ORGUNITALL OAAFT,  
             TB_STA_EMP E,  
             TB_STA_EMP_CLASS TC  
        WHERE YD.C_PRE_UNITHID = OABEF.C_HID  
          AND YD.C_NEW_POSHID=OPM.C_HID  
          AND YD.C_NEW_UNITHID = OAAFT.C_HID  
          AND YD.C_EMPLOYEE_ID = E.C_OID  
          AND YD.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        AND EG.C_EMPLOYEE_ID = E.C_OID  
        AND EG.C_BEGIN_DATE <= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)  
        AND EG.C_DEPT_TYPE = '1'  
        and EG.C_EMPLOYEE_STATUS in('2','11')  
        AND (OAAFT.C_SUBDEPT_HID IS NOT NULL AND (OABEF.C_SUBDEPT_HID IS NULL OR OAAFT.C_SUBDEPT_HID <>  
OABEF.C_SUBDEPT_HID))  
UNION ALL  
SELECT  
    OAAFT.C_SUBDEPT_HID  depthid,--副部待异动入 未来生效  
    EO.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EO.C_EMPTYPE,  
    EO.C_O15 o15  
FROM  
    TB_STA_EMP_TURNOVER TU,  
    TB_ORG_ORGUNITALL OABEF,  
    TB_ORG_ORGUNITALL OAAFT,  
    TB_STA_EMP E,  
    TB_STA_EMP_ORG EO,  
    TB_STA_EMP_CLASS TC  
WHERE  
        EO.C_TURNOVER_ID = TU.C_OID  
  AND E.C_OID = EO.C_EMPLOYEE_ID  
  AND EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
  AND EO.C_DEPT_TYPE = '1'  
  AND EO.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EO.C_END_DATE > TRUNC(SYSDATE)  
  AND EO.C_DEPT_TYPE = '1'  
  AND TU.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND TU.C_PREV_DEPT_HID = OABEF.C_HID  
  AND TU.C_NEW_DEPT_HID = OAAFT.C_HID  
  AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND (OAAFT.C_SUBDEPT_HID IS NOT NULL AND (OABEF.C_SUBDEPT_HID IS NULL OR OAAFT.C_SUBDEPT_HID <>  
OABEF.C_SUBDEPT_HID))  
) m  
GROUP BY m.depthid  
  
UNION  ALL  
  
SELECT  depthid,  
        nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odydCnt,  
        nvl(sum(CASE WHEN  m.C_EMPTYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
FROM  
    (  
        SELECT  
            OAAFT.C_GRUP_HID  depthid,--组科室待异动入 在途  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE C_EMPTYPE,  
            EG.C_O15 o15  
        FROM TB_STA_INTRANSFERINFO YD,  
             TB_ORG_POSITION OPM,  
             TB_STA_EMP_ORG EG,  
             TB_ORG_ORGUNITALL OABEF,  
             TB_ORG_ORGUNITALL OAAFT,  
             TB_STA_EMP E,  
             TB_STA_EMP_CLASS TC  
        WHERE YD.C_PRE_UNITHID = OABEF.C_HID  
          AND YD.C_NEW_POSHID=OPM.C_HID  
          AND YD.C_NEW_UNITHID = OAAFT.C_HID  
          AND YD.C_EMPLOYEE_ID = E.C_OID  
          AND YD.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        AND EG.C_EMPLOYEE_ID = E.C_OID  
        AND EG.C_BEGIN_DATE <= TRUNC(SYSDATE)  
        AND EG.C_END_DATE > TRUNC(SYSDATE)  
        AND EG.C_DEPT_TYPE = '1'  
        and EG.C_EMPLOYEE_STATUS in('2','11')  
        AND (OAAFT.C_GRUP_HID IS NOT NULL AND (OABEF.C_GRUP_HID IS NULL OR OAAFT.C_GRUP_HID <>  
OABEF.C_GRUP_HID))  
UNION ALL  
SELECT  
    OAAFT.C_GRUP_HID  depthid,--组科室待异动入 未来生效  
    EO.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EO.C_EMPTYPE,  
    EO.C_O15 o15  
FROM  
--     异动 主键 连接员工组织表的 异动id 员工组织连接 员工类型 异动前后的全组织  
    TB_STA_EMP_TURNOVER TU,  
    TB_ORG_ORGUNITALL OABEF,  
    TB_ORG_ORGUNITALL OAAFT,  
    TB_STA_EMP E,  
    TB_STA_EMP_ORG EO,  
    TB_STA_EMP_CLASS TC  
WHERE  
        EO.C_TURNOVER_ID = TU.C_OID  
  AND E.C_OID = EO.C_EMPLOYEE_ID  
  AND EO.C_EMPLOYEE_ID = TC.C_EMPLOYEE_ID  
  AND EO.C_DEPT_TYPE = '1'  
  AND EO.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EO.C_END_DATE > TRUNC(SYSDATE)  
  AND EO.C_DEPT_TYPE = '1'  
  AND TU.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND TU.C_PREV_DEPT_HID = OABEF.C_HID  
  AND TU.C_NEW_DEPT_HID = OAAFT.C_HID  
  AND TC.C_BEGIN_DATE <= TRUNC(SYSDATE) AND TC.C_END_DATE >= TRUNC(SYSDATE)  
        AND (OAAFT.C_GRUP_HID IS NOT NULL AND (OABEF.C_GRUP_HID IS NULL OR OAAFT.C_GRUP_HID <>  
OABEF.C_GRUP_HID))  
) m  
GROUP BY m.depthid  
  
  
  
-- 计划二部测试  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) and  m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_DEPT_HID  deptHid,--部门预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( JC.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        LEFT JOIN TB_ORG_JOB_CLASS JC ON H.C_POS_LEVEL = JC.C_OID AND JC.C_STATUS = '1'  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_DEPT_HID IS NOT NULL  
        UNION ALLSELECT  
    EOA.C_DEPTHID  depthid,--部门预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_DEPTHID  IS NOT NULL  
    ) m GROUP BY m.deptHid  
  
SELECT  
    deptHid,  
--     CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END odrzCnt111  
--     nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
--     nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
--     nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_DEPT_HID  deptHid,--部门预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            JC.C_NAME,  
            OPM.C_POST_LEVEL,  
            OPM.C_NAME,  
            CASE WHEN ( JC.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
                LEFT JOIN TB_ORG_JOB_CLASS JC ON H.C_POS_LEVEL = JC.C_OID AND JC.C_STATUS = '1'  
        WHERE OAL.C_HID = H.C_DEPT_HID  
          AND H.C_STATUS = 'preparing'  
          AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
          AND OAL.C_HID = U.C_HID  
          AND U.C_STATUS = '1'  
          AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
          AND U.C_END_DATE >=  TRUNC(SYSDATE)  
          AND OAL.C_DEPT_HID IS NOT NULL  
        ORDER BY  o15 DESC  
        and(( JC.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长')  
  
--         and OPM.C_NAME LIKE '%班组长'  
--           and OAL.C_DEPT_HID = '34ff4dea4e8e4ef9858069b925d27640'  
--         and OPM.C_EMP_TYPE = '4' AND (H.C_YONGGONGXINGSHI = '1' OR H.C_ZHAOPINQUDAO = '100006003')  
            UNION ALL  
        SELECT            EOA.C_DEPTHID  depthid,--部门预入职未来生效  
            EG.C_EMPLOYEE_TYPE ,  
            TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
            E.C_ZHAOPINQUDAO,  
            EG.C_EMPTYPE,  
            E.C_CODE,  
            EG.C_O15  o15  
        FROM  
            TB_STA_EMP E,  
            TB_STA_EMP_CLASS TC,  
            TB_STA_EMP_ORG EG,  
            TB_STA_EMP_ORGAPPEND EOA  
        WHERE E.C_OID=EG.C_EMPLOYEE_ID  
          AND EG.c_end_date > TRUNC(SYSDATE)  
          AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
          AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
          AND EG.C_DEPT_TYPE ='1'  
          AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
          AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
          AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
          AND EOA.C_OID = EG.C_OID  
          AND EOA.C_DEPTHID  IS NOT NULL  
        and EG.C_DEPT_HID = 'd5026c008b03439ebe01aa23378bb686'  
    ) m  
where m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003'  
  
  
  
  
  
  
  
  
  
  
  
-- 预入职测试  
SELECT  
    DISTINCT deptHid,  
             nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
             nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
             nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_COMPANY_HID  deptHid, --公司预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
--             CASE WHEN ( OPM.C_CLASS_ID IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
--             CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
            CASE WHEN ( jc.C_NAME IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
--          预入职表  里的部门id link  组织单元表 全组织表  岗位id link 左连接岗位表  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
                LEFT JOIN TB_ORG_JOB_CLASS jc on H.C_POS_LEVEL = jc.C_OID  
        WHERE OAL.C_HID = H.C_DEPT_HID --最小组织为部门 连接 预入职表的部门  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
          and jc.C_STATUS = '1'  
        UNION ALL  
SELECT  
    EG.C_COMPANY_HID depthid, ---公司预入职 未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
    ) m GROUP BY m.deptHid  
  
UNION ALL  
  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_CENTER_HID  deptHid,--中心预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_CENTER_HID IS NOT NULL  
        UNION ALLSELECT  
    EOA.C_CENTHID  depthid,--中心预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_CENTHID IS NOT NULL  
    ) m GROUP BY m.deptHid  
  
UNION ALL  
  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_SYSTEM_HID  deptHid,--体系预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_SYSTEM_HID IS NOT NULL  
        UNION ALLSELECT  
    EOA.C_SYSTHID  depthid,--体系预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_SYSTHID IS NOT NULL  
    ) m GROUP BY m.deptHid  
  
UNION ALL  
  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_F_SYSTEM_HID  deptHid,--分体系预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_F_SYSTEM_HID IS NOT NULL  
        UNION ALLSELECT  
    EOA.C_F_SYSTEM_ID  depthid,--分体系预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_F_SYSTEM_ID  IS NOT NULL  
    ) m GROUP BY m.deptHid  
  
UNION ALL  
  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_DEPT_HID  deptHid,--部门预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_DEPT_HID IS NOT NULL  
        UNION ALLSELECT  
    EOA.C_DEPTHID  depthid,--部门预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_DEPTHID  IS NOT NULL  
    ) m GROUP BY m.deptHid  
  
UNION ALL  
  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_SUBDEPT_HID  deptHid,--副部预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_SUBDEPT_HID IS NOT NULL  
        UNION ALLSELECT  
    EOA.C_SUBDEPTHID  depthid,--副部预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_SUBDEPTHID  IS NOT NULL  
    ) m GROUP BY m.deptHid  
  
UNION ALL  
  
SELECT  
    deptHid,  
    nvl(sum(CASE WHEN  m.C_EMPLOYEE_TYPE = '0'  AND m.C_YONGGONGXINGSHI in('1','5')  THEN 1 ELSE 0 END),0) zydrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003' ) THEN 1 ELSE 0 END),0) odrzCnt,  
    nvl(sum(CASE WHEN  m.C_EMP_TYPE = '4' AND (m.C_YONGGONGXINGSHI = '1' OR m.C_ZHAOPINQUDAO = '100006003') AND m.o15 = '1' THEN 1 ELSE 0 END),0) o15OnjobCnt  
from(  
        SELECT  
            OAL.C_GRUP_HID  deptHid,--组科室预入职在途  
            H.C_EMPLOYEE_TYPE ,  
            H.C_YONGGONGXINGSHI,  
            H.C_ZHAOPINQUDAO,  
            OPM.C_EMP_TYPE,  
            H.C_CODE,  
            CASE WHEN ( H.C_POS_LEVEL IN ('O7', 'O8', 'O9') AND OPM.C_POST_LEVEL IN ('B3','B4') ) OR OPM.C_NAME LIKE '%班组长' THEN '1' ELSE '0' END o15  
        FROM  
            TB_ORG_ORGUNIT U,  
            TB_ORG_ORGUNITALL OAL,  
            TB_STA_PREPARE_HIRE H  
                LEFT JOIN TB_ORG_POSITION OPM ON H.C_POSITION_ID = OPM.C_HID AND OPM.C_BEGIN_DATE <= TRUNC( CURRENT_DATE)  AND  OPM.C_END_DATE > TRUNC(CURRENT_DATE)  
        WHERE OAL.C_HID = H.C_DEPT_HID  
        AND H.C_STATUS = 'preparing'  
        AND OAL.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
        AND OAL.C_HID = U.C_HID  
        AND U.C_STATUS = '1'  
        AND U.C_BEGIN_DATE <=  TRUNC(SYSDATE)  
        AND U.C_END_DATE >=  TRUNC(SYSDATE)  
        AND OAL.C_GRUP_HID IS NOT NULL  
        UNION ALLSELECT  
    EOA.C_GRUPHID  depthid,--组科室预入职未来生效  
    EG.C_EMPLOYEE_TYPE ,  
    TC.C_LABOR_TYPE C_YONGGONGXINGSHI,  
    E.C_ZHAOPINQUDAO,  
    EG.C_EMPTYPE,  
    E.C_CODE,  
    CASE WHEN EG.C_O15 = '1' THEN '1' ELSE '0' END o15  
FROM  
    TB_STA_EMP E,  
    TB_STA_EMP_CLASS TC,  
    TB_STA_EMP_ORG EG,  
    TB_STA_EMP_ORGAPPEND EOA  
WHERE E.C_OID=EG.C_EMPLOYEE_ID  
  AND EG.c_end_date > TRUNC(SYSDATE)  
  AND EG.C_BEGIN_DATE > TRUNC(SYSDATE)  
  AND EG.C_EMPLOYEE_STATUS IN ('2', '11')  
  AND EG.C_DEPT_TYPE ='1'  
  AND EG.C_COMPANY_HID = '6042d4ad0d654ddf819926f023b4d968'  
  AND TC.C_EMPLOYEE_ID = EG.C_EMPLOYEE_ID  
  AND TC.C_BEGIN_DATE > TRUNC(SYSDATE) AND TC.C_END_DATE > TRUNC(SYSDATE)  
  AND EOA.C_OID = EG.C_OID  
  AND EOA.C_GRUPHID  IS NOT NULL  
    ) m GROUP BY m.deptHid
```



# 根据日期执行考勤中间表 连续矿工两天 4小时

```
mybatis/manager/AttendanceMidMapper.xml
```

```sql
<!--先分组：按 C_EMPLOYEE_ID 将数据分成若干组。  
组内排序：每组按 C_DATE 从新到旧（DESC）排序。  
分配行号：每组内从1开始依次编号。-->
SELECT ROW_NUMBER() OVER(PARTITION BY DS.C_EMPLOYEE_ID ORDER BY DS.C_DATE DESC) NUM,  
DS.C_DATE,  
DS.C_EMPLOYEE_ID  
FROM TB_TMG_DAILYSUMMARY DS, TB_TMG_SHIFT SHIFT  
WHERE DS.C_SHIFT_ID = SHIFT.C_OID  
AND SHIFT.C_RESTSHIFT = '0'  
AND DS.C_DATE >= trunc(add_months(last_day(to_date(#{date},'yyyy-mm-dd')), -1) + 1) --本月第一天  
AND DS.C_DATE &lt;= to_date(#{date},'yyyy-mm-dd')
```
![](assets/Pasted%20image%2020250729120847.png)

```sql
<!--直接插入-->  
<insert id="insertMid" useGeneratedKeys="false">  
    INSERT INTO TB_SELF_MID_ATTENDANCE (<include refid="baseSqlMid"/>)  
    (    SELECT        LOWER(SYS_GUID()),        'ADMIN',        SYSDATE,        DS.C_EMPLOYEE_ID,        DS.C_DATE,        DS.C_ATTENDANCE_TIMES * 60,        DS.C_EARLY_TIMES,        DS.C_LATE_TIMES,        DS.C_WORKING_OVERTIME,        DS.C_WEEKEND_OVERTIME,        DS.C_HOLIDAYS_OVERTIME,        DS.C_NIGHTSHIFTS_NUMBER,        DS.C_LATENUMS,        DS.C_EARLYNUMS,        DS.C_ITEMNAMES,        DS.C_VACATION_TIMES,        LXKG.BEGIN_DATE,        LXKG.NUM,        MON.actual,        MON.work_over,        MON.weekend_over,        MON.holiday_over,        DS.C_SHIFT_ID,        '1'    FROM TB_TMG_DAILYSUMMARY DS    LEFT JOIN            (SELECT E.C_OID          EMPLOYEEID,                KONG.NUM         NUM,                KONG.C_DATE      BEGIN_DATE                FROM TB_TMG_MEMBER M, TB_STA_EMP E,                    (SELECT M.NUM, M.C_EMPLOYEE_ID, M.C_DATE                    FROM  
                        (SELECT ROW_NUMBER() OVER(PARTITION BY KG.C_EMPLOYEE_ID ORDER BY KG.C_DATE) DESCNUM,                        KG.NUM,                        KG.C_EMPLOYEE_ID,                        KG.C_DATE                        FROM                            <!--找到某个员工某天一天旷工时间大于4小时的记录-->  
                            (SELECT ROW_NUMBER() OVER(PARTITION BY AE.C_EMPLOYEE_ID ORDER BY AE.C_DATE DESC) NUM,  
                            AE.C_EMPLOYEE_ID,                            AE.C_DATE                            FROM                                <!--员工id 考勤事件发生日期 累计旷工时长  从月初到指定时间  -->  
                                (SELECT TD.C_EMPLOYEE_ID C_EMPLOYEE_ID,  
                                TD.C_DATE C_DATE,                                SUM(CASE WHEN TD.C_RESULT_TYPE = '5' THEN TD.C_PERIOD ELSE 0 END) / 60 C_ABSENTEEISMTIMES                                FROM TB_TMG_DAILYHANDLERESULT TD,                                TB_TMG_MEMBER            M                                WHERE M.C_EMPLOYEE_ID = TD.C_EMPLOYEE_ID                                AND M.C_EFFECTIVE_DATE_BEGIN &lt;= SYSDATE  
                                AND M.C_EFFECTIVE_DATE_END >= SYSDATE                                AND TD.C_DATE >= trunc(add_months(last_day(to_date(#{date},'yyyy-mm-dd')), -1) + 1) --本月第一天  
                                AND TD.C_DATE &lt;= to_date(#{date},'yyyy-mm-dd')  
                                GROUP BY TD.C_EMPLOYEE_ID, TD.C_DATE) AE  
                            WHERE AE.C_ABSENTEEISMTIMES > 4) KG,  
                            <!--先分组：按 C_EMPLOYEE_ID 将数据分成若干组。  
                            组内排序：每组按 C_DATE 从新到旧（DESC）排序。  
                            分配行号：每组内从1开始依次编号。-->  
                            (SELECT ROW_NUMBER() OVER(PARTITION BY DS.C_EMPLOYEE_ID ORDER BY DS.C_DATE DESC) NUM,  
                            DS.C_DATE,                            DS.C_EMPLOYEE_ID                            FROM TB_TMG_DAILYSUMMARY DS, TB_TMG_SHIFT SHIFT                            WHERE DS.C_SHIFT_ID = SHIFT.C_OID                            AND SHIFT.C_RESTSHIFT = '0'  <!--不是休息班-->  
                            AND DS.C_DATE >= trunc(add_months(last_day(to_date(#{date},'yyyy-mm-dd')), -1) + 1) --本月第一天  
                            AND DS.C_DATE &lt;= to_date(#{date},'yyyy-mm-dd')) ZB  
  
                        WHERE KG.NUM = ZB.NUM                        AND KG.C_EMPLOYEE_ID = ZB.C_EMPLOYEE_ID                        AND KG.C_DATE = ZB.C_DATE                        AND KG.NUM >= 2  <!--作用是找到连续矿工两天？不对，是矿工两天，不对，不对，就是矿工两天矿工大于等于两天--> ) M  
  
  
                    WHERE M.DESCNUM = 1) KONG                WHERE M.C_EMPLOYEE_ID = E.C_OID                AND M.C_EFFECTIVE_DATE_BEGIN &lt;= SYSDATE  
                AND M.C_EFFECTIVE_DATE_END >= SYSDATE                AND KONG.C_EMPLOYEE_ID = E.C_OID                GROUP BY E.C_OID, KONG.NUM, KONG.C_DATE            ) LXKG ON DS.C_EMPLOYEE_ID = LXKG.EMPLOYEEID        --连续旷工2天以上的  
    LEFT JOIN  
        (SELECT S.C_EMPLOYEE_ID,            sum(S.C_ATTENDANCE_TIMES) * 60 actual,                                                      --实际出勤时数  
            sum(S.C_WORKING_OVERTIME) work_over,                                                      --平日加班  
            sum(S.C_WEEKEND_OVERTIME) weekend_over,                                                      --休息日加班  
            sum(S.C_HOLIDAYS_OVERTIME) holiday_over,                                                     --节假日加班  
            sum(S.C_WORKING_OVERTIME) + sum(S.C_WEEKEND_OVERTIME) + sum(S.C_HOLIDAYS_OVERTIME)    total_over, --加班合计  
            sum(S.C_ATTENDANCE_TIMES) * 60 + sum(S.C_WORKING_OVERTIME) + sum(S.C_WEEKEND_OVERTIME) + sum(S.C_HOLIDAYS_OVERTIME)   total_time      --总工时  
            FROM TB_TMG_DAILYSUMMARY S  
            WHERE S.C_DATE >= trunc(add_months(last_day(to_date(#{date},'yyyy-mm-dd')), -1) + 1) --所选日期的当月第一天  
                AND S.C_DATE &lt;= to_date(#{date},'yyyy-mm-dd')  
            group by S.C_EMPLOYEE_ID        ) MON ON DS.C_EMPLOYEE_ID = MON.C_EMPLOYEE_ID    WHERE DS.C_DATE = to_date(#{date},'yyyy-mm-dd')    )</insert>
```

## WHERE KG.NUM = ZB.NUM能看出来这个作用是什么吗
```sql
(SELECT ROW_NUMBER() OVER(PARTITION BY KG.C_EMPLOYEE_ID ORDER BY KG.C_DATE) DESCNUM,  
KG.NUM,  
KG.C_EMPLOYEE_ID,  
KG.C_DATE  
FROM  
    <!--找到某个员工某天一天旷工时间大于4小时的记录-->  
    (SELECT ROW_NUMBER() OVER(PARTITION BY AE.C_EMPLOYEE_ID ORDER BY AE.C_DATE DESC) NUM,  
    AE.C_EMPLOYEE_ID,    AE.C_DATE    FROM        <!--员工id 考勤事件发生日期 累计旷工时长  从月初到指定时间  -->  
        (SELECT TD.C_EMPLOYEE_ID C_EMPLOYEE_ID,  
        TD.C_DATE C_DATE,        SUM(CASE WHEN TD.C_RESULT_TYPE = '5' THEN TD.C_PERIOD ELSE 0 END) / 60 C_ABSENTEEISMTIMES        FROM TB_TMG_DAILYHANDLERESULT TD,        TB_TMG_MEMBER            M        WHERE M.C_EMPLOYEE_ID = TD.C_EMPLOYEE_ID        AND M.C_EFFECTIVE_DATE_BEGIN &lt;= SYSDATE  
        AND M.C_EFFECTIVE_DATE_END >= SYSDATE        AND TD.C_DATE >= trunc(add_months(last_day(to_date(#{date},'yyyy-mm-dd')), -1) + 1) --本月第一天  
        AND TD.C_DATE &lt;= to_date(#{date},'yyyy-mm-dd')  
        GROUP BY TD.C_EMPLOYEE_ID, TD.C_DATE) AE  
    WHERE AE.C_ABSENTEEISMTIMES > 4) KG,  
    <!--先分组：按 C_EMPLOYEE_ID 将数据分成若干组。  
    组内排序：每组按 C_DATE 从新到旧（DESC）排序。  
    分配行号：每组内从1开始依次编号。-->  
    (SELECT ROW_NUMBER() OVER(PARTITION BY DS.C_EMPLOYEE_ID ORDER BY DS.C_DATE DESC) NUM,  
    DS.C_DATE,    DS.C_EMPLOYEE_ID    FROM TB_TMG_DAILYSUMMARY DS, TB_TMG_SHIFT SHIFT    WHERE DS.C_SHIFT_ID = SHIFT.C_OID    AND SHIFT.C_RESTSHIFT = '0'  <!--不是休息班-->  
    AND DS.C_DATE >= trunc(add_months(last_day(to_date(#{date},'yyyy-mm-dd')), -1) + 1) --本月第一天  
    AND DS.C_DATE &lt;= to_date(#{date},'yyyy-mm-dd')) ZB  
  
WHERE KG.NUM = ZB.NUM  
AND KG.C_EMPLOYEE_ID = ZB.C_EMPLOYEE_ID  
AND KG.C_DATE = ZB.C_DATE  
AND KG.NUM >= 2  <!--作用是找到连续矿工两天？不对，是矿工两天，不对，不对，就是矿工两天矿工大于等于两天--> )
```

经过研究，效果就是，本来应该工作的相邻的两天（这里的相邻包括周五和周一，假设双休），如果两天都旷工4小时就会被查出来这个数据

您指出了一个关键矛盾点，我来更精确地解释这个反例的问题所在，并澄清之前的表述中不够准确的部分。

---

### **更准确的反例说明**
#### 原始连接条件实际应为：
```sql
WHERE KG.NUM = ZB.NUM                  -- 行号匹配
  AND KG.C_EMPLOYEE_ID = ZB.C_EMPLOYEE_ID  -- 员工ID匹配
  AND KG.C_DATE = ZB.C_DATE                -- 日期匹配
  AND KG.NUM >= 2                          -- 连续检测
```

#### **真正的问题场景**
当 **子查询的`ROW_NUMBER()`生成逻辑不一致** 时，即使有 `C_DATE` 匹配条件，仍可能出现错误关联。以下是具体案例：

---

### **反例数据（子查询编号不一致）**
#### 员工1001的原始数据
| 日期       | 旷工时长 | 正常考勤 |
|------------|----------|----------|
| 2023-08-15 | 6小时    | 异常     |
| 2023-08-14 | 0小时    | 正常     |
| 2023-08-13 | 5小时    | 异常     |

#### 子查询结果
1. **KG子查询**（旷工记录，按日期**降序**编号）：
   | NUM | 日期       | 员工ID | 旷工时长 |
   |-----|------------|--------|----------|
   | 1   | 2023-08-15 | 1001   | 6小时    |
   | 2   | 2023-08-13 | 1001   | 5小时    |  
   *（2023-08-14无旷工，未出现在KG结果中）*

2. **ZB子查询**（正常考勤，按日期**降序**编号）：
   | NUM | 日期       | 员工ID | 考勤状态 |
   |-----|------------|--------|----------|
   | 1   | 2023-08-15 | 1001   | 异常     |
   | 2   | 2023-08-14 | 1001   | 正常     |
   | 3   | 2023-08-13 | 1001   | 异常     |

---

### **不加 `KG.NUM = ZB.NUM` 时的错误关联**
如果仅用以下条件：
```sql
WHERE KG.C_EMPLOYEE_ID = ZB.C_EMPLOYEE_ID
  AND KG.C_DATE = ZB.C_DATE
  AND KG.NUM >= 2
```
**匹配过程**：
1. KG中的记录 `(NUM=2, 2023-08-13)` 尝试匹配ZB：
   - 在ZB中查找 `日期=2023-08-13` 的记录 → 找到 `(NUM=3, 2023-08-13)`
   - **但因未要求NUM相等**，系统仍会返回这条记录

2. 最终关联结果：
   | KG.NUM | KG.日期   | ZB.NUM | ZB.日期   | 考勤状态 |
   |--------|-----------|--------|-----------|----------|
   | 2      | 2023-08-13| 3      | 2023-08-13| 异常     |

**错误结论**：  
虽然日期匹配了，但：
- **NUM不对齐**（KG.NUM=2 vs ZB.NUM=3）  
- 导致后续逻辑误认为 `NUM=2` 对应的是连续旷工的第二条记录（实际应关联NUM=2的考勤记录，但那天无旷工）

---

### **为什么需要 `NUM` 匹配？**
1. **业务逻辑要求**  
   需要检测的是 **按时间倒序排列后相邻日期的旷工连续性**。NUM代表时间序列中的位置序号，必须严格对齐。

2. **数据完整性**  
   KG和ZB两个子查询的数据范围可能不同（如ZB包含所有日期，KG只含旷工日期），仅用日期匹配会导致：
   - 漏检（如KG.NUM=2对应日期在ZB中NUM≠2）
   - 误检（如本例中匹配到非相邻日期的记录）

---

### **正确的关联逻辑**
```sql
WHERE KG.NUM = ZB.NUM                      -- 确保时间序列位置一致
  AND KG.C_EMPLOYEE_ID = ZB.C_EMPLOYEE_ID  -- 同一员工
  AND KG.C_DATE = ZB.C_DATE                -- 同一日期（双重校验）
  AND KG.NUM >= 2                          -- 连续检测
```
**此时**：
- KG中的 `(NUM=2, 2023-08-13)` 只会匹配 ZB中 `NUM=2` 的记录（即2023-08-14）
- 但由于日期不匹配（2023-08-13 ≠ 2023-08-14），**这条记录会被过滤掉**
- 最终正确结果：无连续旷工

---

### **总结**
| 条件 | 作用 | 反例中缺失的后果 |
|------|------|------------------|
| `KG.NUM = ZB.NUM` | 保证时间序列位置对齐 | 错误关联非相邻日期的记录 |
| `KG.C_DATE = ZB.C_DATE` | 二次校验日期一致性 | 可能匹配到正确日期但错误时序位置的数据 |
| 二者共存 | **精确锁定同一时序位置的同一日期记录** | 业务逻辑崩溃 |

这种设计是典型的 **时序数据双保险校验**，在金融、考勤等严谨场景中尤为常见。


# 班次 班段 考勤日报

```
/getKqSummary
```
包含 应出勤 实际出勤，加班等信息
整体就是 先查 实际出勤 应出勤等信息，
然后找各种考勤项目（包含各种假别 ，各种出差等）
最后带薪假需要前面查到的汇总（所有考勤项目） 减去 后面单独查出来的 周末出差等

粘贴至 数据库软件 datagrip中使用

```
  
select optDate,  
       yingchu,  
       shiji,  
       workingovertime,  
       weekendovertime,  
       holidaysovertime,  
       kuanggong,  
       qingjia,  
       earlyandlatemin,  
       earlyandlateminZzo,  
       earlyandlatenum,  
       cardnums  
from (select optDate,  
             sum(yingchu)                                  as yingchu,  
             sum(attendanceTime)                           as shiji,  
             sum(workingovertime)                          as workingovertime,  
             sum(weekendovertime)                          as weekendovertime,  
             sum(holidaysovertime)                         as holidaysovertime,  
             trunc(sum(absenteeism) / 60, 2)               as kuanggong,  
             sum(vacationTime)                             as qingjia,  
             trunc(sum(earlyAndLateTimes) / 60, 2)         as earlyandlatemin,  
             CASE  
                 WHEN ((sum(earlyAndLateNum) - 3) * 0.5) < 0 THEN 0  
                 ELSE (sum(earlyAndLateNum) - 3) * 0.5 END AS earlyandlateminZzo,--本月迟到/早退时长 制造O升级逻辑调整为 （本月迟到次数+本月早退次数-3）*0.5，不可为负数，最小为0  
             sum(earlyAndLateNum)                          as earlyandlatenum,  
             sum(cardNum)                                  as cardnums  
      from (select to_char(DS.c_date, 'yyyy-MM')      as optDate,  
                   case  
                       when S1.HOURSBYSHIFT is null then  
                           0  
                       else  
                           S1.HOURSBYSHIFT  
                       end                            as yingchu,  --原来这个就是 根据班次班段来定义得到的 应出勤时数  
                   DS.C_ATTENDANCE_TIMES              as attendanceTime,  -- 考勤分析日报里的 出勤时间，工作加班时间等等，其实就是也是个汇总好的数据表  
                   DS.C_WORKING_OVERTIME              as workingovertime, -- 加班时间  
                   DS.C_WEEKEND_OVERTIME              as weekendovertime,  -- 周末加班时间  
                   DS.C_HOLIDAYS_OVERTIME             as holidaysovertime,  --节假日加班  
                   DS.C_ABSENTEEISM                   as absenteeism, --  旷工缺勤  
                   DS.C_VACATION_TIMES                as vacationTime, -- 假期时间  
                   DS.C_EARLY_TIMES + DS.C_LATE_TIMES as earlyAndLateTimes, -- 早退和迟到时间  
                   DS.C_LATENUMS + DS.C_EARLYNUMS     as earlyAndLateNum, -- 迟到和早退时数？  
                   DS.C_CARD_NUMBER                   as cardNum -- 卡的数量  
            from TB_TMG_DAILYSUMMARY DS  
                     LEFT JOIN (SELECT S.C_OID  ID,  
                                       SUM(CASE  
                                               WHEN (TO_DATE(SP.C_END_TIME, 'hh24:mi') >  
                                                     TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) THEN  
                                                       (TO_DATE(SP.C_END_TIME, 'hh24:mi') -  
                                                        TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) * 24  
                                               WHEN (TO_DATE(SP.C_END_TIME, 'hh24:mi') <  
                                                     TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) THEN  
                                                           (TO_DATE(SP.C_END_TIME, 'hh24:mi') -  
                                                            TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) * 24 + 24  
                                           END) HOURSBYSHIFT  
                                FROM TB_TMG_SHIFT S,  
                                     TB_TMG_SHIFT_PERIOD SP  
                                WHERE S.C_RESTSHIFT = '0' -- 是否休息班   否  
                                  AND SP.C_SHIFT_ID = S.C_OID  
                                  AND (SP.C_IFOVERTIMEPERIOD = '0' OR  SP.C_IFOVERTIMEPERIOD IS NULL)   -- 不是加班时间短  
                                GROUP BY S.C_OID) S1  
                               ON S1.ID = DS.C_SHIFT_ID   -- 不是加班时间段 不是休息班的 shift 的主键 = ds的shiftid  
            where DS.c_Employee_Id = '#{userId}'  -- 限定查询某个人的考勤数据列表 每个项包含上述数据  （这里查到的应该是这个人所有的考勤日报  嗯对，就是日报，但是optdata记录的是不是哪天，是哪个月，仅表示哪月内的日报  
            ) b  
      group by optDate) a  -- 再根据考勤日期进行分组 上面得出数据的时候再进行 求和  哦哦， 所以这里求的就是类似于月报的里的相关数据  这个月一共应该出勤多少，之类的  
where optDate = '#{date}'   -- 这里应该就是直接上面先抄现有代码，然后这里直接加上 限定条件，应该不会影响查询性能，不一定，不知道oracl底层会不会优化，mysql会提前根据参数来进行内部过滤的，忘记叫啥了  
order by optDate desc;  
  
  
-- 1个班次对应多个班段  
select *  
from TB_TMG_SHIFT s,TB_TMG_SHIFT_PERIOD sp  
where s.C_OID = sp.C_SHIFT_ID;  
  
  
 -- 限定查询某个人的考勤数据列表 每个项包含上述数据  （这里查到的应该是这个人所有的考勤日报  嗯对，就是日报，但是optdata记录的是不是哪天，是哪个月，仅表示哪月内的日报  
select  
        DS.c_Employee_Id  ,  
    to_char(DS.c_date, 'yyyy-MM')      as optDate,  
                   case  
                       when S1.HOURSBYSHIFT is null then  
                           0  
                       else  
                           S1.HOURSBYSHIFT  
                       end                            as yingchu,  --原来这个就是 根据班次班段来定义得到的 应出勤时数  
                   DS.C_ATTENDANCE_TIMES              as attendanceTime,  -- 考勤分析日报里的 出勤时间，工作加班时间等等，其实就是也是个汇总好的数据表  
                   DS.C_WORKING_OVERTIME              as workingovertime, -- 加班时间  
                   DS.C_WEEKEND_OVERTIME              as weekendovertime,  -- 周末加班时间  
                   DS.C_HOLIDAYS_OVERTIME             as holidaysovertime,  --节假日加班  
                   DS.C_ABSENTEEISM                   as absenteeism, --  旷工缺勤  
                   DS.C_VACATION_TIMES                as vacationTime, -- 假期时间  
                   DS.C_EARLY_TIMES + DS.C_LATE_TIMES as earlyAndLateTimes, -- 早退和迟到时间  
                   DS.C_LATENUMS + DS.C_EARLYNUMS     as earlyAndLateNum, -- 迟到和早退时数？  
                   DS.C_CARD_NUMBER                   as cardNum -- 卡的数量  
            from TB_TMG_DAILYSUMMARY DS  
                     LEFT JOIN (SELECT S.C_OID  ID,  
                                       SUM(CASE  
                                               WHEN (TO_DATE(SP.C_END_TIME, 'hh24:mi') >  
                                                     TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) THEN  
                                                       (TO_DATE(SP.C_END_TIME, 'hh24:mi') -  
                                                        TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) * 24  
                                               WHEN (TO_DATE(SP.C_END_TIME, 'hh24:mi') <  
                                                     TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) THEN  
                                                           (TO_DATE(SP.C_END_TIME, 'hh24:mi') -  
                                                            TO_DATE(SP.C_BEGIND_TIME, 'hh24:mi')) * 24 + 24  
                                           END) HOURSBYSHIFT  
                                FROM TB_TMG_SHIFT S,  
                                     TB_TMG_SHIFT_PERIOD SP  
                                WHERE S.C_RESTSHIFT = '0' -- 是否休息班   否  
                                  AND SP.C_SHIFT_ID = S.C_OID  
                                  AND (SP.C_IFOVERTIMEPERIOD = '0' OR  SP.C_IFOVERTIMEPERIOD IS NULL)   -- 不是加班时间短  
                                GROUP BY S.C_OID) S1  
                               ON S1.ID = DS.C_SHIFT_ID   -- 不是加班时间段 不是休息班的 shift 的主键 = ds的shiftid
```


```
  
-- 非o类 比 o类 区别就是 带薪假多个 x02 x26  
SELECT NVL(sum(case when a.code = 'X01' then times end), 0)           as personalLeave,  --事假  
       NVL(sum(case when a.code = 'X18' then times end), 0)           as kuanggongLeave, --旷工假  
       NVL(sum(case when a.code = 'X03' then times end), 0)           as sickLeave,      --病假  
       NVL(sum(case when a.code in ('X02', 'X26') then times end), 0) as absenceLeave,   --缺勤假/缺勤说明（制造O属于无薪假，集团职员+制造职员属于有薪假  非制造O无此假别）  
       NVL(sum(case when a.code = 'X05' then times * 8 end), 0)       as maternityLeave, --产假  
       NVL(sum(case when a.code IN ('X04', 'X05', 'X06', 'X07', 'X08', 'X11', 'X30') then times * 8 end), 0) +  
       NVL(sum(case  
                   when a.code IN  
                        ('X02', 'X10', 'X12', 'X13', 'X14', 'X15', 'X16', 'X19', 'X20', 'X21', 'X24', 'X25', 'X26',  
                         'X29') then times end), 0)                   as paidLeave       -- 带薪假  
FROM (select to_char(a.c_date, 'YYYY-MM-DD') as optdate,  
             a.c_period                      as times,  
             b.c_code                        as code,  
             b.c_name                           name,  
             b.c_oid                         as itemId  
      from TB_TMG_DAILYHANDLERESULT a  
               left join TB_TMG_ATTENDANCE_ITEM b on a.c_attendanceitem = b.c_oid and b.c_status = 1  
      where a.c_employee_id = '#{userId}'  
        and to_char(a.c_date, 'YYYY-MM') = '#{date}'  
        and a.c_attendanceitem is not null) a; -- 这个a应该是找的是 这个人的某个日期（某个月）下的 所有有效 考勤项目的 分析结果 就是 平常加班 这个考勤项目 这个月的所有记录结果 在上面进行sum  
  
  
SELECT NVL(sum(case when a.code = 'X01' then times end), 0)           as personalLeave,  --事假  
       NVL(sum(case when a.code = 'X18' then times end), 0)           as kuanggongLeave, --旷工假  
       NVL(sum(case when a.code = 'X03' then times end), 0)           as sickLeave,      --病假  
       NVL(sum(case when a.code in ('X02', 'X26') then times end), 0) as absenceLeave,   --缺勤假/缺勤说明（制造O属于无薪假，集团职员+制造职员属于有薪假  非制造O无此假别）  
       NVL(sum(case when a.code = 'X05' then times * 8 end), 0)       as maternityLeave, --产假  
       NVL(sum(case when a.code IN ('X04', 'X05', 'X06', 'X07', 'X08', 'X11', 'X30') then times * 8 end), 0) + NVL(  
               sum(case  
                       when a.code IN  
                            ('X10', 'X12', 'X13', 'X14', 'X15', 'X16', 'X19', 'X20', 'X21', 'X24', 'X25', 'X29')  
                           then times end), 0)                        as paidLeave  
FROM (select to_char(a.c_date, 'YYYY-MM-DD') as optdate,  
             a.c_period                      as times,  
             b.c_code                        as code,  
             b.c_name                           name,  
             b.c_oid                         as itemId  
      from TB_TMG_DAILYHANDLERESULT a  
               left join TB_TMG_ATTENDANCE_ITEM b on a.c_attendanceitem = b.c_oid and b.c_status = 1  
      where a.c_employee_id = '#{userId}'  
        and to_char(a.c_date, 'YYYY-MM') = '#{date}'  
        and a.c_attendanceitem is not null) a  
  
  
  
SELECT NVL(SUM(RS.C_ORIGINALPERIOD),0) AS num FROM TB_TMG_DAILYHANDLERESULT RS,TB_TMG_SHIFT S  
        WHERE  
            RS.C_EMPLOYEE_ID ='#{userId}'  
        AND to_char(RS.C_DATE, 'yyyy-MM')>='#{date}'  
        and to_char(RS.C_DATE, 'yyyy-MM')<='#{date}'  AND S.C_OID=RS.C_SHIFT AND  
        (C_ATTENDANCEITEM='85f2a27da8c0458e94564bab752d083e' or  
        C_ATTENDANCEITEM='772d24cf01e64201b97c39993fece9ab') AND  S.C_RESTSHIFT=1
```

# end
