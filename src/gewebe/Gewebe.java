package gewebe;

import java.io.File;

public class Gewebe {
    public static String get_resource_path() {
        return Gewebe.class.getResource("").getPath();
    }

    public static String get_os_name() {
        final String mOS;
        if (System.getProperty("os.name").startsWith("Windows")) {
            mOS = "windows";
        } else if (System.getProperty("os.name").startsWith("Mac")) {
            mOS = "macos";
        } else {
            mOS = "linux";
        }
        return mOS;
    }

    public static boolean file_exists(String path) {
        return (new File(path).exists());
    }
}
