package ru.qwazer.scheme2ddl;

/**
 * Class for storing database user object
 * It's not simple bean, it also provide additional get method for file worker class
 * Date: 19.02.11
 * Time: 14:53
 */
public class UserObject {

    public UserObject(String type, String name, String ddl) {
        this.type = type;
        this.name = name;
        this.ddl = ddl;
    }


    public String getName() {
        return name;
    }

    /**
     * Get name for filename
     * @return  name for filename
     */
    public String getName4Filename() {
        return name.toLowerCase().replaceAll("/", "");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    /**
     * used if sortByDirectory = true
     * for creating directory names
     * @return
     */
    public String getTypePlural() {
        if (type.toLowerCase().endsWith("x")) {
            return type.toLowerCase()+"ex";
        }
        return type.toLowerCase()+"s";
    }

    /**
     * Oracle types in user_table without underscore, for example PACKAGE BODY
     * but in DBMS_METADATA with underscore   PACKAGE_BODY
     * @return  type name foe using in  DBMS_METADATA package
     */

    public String getType4DBMS(){
        return type.replaceAll(" ", "_");
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    String name;
    String type;
    String ddl;

    @Override
    public String toString() {
        return "UserObject{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", ddl='" + ddl + '\'' +
                '}';
    }
}
