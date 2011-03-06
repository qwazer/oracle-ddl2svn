@echo off
rem script for auto commiting directory tree changes in svn
rem directory must be under version control
for /f "tokens=2*" %%i in ('svn status %1 ^| find "?"') do svn add "%%i"
for /f "tokens=2*" %%i in ('svn status %1 ^| find "!"') do svn delete "%%i"
svn commit -m "Automatic commit" %1
