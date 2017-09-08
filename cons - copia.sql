grant select on v_$sysstat TO alex;
grant select on v_$instance TO alex;
grant select on dba_free_space to alex;
grant select on v_$temp_space_header to alex;
grant select on dba_temp_files to alex;
grant select on dba_segments to alex;
grant select on  dba_data_files to alex;



select Monitor('USERS',0.80) from dual;
---------------------------------------------------------------------------------------------------------------------------------------------------------------

---------------------------Funcion para CBD-----------------------------------
CREATE OR REPLACE FUNCTION CBD(name varchar)
   RETURN NUMBER 
   IS tpd NUMBER;
      cbt NUMBER;
   BEGIN 
      select round(sum(s.value/(SYSDATE - startup_time)),3) INTO tpd
	from v$sysstat  s,v$instance i where s.NAME in ('user commits','transaction rollbacks') ;

      select sum(a1.data_length) into cbt from all_tab_columns a1, all_tables a2 where a1.table_name=a2.table_name 
		and a2.tablespace_name= name;

      RETURN((tpd*cbt)); 
    END;
/
----------------------------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------Funcion para espacio disponible hasta el tope tablespace------------------------------------------------------
CREATE OR REPLACE FUNCTION FreeT(name varchar, X NUMBER)
   RETURN NUMBER 
   IS ft NUMBER;
   BEGIN 
   IF name != 'TEMP' THEN
    select X*df.bytes - (df.bytes - SUM(fs.bytes)) into ft from dba_free_space fs,(SELECT tablespace_name,SUM(bytes) bytes
          FROM dba_data_files where tablespace_name in (name) 
         GROUP BY tablespace_name) df WHERE fs.tablespace_name (+)  = df.tablespace_name
 GROUP BY df.tablespace_name,df.bytes;
  ELSE 
    select (X*fs.bytes - (SUM(fs.bytes) - df.bytes_free)) into ft from dba_temp_files fs,(SELECT tablespace_name,bytes_free,bytes_used
          FROM v$temp_space_header GROUP BY tablespace_name,bytes_free,bytes_used) df
          WHERE fs.tablespace_name (+)  = df.tablespace_name
          GROUP BY fs.bytes,df.bytes_free;

  END IF;
      RETURN(ft); 
    END;
/
-----------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION Monitor(name varchar, X NUMBER)
   RETURN NUMBER 
   IS num NUMBER;
   BEGIN 
		select FreeT(name,X)/CBD(name) into num from dual;
      RETURN(num); 
    END;
/


******************************

select tablespace_name from dba_tablespaces;


SELECT COUNT(tablespace_name) FROM dba_tablespaces;


create or replace Procedure getMemory(c1 out SYS_REFCURSOR)/*consulta original*/
AS
begin
   open c1 for select df.tablespace_name "Tablespace",
	totalusedspace "Used MB",
	(df.totalspace - tu.totalusedspace) "Free MB",
	df.totalspace "Total MB",
	round(100 * ( (df.totalspace - tu.totalusedspace)/ df.totalspace))
	"Pct. Free"
	from
	(select tablespace_name,
	round(sum(bytes) / 1048576) TotalSpace
	from dba_data_files 
	group by tablespace_name) df,
	(select round(sum(bytes)/(1024*1024)) totalusedspace, tablespace_name
	from dba_segments 
	group by tablespace_name order by(tablespace_name)) tu
	where df.tablespace_name = tu.tablespace_name;
	end;
/


create or replace Procedure getMemory(c1 out SYS_REFCURSOR)
AS
begin
   open c1 for select df.tablespace_name "Tablespace",
	totalusedspace "Used MB",
	(df.totalspace - tu.totalusedspace) "Free MB",
	df.totalspace "Total MB"
	from
	(select tablespace_name,
	round(sum(bytes) / 1048576) TotalSpace
	from dba_data_files 
	group by tablespace_name) df,
	(select round(sum(bytes)/(1024*1024)) totalusedspace, tablespace_name
	from dba_segments 
	group by tablespace_name order by(tablespace_name)) tu
	where df.tablespace_name = tu.tablespace_name;
	end;
/

create or replace Procedure getMemoryTemp(c1 out SYS_REFCURSOR)
AS
begin
   open c1 for SELECT df.tablespace_name tspace,
       fs.bytes / (1024 * 1024),
       SUM(df.bytes_free) / (1024 * 1024)
  FROM dba_temp_files fs,
       (SELECT tablespace_name,bytes_free,bytes_used
          FROM v$temp_space_header
         GROUP BY tablespace_name,bytes_free,bytes_used) df
 WHERE fs.tablespace_name (+)  = df.tablespace_name
 GROUP BY df.tablespace_name,fs.bytes,df.bytes_free,df.bytes_used;
 
   end;
/




























