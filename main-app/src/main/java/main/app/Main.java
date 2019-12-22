package main.app;

import com.sun.jna.platform.win32.Kernel32;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by ArtemParfenov on 21.12.2019.
 */
public class Main {
    private static String myPid;
    public static void main(String[] args) {
        System.out.println("version 0");
        myPid = getMyPid();
        System.out.println("PID: " + myPid);
        try {
            System.out.println("Started!");
            Thread.sleep(3000);
            System.out.println("Ready to kill");
            runReplaceProcess();
            System.out.println("Still alive here");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished!");
    }

    private static String getMyPid() {
        return String.valueOf(Kernel32.INSTANCE.GetCurrentProcessId());
    }

    public static void runReplaceProcess() {
        try {
            selfDestructWindowsJARFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void selfDestructWindowsJARFile() throws Exception {
        Path path = Optional.of(Paths.get("self-destruct.bat"))
                .map(p -> {
                    if (!p.toFile().exists()) {
                        try {
                            return Files.createFile(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    } else {
                        return p;
                    }
                })
                .orElse(null);
        try (FileWriter fileWriter = new FileWriter(path.toFile());
             PrintWriter printWriter = new PrintWriter(fileWriter))
        {
            printWriter.println("set mypid=%1");
            printWriter.println("taskkill /F /PID %mypid%");
            File currJarName = ProgramDirectoryUtilities.getCurrentJARFilePath();
            System.out.println(currJarName);
            printWriter.println("DEL /F \"" + currJarName + "\"");
            printWriter.println("xcopy /Y \"D:\\tmp\\delME\\main-app-1.0-SNAPSHOT-jar-with-dependencies.jar\"* \"" +
                    currJarName + "\"*"
            );
            printWriter.println("exit");

        }
        Runtime.getRuntime().exec("cmd /c START \"\" \"" + path.toFile() + "\" " + myPid);
    }
}
