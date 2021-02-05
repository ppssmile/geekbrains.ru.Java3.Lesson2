package Lesson3.client;

import java.io.*;

public final class LocalHistoryRecord {

    public LocalHistoryRecord() {
    }

    public static File getFile(String userName) {
        File file = new File(String.format("src/main/resources/history_%s.txt", userName));
        return file;
    }

    public static boolean isExistToFile(String userName) {
        File file = getFile(userName);
        if (!file.exists()) {
            try {
                System.out.println("File create!!!");
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("file non create.", e);
            }
            return true;
        } else {
            return false;
        }
    }

    public static void doWriteIntoFile(String userName, String message) {
        File file = getFile(userName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(String.format("\n[%s]: %s", userName, message));
            writer.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static String doLoadingHistory(String userName) {
        System.out.println("enter to doLoadingHistory!");
        File file = getFile(userName);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            long lines = reader.lines().count();
            reader = new BufferedReader(new FileReader(file));
            while (lines > 100) {
                reader.readLine();
                lines--;
            }
            while (lines > 0) {
                sb.append(reader.readLine() + "\n");
                lines--;
            }
            reader.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
