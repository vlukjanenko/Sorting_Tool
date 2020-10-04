package sorting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

abstract class SortedData<T> {

    protected String dataType;
    protected int commonCounter;
    Map<T, Integer> tmp = new LinkedHashMap<>(); // for byCount sort
    ArrayList<T> content = new ArrayList<>(); // for content store

/*
    Load data to content
*/
    abstract protected void loadData(String inputFile);

/*
    Create output stream (outputFile or System.out)
*/
    protected PrintStream getStream(String outputFile) {
        PrintStream stream = null;
        try {
            stream = outputFile != null ? new PrintStream(new File(outputFile)) : new PrintStream(System.out);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return stream;
    }

/*
    Create input Scanner (inputFile  or System.in)
*/
    protected Scanner getScanner(String inputFile) {
        Scanner scanner = null;
        try {
            scanner = inputFile != null ? new Scanner(new File(inputFile)) : new Scanner(System.in);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return scanner;
    }

/*
    Natural sort
*/
    public void sortData(String sortingType, String inputFile) {
        loadData(inputFile);
        content.sort(null);
        if (sortingType != null && sortingType.equals("byCount")) {
            sortByCount();
        }
    }

/*
    Filling Map tmp for byCount output.
*/
    protected void sortByCount() {

        for (int i = 0; i < content.size(); i++) {
            if (tmp.get(content.get(i)) == null) {
                tmp.put(content.get(i), 1);
            } else {
                tmp.put(content.get(i), tmp.get(content.get(i)) + 1);
            }
        }
    }

/*
    Output choose
*/
    public void printStat(String sortingType, String outputFile) {
        PrintStream stream = getStream(outputFile);
        printTotal(stream);
        if (sortingType == null || sortingType.equals("natural")) {
            printNatural(stream);
        } else {
            printByCount(stream);
        }
    }

/*
    Output Total:
*/
    protected void printTotal(PrintStream stream) {
        stream.printf("Total %s: %d\n", dataType, commonCounter);
    }

/*
    Output natural
*/
    protected void printNatural(PrintStream stream) {
        stream.print("Sorted data: ");
        for (T k : content) {
            stream.print(k + " ");
        }
    }

/*
    Output byCount
*/
    protected void printByCount(PrintStream stream) {
        tmp.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(s-> stream.println(s.getKey() + ": " + s.getValue() + " time(s). " + (int)(100 * (double)s.getValue() / commonCounter) + "%" ));
    }
}

class SortedLong extends SortedData<Long> {

    SortedLong() {
        dataType = "numbers";
    }

    @Override
    protected void loadData(String inputFile) throws NumberFormatException {
        Scanner scanner = getScanner(inputFile);
        while (scanner.hasNext()) {
            String str = scanner.next();
            long number;
            try {
                number = Long.parseLong(str);
            } catch (NumberFormatException e) {
                System.out.printf("\"%s\" isn't a long. It's skipped.\n", str);
                continue;
            }
            commonCounter++;
            content.add(number);
        }
    }
}

class SortedWord extends SortedData<String> {

    SortedWord() {
        dataType = "words";
    }

    @Override
    protected void loadData(String inputFile) {
        Scanner scanner = getScanner(inputFile);
        while (scanner.hasNext()) {
            commonCounter++;
            content.add(scanner.next());
        }
    }
}

class SortedLine extends SortedData<String> {

    SortedLine() {
        dataType = "lines";
    }

    @Override
    protected void loadData(String inputFile) {
        Scanner scanner = getScanner(inputFile);
        while (scanner.hasNext()) {
            commonCounter++;
            content.add(scanner.nextLine());
        }
    }

    @Override
    protected void printNatural(PrintStream stream) {
        stream.println("Sorted data: ");
        for (String k : content) {
            stream.println(k);
        }
    }
}

public class Main {
    public static void validateParams(String[] argv) {
        for (String param : argv) {
            if (param.charAt(0) == '-' && !param.equals("-sortingType") && !param.equals("-dataType") && !param.equals("-inputFile") && !param.equals("-outputFile")) {
                System.out.printf("%s isn't a valid parameter. It's skipped.\n", param);
            }
        }
    }

    public static String getCmdLineParam(String param, String[] args) {

        int i;

        i = 0;
        while (i <  args.length) {
            if (args[i].equals(param) && i == args.length - 1) {
                return "";
            } else if (args[i].equals(param) && i < args.length - 1) {
                return args[i + 1];
            }
        i++;
        }
        return null;
    }

    static SortedData sdCreator(String dataType) {
        if (dataType == null)
            return new SortedWord();
        switch (dataType) {
            case "long":
                return new SortedLong();
            case "line":
                return new SortedLine();
            case "word":
                return new SortedWord();
            default:
                return new SortedWord();
        }
    }

    public static void main(final String[] args) {

        final String inputFile;
        final String outputFile;
        final String dataType;
        final String sortingType;
        SortedData d;

        validateParams(args);
        sortingType = getCmdLineParam("-sortingType", args);
        dataType = getCmdLineParam("-dataType", args);
        inputFile = getCmdLineParam("-inputFile", args);
        outputFile = getCmdLineParam("-outputFile", args);
        if (dataType != null && dataType.equals("")) {
            System.out.println("No data type defined!");
            return;
        }
        if (sortingType != null && sortingType.equals("")) {
            System.out.println("No sorting type defined!");
            return;
        }
        if (inputFile != null && inputFile.equals("")) {
            System.out.println("No input file defined!");
            return;
        }
        d = sdCreator(dataType);
        d.sortData(sortingType, inputFile);
        d.printStat(sortingType, outputFile);
    }
}
