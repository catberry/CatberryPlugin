package org.buffagon.intellij.catberry;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * @author Prokofiev Alex
 */
public class FileSystemWorker {
  public static void processTextFilesRecursively(@NotNull File dir, Processor<String, String> processor) throws IOException {
    File[] files = dir.listFiles();
    if(files == null)
      return;
    for(File file : files) {
      if(file.isDirectory()) {
        FileSystemWorker.processTextFilesRecursively(file, processor);
        continue;
      }
      processTextFile(file, processor);
    }
  }

  public static void processTextFile(File file, Processor<String, String> processor) throws IOException {
    FileReader fr = new FileReader(file);
    BufferedReader br=new BufferedReader(fr);

    FileWriter fw = new FileWriter(file);
    BufferedWriter bw = new BufferedWriter(fw);
    String line;
    while((line=br.readLine())!=null) {
      bw.write(processor.process(line));
      bw.newLine();
    }
    br.close();
    bw.close();
  }
}
