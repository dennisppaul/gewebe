/*
 * Gewebe
 *
 * This file is part of the *wellen* library (https://github.com/dennisppaul/wellen).
 * Copyright (c) 2024 Dennis P Paul.
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package gewebe;

import processing.core.PApplet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Gewebe {

    /**
     * @param pZipFile            absolute path to destination zip-file
     * @param pSourceFileOrFolder absolute path to file(s) or directory to be zipped
     */
    public static void zip(String pZipFile, String pSourceFileOrFolder) {
        try {
            FileOutputStream fos       = new FileOutputStream(pZipFile);
            ZipOutputStream  zipOut    = new ZipOutputStream(fos);
            File             fileToZip = new File(pSourceFileOrFolder);

            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static String get_resource_path() {
        return Gewebe.class.getResource("").getPath();
    }

    public static void run_sketch_with_resources(Class<? extends PApplet> pSketch) {
        PApplet.runSketch(new String[]{"--sketch-path=" + Gewebe.get_resource_path(), pSketch.getName()}, null);
    }


    /**
     * @param pZipFile         absolute path to source zip-file
     * @param pDestinationPath absolute path to directory to unpack zip-file in
     */
    public static void unzip(String pZipFile, String pDestinationPath) {
        try {
            File           destDir  = new File(pDestinationPath);
            byte[]         buffer   = new byte[1024];
            ZipInputStream zis      = new ZipInputStream(new FileInputStream(pZipFile));
            ZipEntry       zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int              len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath  = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private static void zipFile(File pFileOrDirectoryToZip, String fileName, ZipOutputStream zipOut) {
//        if (pFileOrDirectoryToZip.isHidden()) {
//            return;
//        }
        try {
            if (pFileOrDirectoryToZip.isDirectory()) {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                }
                zipOut.closeEntry();
                File[] children = pFileOrDirectoryToZip.listFiles();
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            FileInputStream fis      = new FileInputStream(pFileOrDirectoryToZip);
            ZipEntry        zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int    length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
