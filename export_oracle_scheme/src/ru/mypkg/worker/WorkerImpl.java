package ru.mypkg.worker;

import ru.mypkg.beans.UserObject;
import ru.mypkg.dao.Dao;
import ru.mypkg.filework.FileWorker;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 20.02.11
 * Time: 18:21
 */
public class WorkerImpl implements Worker {

    private Dao dao;
    private FileWorker fileWorker;


    public void work() {
        System.out.println("start getting of user object list for processing");
        List<UserObject> list = dao.getUserObjectList();
        System.out.println("get " + list.size() + " objects");
        for (UserObject obj : list){
            obj = dao.fillDDL(obj);
            fileWorker.save2file(obj);
        }
        System.out.println(" done " );
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void setFileWorker(FileWorker fileWorker) {
        this.fileWorker = fileWorker;
    }
}
