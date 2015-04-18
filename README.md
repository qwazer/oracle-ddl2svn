Set of tools for automatization storing of oracle DDL schema in SVN.

### The contents of the set ###
  1. [scheme2ddl](http://code.google.com/p/scheme2ddl) - command line utility for export oracle schema in set of ddl scripts. provide lot of configurations via basic command line options or advanced XML configuration
  1. additional scripts (sh, bat and ant) for automated retrieving scheme ddl and storing it in svn


### Benefits ###
Using of SVN (or other VCS tools) for storing changes of oracle scheme is great idea.
Simple scenario as 2-step script
```
exp user/password@sid owner=owner file=file.dmp log=logfile.log rows=n
imp user/password@sid full=y file=file.dmp show=y log=script.log
```
is work, but outbut is ugly formatted scripts in one file, with lot of additional information.

**scheme2ddl** give ability to filter undesirable information, separate DDL in different files, pretty format output.


### How to start with minimal configuration ###
Java must be installed on your computer.
For exporting oracle scheme you must provide
  * DB connection string
  * output directory
Usage example. Command
```
java -jar scheme2ddl.jar -url scott/tiger@localhost:1521:ORCL -o C:/temp/oracle-ddl2svn/
```
will produce directory tree
```
 views/
       view1.sql
       view2.sql
 tables/
       table1.sql
 functions
      /f1.sql  
```

More command line options
```
java -jar scheme2ddl.jar -help
```


### How it is work inside? ###

  1. First, get list of all user\_object to export
```
select * from user_objects where object_type in ()
```
  1. then applying [dbms\_metadata.set\_transform\_param](http://download.oracle.com/docs/cd/B19306_01/appdev.102/b14258/d_metada.htm#i1000135)
  1. for every user object invoke [dbms\_metadata.get\_ddl](http://download.oracle.com/docs/cd/B19306_01/appdev.102/b14258/d_metada.htm#i1019414) and [dbms\_metadata.get\_dependent\_ddl](http://download.oracle.com/docs/cd/B19306_01/appdev.102/b14258/d_metada.htm#i1019414)
  1. print every ddl to separate file grouped in folders like tables, views, procedures etc

### Advanced configuration ###
advanced configuration provided in scheme2ddl.config.xml,
