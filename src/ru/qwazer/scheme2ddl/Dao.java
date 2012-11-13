/*
 *    Copyright (c) 2011 Reshetnikov Anton aka qwazer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.qwazer.scheme2ddl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.*;
import java.util.*;


/**
 * DAO class for working with database
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 19.02.11
 * Time: 15:01
 */
public class Dao extends JdbcDaoSupport {

    private Map<String, Set<String>> map;
    private Map<String,String> transformParams;
    private Set<String> filterTypes;
    private Map<String,Set<String>> excludeMapPrefixes;
    private Map<String,Set<String>> excludeMapPatterns;
    private int objectsAge;

    public UserObject fillDDL(UserObject obj) {
        String ddl = "";
        ddl += getPrimaryDDL(obj);
        ddl += getDependedDDL(obj);
        obj.setDdl(ddl);
        return obj;
    }

    /**
     * There is  primary and depended DDL in DMBS_METADATA package
     * Example of primary is TABLE, example of depended is INDEX
     * @param obj
     * @return
     */
    private String getPrimaryDDL(final UserObject obj) {

        if (obj.getType().equals("DBMS_JOB"))
        {
            return findDbmsJobDDL(obj);

             }

        String sql = "select dbms_metadata.get_ddl(?, ?) from dual";
        if (obj.getType().equals("PUBLIC DATABASE LINK"))
            sql = "select dbms_metadata.get_ddl(?, ?, 'PUBLIC') from dual";
        final String query= sql;
        return (String) getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                setTransformParameters(connection);
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, obj.getType4DBMS());
                ps.setString(2, obj.getName());
                ResultSet rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        return rs.getString(1).trim();
                    }
                } finally {
                    rs.close();
                }
                return null;
            }
        });
    }

    private String findDbmsJobDDL(UserObject obj) {

       return  (String) getJdbcTemplate().execute("DECLARE\n" +
               " callstr VARCHAR2(4000);\n" +
               "BEGIN\n" +
               "  dbms_job.user_export("+obj.getName()+", callstr);\n" +
               ":done := callstr; " +
               "END;", new CallableStatementCallbackImpl() );

    }

    private class CallableStatementCallbackImpl implements CallableStatementCallback{
        public Object doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
            callableStatement.registerOutParameter( 1, java.sql.Types.VARCHAR  );
            callableStatement.executeUpdate();
            return callableStatement.getString(1);
        }
    }

    private String getDependedDDL(UserObject obj) {
        String res = "";
        Set<String> dependedTypes = map.get(obj.getType());
        if (dependedTypes != null) {
            for (String dependedType : dependedTypes) {
                res += getDependentDLLByTypeName(dependedType, obj.getName()).trim();
            }
        }
        return res;

    }

    private String getDependentDLLByTypeName(final String type, final String name) {

        return (String) getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                setTransformParameters(connection);
                PreparedStatement ps = connection.prepareStatement("select dbms_metadata.get_dependent_ddl(?, ?) from dual");
                ps.setString(1, type);
                ps.setString(2, name);
                ResultSet rs;
                try {
                    rs = ps.executeQuery();
                } catch (SQLException e) {
                    //System.err.println("error of get_dependent_ddl for object type " + type + " of object name " + name);
                    return "";
                }
                try {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                } finally {
                    rs.close();
                }
                return null;
            }
        });
    }

    /**
     *  Get user object list for processing;
     * @return  List of ru.qwazer.scheme2ddl.UserObject
     */
    public List<UserObject> getUserObjectList() {
        String whereAdd = null;
        if (filterTypes != null && !filterTypes.isEmpty()) {
            whereAdd = " and object_type in ( ";
            for (String type : filterTypes) {
                whereAdd += "'" + type.toUpperCase() + "',";
            }
            whereAdd += "'')";
        }
         List<UserObject> list = getUserObjectListPrivate(whereAdd);
        System.out.println("list.size() before filter = " + list.size());
        filterFromSystemTypes(list);
        filterFromExcludedTypes(list);
        return list;
    }

    /**
     * Remove exluded types specified by prefixes in config
     * @param list
     */
    private void filterFromExcludedTypes(List<UserObject> list) {
        filterFromExcludedTypesPrefixes(list);
        filterFromExcludedTypesPatterns(list);
    }


    /**
     * Remove exluded types specified by prefixes in config
     * @param list
     */
    private void filterFromExcludedTypesPrefixes(List<UserObject> list) {
        if (excludeMapPrefixes == null || excludeMapPrefixes.size()==0) return;
        List<UserObject> removed = new ArrayList<UserObject>();
        for (UserObject obj : list) {
            for (String typeName : excludeMapPrefixes.keySet()) {
                for (String prefix : excludeMapPrefixes.get(typeName)) {
                    if (obj.getType().equalsIgnoreCase(typeName) &&
                            obj.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                        removed.add(obj);
                    }
                }
            }
        }
        list.removeAll(removed);
    }


    private void filterFromExcludedTypesPatterns(List<UserObject> list) {
        if (excludeMapPatterns == null || excludeMapPatterns.size()==0) return;
        List<UserObject> removed = new ArrayList<UserObject>();
        for (UserObject obj : list) {
            for (String typeName : excludeMapPatterns.keySet()) {
                for (String pattern : excludeMapPatterns.get(typeName)) {
                    if (obj.getType().equalsIgnoreCase(typeName) &&
                            patternMatch(obj.getName().toLowerCase(), pattern)) {
                        removed.add(obj);
                    }
                }
            }
        }
        list.removeAll(removed);
    }

    private boolean patternMatch(String s, String pattern) {
        pattern = pattern.replace("*", "(.*)");
        return s.matches(pattern);
    }

    /**
     * For removing system types http://www.sql.ru/forum/actualthread.aspx?bid=3&tid=542661&hl=
     * @param list
     */
    private void filterFromSystemTypes(List<UserObject> list) {
        List<UserObject> removed= new ArrayList<UserObject>();
        for (UserObject obj : list ){
            if (obj.getType().equalsIgnoreCase("TYPE")
                    && obj.getName().startsWith("SYSTP")
                    && obj.getName().endsWith("==")){
                removed.add(obj);
            }
        }
        list.removeAll(removed);
    }

