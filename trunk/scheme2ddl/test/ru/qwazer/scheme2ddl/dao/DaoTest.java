package ru.qwazer.scheme2ddl.dao;

import org.junit.Test;
import ru.qwazer.scheme2ddl.Dao;
import ru.qwazer.scheme2ddl.SpringUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 20.02.11
 * Time: 11:44
 */
public class DaoTest {


    List<String> getFilterList(){
        List<String> list = new ArrayList<String>();
        list.add("table");
        list.add("view");
        return list;

    }

    @Test
    public void testGetUserObjectListIsNotNull() throws Exception {
        Dao dao = (Dao) SpringUtils.getSpringBean("dao");
        assertNotNull(dao.getUserObjectList());
    }

    @Test
    public void testGetUserObjectListIsNotEmpty() throws Exception {
        Dao dao = (Dao) SpringUtils.getSpringBean("dao");
        assertFalse(dao.getUserObjectList().isEmpty());
    }
    

    @Test
    public void testGetUserObjectListWithFilterIsNotNull() throws Exception {

        Dao dao = (Dao) SpringUtils.getSpringBean("dao");
        assertNotNull(dao.getUserObjectList(getFilterList()));
    }


}
