import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.nio.file.*;

public class Main {

    static Map<String, Map<String, String>> mapMissing = new TreeMap<String, Map<String, String>>();
    static Map<String, Map<String, String>> overTranslating = new TreeMap<String, Map<String, String>>();


    public static void main(String[] args) throws Exception {

        System.out.println("Starting to prepare bundle and translation");

        try (Stream<Path> paths = Files.walk(Paths.get("../compare"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach( filePath -> {
                                String version = filePath.getFileName().toString().subSequence(0,3).toString();
                                System.out.println("Found " + version);
                                addTreeForversion(version);
                                processReport(filePath, version);
                            }
                    );

        }

        System.out.println("End of report analyze");
        writeArchive();
        writeToTranslate();
    }

    private static void writeArchive() throws IOException {
        TreeMap<String, String> fullArchive = new TreeMap<String, String>();
        overTranslating.forEach((version, archive) -> fullArchive.putAll(archive));

        File myArchive = new File("../towork/archive-old.props");
        FileWriter  archiveWriter = new FileWriter(myArchive, false);

        fullArchive.forEach((key, value) -> {
            try {
                archiveWriter.write(key + "="+ value + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        archiveWriter.close();
    }

    private static void writeToTranslate() throws IOException {
        TreeMap<String, String> fullTranslate = new TreeMap<String, String>();
        mapMissing.forEach((version, missing) -> fullTranslate.putAll(missing));

        File myTranslationTasks = new File("../towork/to-translate.props");
        FileWriter  translateWriter = new FileWriter(myTranslationTasks, false);

        fullTranslate.forEach((key, value) -> {
            try {
                translateWriter.write(key + "="+ value + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        translateWriter.close();
    }

    private static void addTreeForversion(String version) {
        if (!mapMissing.containsKey(version)) {
            mapMissing.put(version, new TreeMap<String, String>());
        }
        if (!overTranslating.containsKey(version)) {
            overTranslating.put(version, new TreeMap<String, String>());
        }
    }

    private static void processReport(Path reportPath, String version) {
        System.out.println(reportPath);

        try {
            final boolean[] isMiisingTranslate = {false};
            final boolean[] isOverTranslated = new boolean[1];
            Files.lines(reportPath).forEach( currentLine -> {

                if (currentLine.equalsIgnoreCase("The following translations do not exist in the reference bundle:")) {
                    System.out.println("OverTranslate found");
                    isMiisingTranslate[0] = false;
                    isOverTranslated[0] = true;
                } else if (currentLine.equalsIgnoreCase(" Missing translations are:")) {
                    System.out.println("MiisingTranslate found");
                    isMiisingTranslate[0] = true;
                    isOverTranslated[0] = false;
                } else {
                    processReportString(currentLine, isOverTranslated[0], isMiisingTranslate[0], version);
                };
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void processReportString(String currentLine, Boolean isOverTranslate, Boolean isMiisingTranslate, String version)
    {
        if (currentLine.startsWith("#")) {
            // comment
            return;
        }
        if ("".equals(currentLine.trim())) {
            // blank
            return;
        }
        if ("=======================".equals(currentLine)) {
            // starter
            return;
        }

        if (currentLine.startsWith("See report file located at:")) {
            // starter
            return;
        }

        if ((isOverTranslate == false) && (isMiisingTranslate == false)) {
            //this is only start of report

        } else if ((isOverTranslate == false) && (isMiisingTranslate == true)) {
            //current line from miising translation
            putToMissingTranslate(version, currentLine);

        } else if ((isOverTranslate == true) && (isMiisingTranslate == false)) {
            putToOverTranslate(version, currentLine);
        }
    }

    private static void putToMissingTranslate(String version, String currentLine) {
        TrasnaltionPair translate = getPairFromLine(currentLine);

        mapMissing.get(version).put(translate.getkey(),translate.getTranslate());
    }


    private static void putToOverTranslate(String version, String currentLine) {
        TrasnaltionPair translate = getPairFromLine(currentLine);

        overTranslating.get(version).put(translate.getkey(),translate.getTranslate());
    }

    private static TrasnaltionPair getPairFromLine(String currentLine) {
        int indexPart = currentLine.indexOf("=");
        String firstPart;
        String secondPart;

        if (indexPart == -1) {
             firstPart = currentLine;
             secondPart = "";
        } else {
             firstPart = currentLine.substring(0, indexPart);
             secondPart = currentLine.substring(indexPart + 1);
        }

        return new TrasnaltionPair(firstPart, secondPart);
    }



}

class TrasnaltionPair {
    private final String key;
    private final String value;

    TrasnaltionPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getkey() {
        return key;
    }

    public String getTranslate() {
        return value;
    }

}

