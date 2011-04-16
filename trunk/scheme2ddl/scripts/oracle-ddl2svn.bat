@echo off
rem DESCRIPTION
rem two-step script
rem 1) get DDLs from oracle schema
rem 2) auto-commit DDLs in SVN

rem REQUREMENTS
rem java must be installed
rem svn console client must be installed
rem output dir must be under version control in svn

rem SET PARAMETERS
rem first parameter is DB_URL
rem second parameter is OUTPUT_DIR
rem you can overwrite this parameters
set DB_URL=%1
set OUTPUT_DIR=%2
set SVN_USER=%3
set SVN_PASS=%4
set COMMIT_MESSAGE="automatic commit by oracle-ddl2svn"

rem test db connection,
rem if db connect is fail, do not perform any filesystem operation, only clean tmp file end exit
set tempfile=connection.tmp
del %tempfile%
java -jar scheme2ddl.jar --test-connection -url %DB_URL% > %tempfile% 2>&1
find "FAIL connect to" %tempfile%
if not errorlevel 1 goto :exit

:runScript
rem delete all files from output directory exept system files
rem this command must keep on disk svn meta information stored in .svn folders
del %OUTPUT_DIR% /Q /S /A-S
java -jar scheme2ddl.jar -url %DB_URL% -output %OUTPUT_DIR%
for /f "tokens=2*" %%i in ('svn status %OUTPUT_DIR% ^| find "?"') do svn add "%%i"
for /f "tokens=2*" %%i in ('svn status %OUTPUT_DIR% ^| find "!"') do svn delete "%%i"
svn commit -m %COMMIT_MESSAGE% %OUTPUT_DIR%  --non-interactive --no-auth-cache --username %SVN_USER% --password %SVN_PASS%

:exit
del %tempfile%
