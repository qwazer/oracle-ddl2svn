package ru.qwazer.filework;

import ru.qwazer.beans.UserObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Reshetnikov AV resheto@gmail.com
 * Date: 19.02.11
 * Time: 18:46
 */
public class FileWorkerImpl implements FileWorker {

    private String outputPath;
    private Boolean sortByDirectory;


    public void save2file(UserObject obj) {
        try {

            String filePath = createFullFileName(obj);
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(filePath));

            out.write(obj.getDdl());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createFullFileName(UserObject obj) throws IOException{
        String res = "";
        if (sortByDirectory){
           String dirPath = outputPath + obj.getType();
            prepareDir(dirPath);

           res = obj.getType() + "/" + obj.getName() + ".sql";
        }
        else {
            res = obj.getName() + "." + obj.getType() + ".sql";
        }
        return outputPath + res;
    }

    private void prepareDir(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
             throw new IOException("Unable to create " + dir.getAbsolutePath());
        }
    }


    public void setSortByDirectory(Boolean sortByDirectory) {
        this.sortByDirectory = sortByDirectory;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
