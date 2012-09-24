@echo off

set COMMIT_MESSAGE="automatic commit by oracle-ddl2svn"


echo =========  start of svn commit  %date% %time% ==============
for /f "tokens=2*" %%i in ('svn status %OUTPUT_DIR% ^| find "?"') do svn add "%%i"
for /f "tokens=2*" %%i in ('svn status %OUTPUT_DIR% ^| find "!"') do svn delete "%%i"
svn commit -m %COMMIT_MESSAGE% %OUTPUT_DIR%  --non-interactive --no-auth-cache --username %SVN_USER% --password %SVN_PASSWORD%
echo =========  end of svn commit %date% %time% ==============


