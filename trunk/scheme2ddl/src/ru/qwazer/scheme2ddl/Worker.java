package ru.qwazer.scheme2ddl;

import ru.qwazer.scheme2ddl.FileWorker;
import ru.qwazer.scheme2ddl.UserObject;
import ru.qwazer.scheme2ddl.Dao;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 20.02.11
 * Time: 18:21
 */
public class Worker {

    private Dao dao;
    private FileWorker fileWorker;


    public void work() {
        System.out.println("start getting of user object list for processing");
        List<UserObject> list = dao.getUserObjectList();
        System.out.println("get " + list.size() + " objects");
        for (UserObject obj : list){
            obj = dao.fillDDL(obj);
            fileWorker.save2file(obj);
            System.out.print(".");
        }
        System.out.println(" done " );
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void setFileWorker(FileWorker fileWorker) {
        this.fileWorker = fileWorker;
    }

    public Dao getDao() {
        return dao;
    }

    public FileWorker getFileWorker() {
        return fileWorker;
    }
}
