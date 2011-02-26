package ru.qwazer.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import ru.qwazer.beans.UserObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 19.02.11
 * Time: 15:01
 */
public class DaoImpl extends JdbcDaoSupport implements Dao {


    private Map<String, Set<String>> map;
    private Map<String,String> transformParams;
    private Set<String> onlyTypes;

    public UserObject fillDDL(UserObject obj) {
        String ddl = "";
        ddl += getBasedDDL(obj);
        ddl += getDependedDDL(obj);
        obj.setDdl(ddl);
        return obj;
    }


    private String getBasedDDL(UserObject obj) {
        return getDLLByTypeName(obj.getType(), obj.getName());
    }


    private String getDependedDDL(UserObject obj) {
        String res = "";
        Set<String> dependedItems = map.get(obj.getType());
        if (dependedItems != null) {
            for (String depItem : dependedItems) {
                res += getDependentDLLByTypeName(depItem, obj.getName()).trim();
            }
        }
        return res;

    }


    private String getDLLByTypeName(final String type, final String name) {

        return (String) getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                setTransformParameters(connection);
                PreparedStatement ps = connection.prepareStatement("select dbms_metadata.get_ddl(?, ?) from dual");
                ps.setString(1, type);
                ps.setString(2, name);
                ResultSet rs = ps.executeQuery();
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
                    System.err.println("error of get_dependent_ddl for object type " + type + " of object name " + name);
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


    public List<UserObject> getUserObjectList() {
        return getUserObjectListPrivate(null);
    }

    public List<UserObject> getUserObjectList(List<String> types) {
        String whereAdd = null;
        if (types != null && !types.isEmpty()) {
            whereAdd = " where object_type in ( ";
            for (String type : types) {
                whereAdd += "'" + type.toUpperCase() + "',";
            }
            whereAdd += "'')";
        }
        return getUserObjectListPrivate(whereAdd);
    }

    private List<UserObject> getUserObjectListPrivate(String whereAdd) {

        String select_sql = "select t.object_name, t.object_type from user_objects t";
        final String sql;
        if (whereAdd != null && !whereAdd.equals("")) {
            sql = select_sql + whereAdd;
        } else sql = select_sql;

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


    private void setTransformParameters(Connection connection) throws SQLException {
        String sql;
        for ( String param: transformParams.keySet()){
           sql = "call DBMS_METADATA.SET_TRANSFORM_PARAM(-1,'" + param + "',"+transformParams.get(param)+")";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute();
        }
    }

    public void setMap(Map<String, Set<String>> map) {
        this.map = map;
    }

    public void setTransformParams(Map<String, String> transformParams) {
        this.transformParams = transformParams;
    }


    public void setOnlyTypes(Set<String> onlyTypes) {
        this.onlyTypes = onlyTypes;
    }


}
