package ru.qwazer.scheme2ddl.filework;

import ru.qwazer.scheme2ddl.beans.UserObject;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 19.02.11
 * Time: 18:55
 */
public interface FileWorker {
    void save2file(UserObject obj);
}
