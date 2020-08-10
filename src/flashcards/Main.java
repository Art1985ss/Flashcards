package flashcards;

public class Main {
    private static String importFilePath = null;
    private static String exportFilePath = null;

    public static void main(String[] args) {
        processArgs(args);
        Application application = new Application(importFilePath, exportFilePath);
        application.execute();
    }

    private static void processArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-import".equals(args[i])) {
                importFilePath = args[i + 1];
                continue;
            }
            if ("-export".equals(args[i])) {
                exportFilePath = args[i + 1];
            }
        }
    }
}
