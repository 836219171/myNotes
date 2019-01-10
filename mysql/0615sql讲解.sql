CALL proc_drop_index ('mydb','emp');

CALL proc_drop_index ('mydb','dept');

1、列出自己的掌门比自己年龄小的人员


SELECT a.name,a.age ,c.name ceoname,c.age ceoage FROM t_emp a 
LEFT OUTER JOIN  t_dept b ON a.deptid=b.id
LEFT JOIN  t_emp c ON b.ceo=c.id
WHERE a.age>c.age


##优化后 
EXPLAIN SELECT  SQL_NO_CACHE a.name,a.age ,c.name ceoname,c.age ceoage FROM emp a 
LEFT OUTER JOIN  dept b ON a.deptid=b.id
LEFT JOIN  emp c ON b.ceo=c.id
WHERE a.age>c.age


TRUNCATE TABLE t_emp 






DELETE FROM  xxx 
 



2、列出所有年龄低于自己门派平均年龄的人员

SELECT c.name ,c.age ,aa.age avgage  FROM t_emp c
INNER JOIN 
(      SELECT a.deptid,AVG(age) age FROM t_emp a 
	WHERE a.deptid IS NOT NULL
	GROUP BY a.deptid
)aa ON c.deptid=aa.deptid 
 WHERE c.age<aa.age
 
 
 ##优化后 
EXPLAIN SELECT SQL_NO_CACHE c.name ,c.age ,aa.age avgage  FROM  emp c  #1.3  0.17
INNER JOIN 
(      SELECT a.deptid,AVG(age) age FROM  emp a 
	WHERE a.deptid IS NOT NULL
	GROUP BY a.deptid
)aa ON c.deptid=aa.deptid 
 WHERE c.age<aa.age



CREATE INDEX idx_deptid ON emp(deptid)

CREATE INDEX idx_deptid_age ON emp(deptid,age)

3、列出至少有2个年龄大于40岁的成员的门派

SELECT  b.deptname,COUNT(*) FROM  t_emp  a 
INNER JOIN t_dept b ON a.deptid=b.id
WHERE a.age >40
GROUP  BY b.deptname
HAVING COUNT(*)>=2



EXPLAIN SELECT SQL_NO_CACHE b.deptname,COUNT(*) FROM   emp  a   #0.76
INNER JOIN  dept b ON a.deptid=b.id
WHERE a.age >40
GROUP  BY b.deptname
HAVING COUNT(*)>=2


##优化后 
EXPLAIN SELECT SQL_NO_CACHE b.deptname,COUNT(*) FROM dept b     #0.76  0.026
STRAIGHT_JOIN  emp  a ON a.deptid=b.id
WHERE a.age >40
GROUP  BY b.deptname
HAVING COUNT(*)>=2

CREATE INDEX idx_deptid_age ON emp(deptid,age)

 CREATE INDEX idx_deptname ON dept(deptname)

4、至少有2位非掌门人成员的门派

SELECT * FROM t_emp WHERE id NOT IN (
	SELECT ceo FROM t_dept WHERE ceo IS NOT NULL
)

NOT IN  -> LEFT JOIN  ... WHERE xxx IS NULL

#
SELECT c.deptname ,COUNT(*) FROM t_emp a 
 INNER JOIN  t_dept c ON a.deptid=c.id
LEFT JOIN  t_dept b ON a.id =b.ceo  WHERE b.id IS NULL
AND a.deptid IS NOT NULL
GROUP BY c.deptname 
HAVING COUNT(*)>=2


##优化后   #0.1
EXPLAIN SELECT SQL_NO_CACHE c.deptname ,COUNT(*) FROM    dept c    ##2.3   #0.1
 STRAIGHT_JOIN   emp a  ON a.deptid=c.id
LEFT JOIN   dept b ON a.id =b.ceo  WHERE b.id IS NULL
GROUP BY c.id
HAVING COUNT(*)>=2


SELECT * FROM emp a   INNER JOIN  dept b  ON a.deptid=b.id
GROUP BY a.deptid 


CREATE INDEX idx_ceo ON dept(ceo);

CREATE INDEX idx_deptid ON emp(deptid);

CREATE INDEX idx_deptname ON dept(deptname);




  
EXPLAIN SELECT SQL_NO_CACHE a.deptname 
	FROM(SELECT b.deptid
		FROM dept a
		INNER JOIN emp b ON a.id = b.deptid
		WHERE a.ceo != b.id
		GROUP BY b.deptid
		HAVING COUNT(*)>1
		) c
	INNER JOIN  dept a ON a.`id` = c.`deptId` ;
	
	
	
	 
	
	
	
	
	
	
	EXPLAIN SELECT * FROM emp GROUP BY deptid ,NAME



