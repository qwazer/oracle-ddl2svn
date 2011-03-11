/*
 *    Copyright (c) 2011.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.qwazer.scheme2ddl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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
public class FileWorker {

    private String outputPath;
    private Boolean sortByDirectory;


    public void save2file(UserObject obj) {
        try {

            String filePath = createFullFileName(obj);
            FileUtils.writeStringToFile(new File(filePath), obj.getDdl());
//            BufferedWriter out = new BufferedWriter(
//                    new FileWriter(filePath));
//
//            out.write(obj.getDdl());
//            out.close();
            System.out.println("saved " + obj.getName() + " to file " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //todo fix issue 2,3,4    //todo refactor
    public String createFullFileName(UserObject obj) throws IOException{
        String res = "";
        if (sortByDirectory){
           //String dirPath = outputPath + obj.getTypePlural();
           // createDirIfNotExist(dirPath);

           res = obj.getTypePlural() + "\\" + obj.getName4Filename() + ".sql";
        }
        else {
            res = obj.getName4Filename() + "." + obj.getType() + ".sql";
        }
        res =  outputPath + res;
        return FilenameUtils.separatorsToSystem(res);
    }

//    private void createDirIfNotExist(String dirPath) throws IOException {
//        File dir = new File(dirPath);
//        if (!dir.exists() && !dir.mkdirs()) {
//             throw new IOException("Unable to create " + dir.getAbsolutePath());
//        }
//    }


    public void setSortByDirectory(Boolean sortByDirectory) {
        this.sortByDirectory = sortByDirectory;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }


}
