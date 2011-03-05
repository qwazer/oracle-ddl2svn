import ru.qwazer.scheme2ddl.Dao;
import ru.qwazer.scheme2ddl.SpringUtils;
import ru.qwazer.scheme2ddl.UserObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 19.02.11
 * Time: 15:18
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        IDao dao = (IDao)SpringUtils.getSpringBean("dao");
//        UserObject obj = dao.getUserObjectByTypeName("TABLE", "EMP");
//        System.out.println("obj = " + obj);
//        IFileWorker fileWorker = (IFileWorker) SpringUtils.getSpringBean("fileWorker");
//        fileWorker.save2file(obj);
   //    testTableDDL();
        //UserObject obj = new UserObject();
        //obj.setName("SYS_IOT_OVER_60777", );
        fillDDL();
      //    testGetUserObjectListPrintData();
    }

    public static void fillDDL() throws Exception {
        Dao dao = (Dao) SpringUtils.getSpringBean("dao");
        UserObject obj = new UserObject("TABLE", "BONUS", null);
        obj = dao.fillDDL(obj);
        System.out.println("obj = " + obj);
    }





    public static void testGetUserObjectListPrintData() throws Exception {
        Dao dao = (Dao) SpringUtils.getSpringBean("dao");
        List<UserObject> list = dao.getUserObjectList();
         for (UserObject obj : list){
             System.out.println("obj = " + obj);
         }
    }


    static List<String> getFilterList(){
        List<String> list = new ArrayList<String>();
        list.add("table");
        list.add("view");
        return list;

    }




}
