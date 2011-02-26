package ru.reshet.beans;

/**
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

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
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
