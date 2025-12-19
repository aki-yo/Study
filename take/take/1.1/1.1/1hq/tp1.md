
部门/体系平均延时出勤数
体系平均延时出勤
月度延迟出勤明细
月度延时超100 
数据来源：
TB_TMG_EMPOVERTIMEDETAIL

市外出差
月度出差天数超过20
数据来源：
TB_TMG_OUTCITYTRAVEL

考勤异常人员明细
TB_TMG_ABNORMAL

周度延时出勤
TB_TMG_WEEKOVERTIMEDETAIL

有薪假库存
TB_TMG_LEAVE_INVENTORY

连续7天未休息 日常管理（O类员工）
月度累计工时<250明细
月度累计工时汇总 日常管理O类员工
TB_TMG_DAILYSUMMARY

当天缺勤明细
连续旷工两天明细
TB_TMG_DAILYSUMMARY
TB_TMG_DAILYHANDLERESULT
TB_TMG_SHIFT







月度累计工时<250明细
月度累计工时汇总 日常管理O类员工
TB_TMG_DAILYSUMMARY


连续7天未休息 日常管理（O类员工）
TB_SELF_MID_ATTENDANCE_MONTH
来自定时任务
从TB_TMG_DAILYSUMMARY获取月度出勤数据
插入到TB_SELF_MID_ATTENDANCE_MONTH
```
com/o/framework/task/AttendanceMidTask.java
```


当天缺勤明细
连续旷工两天明细

TB_SELF_MID_ATTENDANCE
来自 定时任务
TB_TMG_DAILYSUMMARY
TB_TMG_DAILYHANDLERESULT
TB_TMG_SHIFT
```
com/o/framework/task/AttendanceMidTask.java
```





月度累计工时<250明细
月度累计工时汇总 日常管理O类员工
TB_SELF_ATTENDANCE_TOTAL
来自
TB_TMG_DAILYSUMMARY
```
com/o/framework/task/AttendanceMidTask.java
attendanceMidMapper.insertMonthTotal();
```










# 详细


部门/体系平均延时出勤数
TB_TMG_EMPOVERTIMEDETAIL

体系平均延时出勤
TB_TMG_EMPOVERTIMEDETAIL

月度延迟出勤明细
TB_TMG_EMPOVERTIMEDETAIL

月度延时超100 
TB_TMG_EMPOVERTIMEDETAIL

市外出差
TB_TMG_OUTCITYTRAVEL

月度出差天数超过20
TB_TMG_OUTCITYTRAVEL

考勤异常人员明细
TB_TMG_ABNORMAL

周度延时出勤
TB_TMG_WEEKOVERTIMEDETAIL

有薪假库存
TB_TMG_LEAVE_INVENTORY



连续7天未休息 日常管理（O类员工）
TB_SELF_MID_ATTENDANCE_MONTH
来自定时任务
获取月度出勤数据
TB_TMG_DAILYSUMMARY
插入到
TB_SELF_MID_ATTENDANCE_MONTH
```
com/o/framework/task/AttendanceMidTask.java
```


当天缺勤明细
TB_SELF_MID_ATTENDANCE
来自 定时任务
TB_TMG_DAILYSUMMARY
TB_TMG_DAILYHANDLERESULT
TB_TMG_SHIFT
```
com/o/framework/task/AttendanceMidTask.java
```

连续旷工两天明细
TB_SELF_MID_ATTENDANCE




月度累计工时汇总 日常管理O类员工
TB_SELF_ATTENDANCE_TOTAL
来自
TB_TMG_DAILYSUMMARY
```
com/o/framework/task/AttendanceMidTask.java
attendanceMidMapper.insertMonthTotal();
```

月度累计工时<250明细
TB_SELF_ATTENDANCE_TOTAL



## 和考勤无关

异动
TB_STA_INTRANSFERINFO
TB_INF_OA_FLOWINFO
TB_EMP_TRANSFER_APPLY
TB_STA_EMP_TURNOVER


离职
TB_STA_EXIT_APPROVAL
TB_INF_OA_FLOWINFO



# END



