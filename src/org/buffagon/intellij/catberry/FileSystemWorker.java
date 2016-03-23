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
    BufferedReader br = new BufferedReader(new FileReader(file));
    StringBuilder stringBuilder = new StringBuilder();
    String ls = System.getProperty("line.separator");

    String line;
    while((line=br.readLine())!=null) {
      stringBuilder.append(processor.process(line));
      stringBuilder.append(ls);
    }
    br.close();

    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    bw.write(stringBuilder.toString());
    bw.close();
  }
}
