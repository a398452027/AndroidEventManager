/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 *
 */
public class fileUtil {

    public static String readFile(String path) {
        return readFile(path, "UTF-8");
    }

    public static String readFile(String path, String encoding) {
        BufferedReader reader = null;
        try {
            File file = new File(path);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            StringBuilder builder = new StringBuilder();
            char[] chars = new char[4096];

            int length = 0;

            while (0 < (length = reader.read(chars))) {
                builder.append(chars, 0, length);
            }

            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static int readLines(String path, List<String> strs) {
        return readLines(path, strs, "UTF-8", true);
    }

    public static int readLines(String path, List<String> strs, boolean trim) {
        return readLines(path, strs, "UTF-8", trim);
    }

    public static int readLines(String path, List<String> strs, String encoding, boolean trim) {
        BufferedReader reader = null;
        try {
            File file = new File(path);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String sline = null;

            while (null != (sline = reader.readLine())) {
                if (trim) {
                    sline = sline.trim();
                    if (!sline.equals("")) {
                        strs.add(sline);
                    }
                } else {
                    strs.add(sline);
                }
            }
            return strs.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] readFileBytes(String path) {
        FileInputStream reader = null;
        try {
            File file = new File(path);
            reader = new FileInputStream(file);
            int filelen = reader.available();

            byte[] chars = new byte[filelen];
            int length = 0;

            while (true) {
                int nl = reader.read(chars, length, Math.min(filelen - length, 1024 * 4));
                length += nl;
                if (nl <= 0 || length >= filelen) {
                    break;
                }
            }
            return chars;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void appendToFile(String path, String content) {
        appendToFile(path, content, "UTF-8");
    }

    public static void appendToFile(String path, String content, String encoding) {
        BufferedWriter writer = null;
        try {
            File file = new File(path);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), encoding));
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void appendToFile(String path, byte[] content) {
        FileOutputStream writer = null;
        try {
            File file = new File(path);
            writer = new FileOutputStream(file, true);
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void saveToFile(String path, String content) {
        saveToFile(path, content, "UTF-8");
    }

    public static void saveToFile(String path, String content, String encoding) {
        BufferedWriter writer = null;
        try {
            File file = new File(path);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), encoding));
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void saveToFile(String path, byte[] content) {
        FileOutputStream writer = null;
        try {
            File file = new File(path);
            writer = new FileOutputStream(file, false);
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean makeDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            return parent.mkdirs();
        }
        return false;
    }

    public static boolean makeDirectory(String fileName) {
        File file = new File(fileName);
        return makeDirectory(file);
    }

    public static boolean emptyDirectory(File directory) {
        boolean result = false;
        File[] entries = directory.listFiles();
        int sz = entries.length;
        for (int i = 0; i < sz; i++) {
            if (!entries[i].isDirectory()) {
                if (!entries[i].delete()) {
                    result = false;
                }
            }
        }
        return true;
    }

    public static boolean emptyDirectory(String directoryName) {
        File dir = new File(directoryName);
        return emptyDirectory(dir);
    }

    public static boolean deleteDirectory(String dirName) {
        return deleteDirectory(new File(dirName));
    }

    public static boolean deleteDirectory(File dir) {
        if ((dir == null) || !dir.isDirectory()) {
            throw new IllegalArgumentException("Argument " + dir + " is not a directory. ");
        }
        File[] entries = dir.listFiles();
        int sz = entries.length;

        for (int i = 0; i < sz; i++) {
            if (entries[i].isDirectory()) {
                if (!deleteDirectory(entries[i])) {
                    return false;
                }
            } else {
                if (!entries[i].delete()) {
                    return false;
                }
            }
        }

        if (!dir.delete()) {
            return false;
        }
        return true;
    }

    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    public static String getFilePath(String fileName) {
        File file = new File(fileName);
        return file.getAbsolutePath();
    }

    public static String getTypePart(String fileName) {
        int point = fileName.lastIndexOf('.');
        int length = fileName.length();
        if (point == -1 || point == length - 1) {
            return "";
        } else {
            return fileName.substring(point + 1, length);
        }
    }

    public static String getFileType(File file) {
        return getTypePart(file.getName());
    }

    public static int getPathLsatIndex(String fileName) {
        int point = fileName.lastIndexOf('/');
        if (point == -1) {
            point = fileName.lastIndexOf('\\');
        }
        return point;
    }

    public static int getPathLsatIndex(String fileName, int fromIndex) {
        int point = fileName.lastIndexOf('/', fromIndex);
        if (point == -1) {
            point = fileName.lastIndexOf('\\', fromIndex);
        }
        return point;
    }

    public static String trimType(String filename) {
        int index = filename.lastIndexOf(".");
        if (index != -1) {
            return filename.substring(0, index);
        } else {
            return filename;
        }
    }

    public static String getNamePart(String fileName) {
        int point = getPathLsatIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return fileName;
        } else if (point == length - 1) {
            int secondPoint = getPathLsatIndex(fileName, point - 1);
            if (secondPoint == -1) {
                if (length == 1) {
                    return fileName;
                } else {
                    return fileName.substring(0, point);
                }
            } else {
                return fileName.substring(secondPoint + 1, point);
            }
        } else {
            return fileName.substring(point + 1);
        }
    }
}
