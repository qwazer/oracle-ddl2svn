package ru.qwazer.scheme2ddl.main;

import oracle.jdbc.pool.OracleDataSource;
import ru.qwazer.scheme2ddl.utils.SpringUtils;
import ru.qwazer.scheme2ddl.worker.Worker;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 20.02.11
 * Time: 10:27
 */
public class Main {

    private static boolean justPrintUsage = false;
    private static String dbUrl = null;
    public static String outputDir = null;
    public static boolean includeStorageInfo = false;

    public static void main(String[] args) throws Exception {

//        IWorker worker = (IWorker) SpringUtils.getSpringBean("worker");
//
//        worker.work();
        collectArgs(args);
        if (justPrintUsage) {
            printUsage();
            return;
        }
        Worker worker = (Worker) SpringUtils.getSpringBean("worker");
        if (dbUrl!=null || outputDir!=null || includeStorageInfo){
            modifyWorkerConfig(worker);
        }
        worker.work();

    }

    private static void modifyWorkerConfig(Worker worker) throws Exception{
         if (dbUrl!=null){
            OracleDataSource ds = new OracleDataSource();
            ds.setURL("jdbc:oracle:thin:"+dbUrl);
            worker.getDao().setDataSource(ds);
        }
        if (outputDir!=null){
            worker.getFileWorker().setOutputPath(outputDir);
        }
    }

    private static void collectArgs(String[] args) throws Exception {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-help") || arg.equals("-h")) {
                justPrintUsage = true;
            } else if (arg.equals("-url")) {
                dbUrl = args[i + 1];
                i++;
            } else if (arg.equals("-o") || arg.equals("-output")) {
                outputDir = args[i + 1];
                i++;
                createDir();
            } else if (arg.startsWith("-")) {
                // we don't have any more args to recognize!
                String msg = "Unknown argument: " + arg;
                System.err.println(msg);
                printUsage();
                throw new Exception("");
            }
        }
    }

    private static void createDir() throws Exception {
        if (!outputDir.endsWith("\\")){
            outputDir +=  "\\";
        }
        try {
            //createDir(outputDir);  //todo
        } catch (Exception e) {
            System.err.println("Cannot create output directory with name, exit");
            throw new Exception("");
        }
    }

    /**
     * Prints the usage information for this class to <code>System.out</code>.
     */
    private static void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append("oracle_schema_exporter [-url ] [-o] [-s]" + lSep);
        msg.append("util for export oracle schema from DB to DDL scripts (file per object)" + lSep);
        msg.append("internally call to dbms_metadata.get_ddl " + lSep);
        msg.append("more config options in scheme2ddl.config.xml " + lSep);
        msg.append("Options: " + lSep);
        msg.append("  -help, -h              print this message" + lSep);
       // msg.append("  -version               print the version information and exit" + lSep);
        // msg.append("  -verbose, -v           be extra verbose" + lSep);
        msg.append("  -url,                  DB connection URL, example scott/tiger@localhost:1521:ORCL" + lSep);

        msg.append("  -output, -o            output dir" + lSep);
        msg.append("  -s,                    include storage info in DDL scripts (default no include)" + lSep);
        System.out.println(msg.toString());
    }

}