//    public List<UserObject> getUserObjectList(List<String> types) {
//        String whereAdd = null;
//        if (types != null && !types.isEmpty()) {
//            whereAdd = "and object_type in ( ";
//            for (String type : types) {
//                whereAdd += "'" + type.toUpperCase() + "',";
//            }
//            whereAdd += "'')";
//        }
//        return getUserObjectListPrivate(whereAdd);
//    }

    private List<UserObject> getUserObjectListPrivate(String whereAdd) {

        String select_sql =
                "select t.object_name, t.object_type " +
                "from user_objects t " +
                "where t.generated='N' and" +
                "      not exists (select 1 " +
                "                  from user_nested_tables unt " +
                "                  where t.object_name = unt.table_name) ";
        if (objectsAge>0){
            select_sql += " and last_ddl_time>=sysdate-"+objectsAge + " ";
        }
        final String sql;

        String publicDbLinksSql = "";
        String dbmsJobsSql = "";

        if (needToAddPublicDbLinks()){
            publicDbLinksSql += " union " +
                    " select db_link as object_name, 'PUBLIC DATABASE LINK' as object_type from DBA_DB_LINKS where owner='PUBLIC'";
        }

        if (needToAddDbmsJobs()){
            dbmsJobsSql += " union " +
                    " select job || '' as object_name, 'DBMS_JOB' as object_type from DBA_JOBS where schema_user != 'SYSMAN'";
        }


        if (whereAdd != null && !whereAdd.equals("")) {
            sql = select_sql + whereAdd + publicDbLinksSql + dbmsJobsSql;
        } else sql = select_sql + publicDbLinksSql + dbmsJobsSql;


        List<UserObject> list = (List<UserObject>) getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                PreparedStatement ps = connection.prepareStatement(sql);

                ResultSet rs = ps.executeQuery();
                List<UserObject> res = new ArrayList<UserObject>();
                try {
                    while (rs.next()) {
                        UserObject obj = new UserObject(rs.getString("object_type"), rs.getString("object_name"), null);
                        res.add(obj);
                    }
                } finally {
                    rs.close();
                }
                return res;
            }
        });

        return list;
    }

    private boolean needToAddDbmsJobs() {
        return filterTypes.contains("DBMS_JOB");
    }

    private boolean needToAddPublicDbLinks() {
        return filterTypes.contains("PUBLIC DATABASE LINK");
    }

    private void setTransformParameters(Connection connection) throws SQLException {
        String sql;
        for (String param: transformParams.keySet()) {
           connection.setAutoCommit(false);
       //    sql = "call DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM,'" + param + "',"+transformParams.get(param)+")";
           sql = "call DBMS_METADATA.SET_TRANSFORM_PARAM(-1,'" + param + "',"+transformParams.get(param)+")";
            //  DBMS_METADATA.SESSION_TRANSFORM replaced by -1 because,
            // variables and constants in the package can only be accessed from the PL / SQL,
            // not from SQL as in my case.
            //(for oracle 10 it works)  //todo test for oracle 11
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute();
        }
    }

    /**
     * Test db connection
     * @return
     */
    public boolean connectionAvailable() {
        try {
            getJdbcTemplate().queryForInt("select 1 from dual");
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    public void setMap(Map<String, Set<String>> map) {
        this.map = map;
    }

    public void setTransformParams(Map<String, String> transformParams) {
        this.transformParams = transformParams;
    }

    public void setFilterTypes(Set<String> types) {
        this.filterTypes = types;
    }

     public int getLast_ddl_time_age() {
        return objectsAge;
    }

    public void setLast_ddl_time_age(int howLong) {
        this.objectsAge = howLong;
    }

    public void setExcludeMapPrefixes(Map<String, Set<String>> excludeMapPrefixes) {
        this.excludeMapPrefixes = excludeMapPrefixes;
    }

    public void setExcludeMapPatterns(Map<String, Set<String>> excludeMapPatterns) {
        this.excludeMapPatterns = excludeMapPatterns;
    }
}
