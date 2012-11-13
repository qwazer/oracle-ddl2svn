#!/bin/bash

# DESCRIPTION
# two-step script
# 1) get DDLs from oracle schema
# 2) auto-commit DDLs in SVN
# REQUREMENTS
# java must be installed
# output dir must be under version control in svn


#------------------------------- Subroutines ---------------------------------
usage(){
echo " Usage: $(DB_URL $0) OUTPUT_DIR" 
echo ""
echo ""
}

#------------------------------- Process the options -------------------------
if [ $# -eq 2 ]
then
    db_url="$1"
    workingdir="$2"
else
    usage
    exit 1
fi

if ! cd $workingdir
then
    echo $workingdir is not a accessible path.
    usage
    exit 1
fi


#----------------- Try to export DDL ----------------
java -jar scheme2ddl.jar -url $db_url -output $workingdir

#------------------------------- Find out what has changed -------------------


svnstatus=$(svn status $workingdir)
added=$(printf "%s" "$svnstatus" | sed -n 's/^[A?] *\(.*\)/\1/p')
removed=$(printf "%s" "$svnstatus" | sed -n 's/^! *\(.*\)/\1/p')

if [ "x$added" != "x" ]
then
    echo adding "$added" to repository
    svn add $added
fi

if [ "x$removed" != "x" ]
then
    echo removing "$removed" to repository
    svn remove $removed
fi

svn commit -m "automatic commit by oracle-ddl2svn on AIX"
