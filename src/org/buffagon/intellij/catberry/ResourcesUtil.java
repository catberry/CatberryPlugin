package org.buffagon.intellij.catberry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Stack;

/**
 * @author Prokofiev Alex
 */
public final class ResourcesUtil {

  public static void copyResource(@NotNull final String resource,
                                  @NotNull final String targetPath,
                                  @Nullable final Processor<String, String> processor) throws IOException {
    InputStream in = ResourcesUtil.class.getClassLoader().getResourceAsStream(resource);
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(targetPath)));
    BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
    copyStream(processor, writer, rdr);
    writer.close();
    in.close();
  }

  private static void copyStream(@Nullable Processor<String, String> processor, BufferedWriter writer, BufferedReader rdr) throws IOException {
    String buf;
    while ((buf = rdr.readLine()) != null) {
      if(processor != null)
        buf = processor.process(buf);
      writer.write(buf);
      writer.newLine();
    }
  }

  public static void copyResourcesDir(@NotNull final String resourcesDir,
                                      @NotNull final String targetDir,
                                      @Nullable final Processor<String, String> processor)
      throws IOException, URISyntaxException {
    File f = new File(targetDir);
    if (!f.exists())
      f.mkdirs();

    Enumeration<URL> resources = ResourcesUtil.class.getClassLoader().getResources(resourcesDir);
    File rootDir = new File(resources.nextElement().toURI());
    Stack<File> stack = new Stack<File>();
    stack.add(rootDir);

    while (!stack.isEmpty()) {
      File parent = stack.pop();
      for (File child : parent.listFiles()) {
        String relative = rootDir.toURI().relativize(child.toURI()).getPath();
        File currentFile = new File(targetDir + File.separator + relative);
        if (child.isFile()) {
          InputStream in = new FileInputStream(child);
          BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
          BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
          copyStream(processor, writer, rdr);
          writer.close();
          in.close();
        } else {
          currentFile.mkdirs();
          stack.add(child);
        }
      }
    }
  }
}