5、列出全部人员，并增加一列备注“是否为掌门”，如果是掌门人显示是，不是掌门人显示否
 
SELECT a.name , (CASE WHEN  b.id IS NULL THEN '否' ELSE '是' END) '是否为掌门'  FROM  t_emp a LEFT JOIN t_dept b ON a.id=b.ceo




 

6、列出全部门派，并增加一列备注“老鸟or菜鸟”，若门派的平均值年龄>50 显示“老鸟”，否则显示“菜鸟”

 SELECT b.deptname, IF( AVG(age)>50,'老鸟','菜鸟') '老鸟or菜鸟'     FROM t_emp a INNER JOIN t_dept b ON a.deptid =b.id
 GROUP BY b.deptname





7、显示每个门派年龄最大的人


SELECT  NAME, MAX(age)  FROM  t_emp GROUP BY deptid


UPDATE t_emp SET age=150 WHERE NAME ='周芷若'

 
SELECT  aa.name ,aa.age FROM t_emp aa 
INNER JOIN
(
SELECT  a.deptid, MAX(a.age) age FROM t_emp a 
WHERE a.deptid IS NOT NULL
 GROUP BY a.deptid
 )ab ON aa.deptid=ab.deptid AND aa.age =ab.age
 
 
 ##优化后
EXPLAIN  SELECT SQL_NO_CACHE  aa.name ,aa.age FROM emp aa # 0.06
INNER JOIN
(
SELECT  a.deptid, MAX(a.age) age FROM emp a 
WHERE a.deptid IS NOT NULL
 GROUP BY a.deptid
 )ab ON aa.deptid=ab.deptid AND aa.age =ab.age
 
 
 CREATE INDEX idx_deptid_age ON emp(deptid,age)
 
 


 

8、显示每个门派年龄第二大的人

 


 

 
##  扩展性不好 ，需要取更多名次 无法实现
EXPLAIN SELECT SQL_NO_CACHE emp.name,dept.deptname
	FROM (SELECT MAX(age) MAX,a.deptid
		FROM (SELECT MAX(age) MAX,deptid  FROM  emp GROUP BY deptid) a
		INNER JOIN  emp b ON a.deptid = b.deptid 
		WHERE b.age !=a.max
		GROUP BY a.deptid) d 
	INNER JOIN  emp emp ON d.deptid = emp.deptid
	INNER JOIN  dept dept ON emp.deptid = dept.id
	WHERE age = d.max;


#分组排名
 oralce :rank()over()
 
 #mysql
 
SET @rank=0;
SET @last_deptid=0;
 SELECT NAME  FROM (
SELECT 
	 t.*,
	 IF (@last_deptid=deptid,@rank:=@rank+1,@rank:=1) AS  rk,
	  @last_deptid:=deptid AS last_deptid
	 FROM  emp  t
	 ORDER BY deptid ,age  DESC
) a WHERE a.rk=2;	

 
SELECT a.name,a.age,a.ceoname  newceo, c.name ceoname FROM t_emp a 
LEFT OUTER JOIN  t_dept b ON a.deptid=b.id
LEFT JOIN  t_emp c ON b.ceo=c.id

SELECT   a.name,a.age,a.ceoname FROM t_emp a


#冗余字段

SELECT a.name,a.age,a.ceoname  newceo FROM t_emp a

UPDATE t_emp a 
LEFT OUTER JOIN  t_dept b ON a.deptid=b.id
LEFT JOIN  t_emp c ON b.ceo=c.id
SET a.ceoname=c.name

UPDATE t_dept SET ceo=5 WHERE id=2


#跨表更新

ALTER TABLE t_emp ADD ceoname VARCHAR(200)



 DELIMITER $$
 
 ##触发器更新冗余
CREATE TRIGGER trig_update_dept
AFTER UPDATE ON t_dept
FOR EACH ROW 
  BEGIN 
	  UPDATE t_emp a 
	    LEFT OUTER JOIN  t_dept b ON a.deptid=b.id
	    LEFT JOIN  t_emp c ON b.ceo=c.id
	  SET a.ceoname=c.name
	  WHERE a.deptid=NEW.id;
  END $$
  
  
  
  ##不要自己触发自己
  CREATE TRIGGER trig_update_dept
AFTER UPDATE ON t_emp
FOR EACH ROW 
  BEGIN 
	  UPDATE t_emp a 
	    LEFT OUTER JOIN  t_dept b ON a.deptid=b.id
	    LEFT JOIN  t_emp c ON b.ceo=c.id
	  SET a.ceoname=c.name
	  WHERE a.deptid=NEW.id;
  END $$

  
  UPDATE t_emp SET NAME ='萧峰'  WHERE id =5



 
 