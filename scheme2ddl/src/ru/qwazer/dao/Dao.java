package ru.qwazer.dao;

import ru.qwazer.beans.UserObject;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 19.02.11
 * Time: 15:00
 */
public interface Dao {

    UserObject fillDDL(UserObject obj);

   // public String getTableDDL(UserObject obj);


   // UserObject getUserObjectByTypeName(String type, String name);

    /**
     *  Get user object list for processing;
     * @return  List of ru.qwazer.beans.UserObject
     */
    List<UserObject> getUserObjectList();

    /**
     * Get user object list for processing, filter by specified types
     * @param types  list of processed types
     * @return   List of ru.qwazer.beans.UserObject
     */
    List<UserObject> getUserObjectList(List<String> types);
}
