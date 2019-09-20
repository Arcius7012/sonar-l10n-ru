import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.*;
import java.nio.file.*;


public class Main {

    static Map<String, String> mapMissing = new TreeMap<String, String>();
    static Map<String, String> overTranslating = new TreeMap<String, String>();

    public static void main(String[] args) throws Exception {

        System.out.println("Starting to prepare bundle and translation");
       
        try (Stream<Path> paths = Files.walk(Paths.get("compare"))) {
            paths
                .filter(Files::isRegularFile)
                .forEach( filePath -> {
                        processReport(filePath);
                    }
                );
                
        } 
    }

    private static void processReport(Path reportPath)
    {
        System.out.println(reportPath);
        
        try {
            Files.lines(reportPath).forEach( currentLine -> {
                boolean isMiisingTranslate;
                boolean isOverTranslated;
                 if (currentLine.equalsIgnoreCase("The following translations do not exist in the reference bundle:")) {
                    System.out.println("OverTranslate found");  
                    isMiisingTranslate = false;
                    isOverTranslated = true;
                 } else if (currentLine.equalsIgnoreCase(" Missing translations are:")) {
                    System.out.println("MiisingTranslate found");
                    isMiisingTranslate = true;
                    isOverTranslated = false;
                 } else {
                    processReportString(currentLine, isOverTranslated, isMiisingTranslate);
                 };
                });
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void processReportString(String currentLine, Boolean isOverTranslate, Boolean isMiisingTranslate)
    {
        if (currentLine.startsWith("#")) {
            // comment
            return;
        }
        if ("".equals(currentLine.trim())) {
            // blank
            return;
        }

        if ((isOverTranslate == null) && (isMiisingTranslate == null)) {
            //this is only start of report

        } else if ((isOverTranslate == false) && (isMiisingTranslate == true)) {
            //current line from miising translation

        } else if ((isOverTranslate == true) && (isMiisingTranslate == false)) {
            //current line from miising translation
            
        }
    }
}

























