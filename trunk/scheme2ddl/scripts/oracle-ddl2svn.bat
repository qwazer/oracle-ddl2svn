@echo off
rem DESCRIPTION
rem two-step script
rem 1) get DDLs from oracle schema
rem 2) auto-commit DDLs in SVN

rem REQUREMENTS
rem java must be installed
rem output dir must be under version control in svn

rem SET PARAMETERS
rem first parameter is DB_URL
rem second parameter is OUTPUT_DIR
rem you can overwrite this parameters
set DB_URL=%1
set OUTPUT_DIR=%2

rem delete all files from output directory exept system files
rem this command must keep on disk svn meta information stored in .svn folders
del %OUTPUT_DIR% /Q /S /A-S
java -jar scheme2ddl.jar -url %DB_URL% -output %OUTPUT_DIR%
call autocommit %OUTPUT_DIR% 







