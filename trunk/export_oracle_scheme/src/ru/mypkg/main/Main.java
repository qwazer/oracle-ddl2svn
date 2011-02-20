package ru.mypkg.main;

import ru.mypkg.utils.SpringUtils;
import ru.mypkg.worker.Worker;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 20.02.11
 * Time: 10:27
 */
public class Main {

    public static void main(String[] args) {
        Worker worker = (Worker) SpringUtils.getSpringBean("worker");
        worker.work();
    }

    /**
     * Prints the usage information for this class to <code>System.out</code>.
     */
    private static void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append("oracle_schema_exporter [-url ] [-o]" + lSep);
        msg.append("util for export oracle schema from DB to separate DDL files"+ lSep);
        msg.append("internally call to dbms_metadata.get_ddl "+ lSep);
        msg.append("more config options in scheme-exporter.config.xml "+ lSep);
        msg.append("Options: " + lSep);
        msg.append("  -help, -h              print this message" + lSep);
        msg.append("  -version               print the version information and exit" + lSep);
       // msg.append("  -verbose, -v           be extra verbose" + lSep);
        msg.append("  -url,                  DB connection URL, example scott/tiger@localhost:1521:ORCL" + lSep);

        msg.append("  -output, -o            output dir"
                + lSep);
        System.out.println(msg.toString());
    }

}
