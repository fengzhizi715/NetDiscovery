package cn.netdiscovery.core.config;

import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tony on 2019-01-15.
 */
public class YamlParser {

    private static final String DEFAULT_DELIMITER = "/";
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    private final String mIndent;
    private final KeyPathFactory mKeyPathFactory;


    /**
     * Creates the instance using default number of white space and the delimiter of keys.
     */
    public YamlParser() {
        this(2, DEFAULT_DELIMITER);
    }

    /**
     * Creates the instance using given number of white space for one indent.
     *
     * @param indentWhiteSpaceNumber Number of white space for one indent.
     */
    public YamlParser(int indentWhiteSpaceNumber) {
        this(indentWhiteSpaceNumber, DEFAULT_DELIMITER);
    }

    /**
     * Creates the instance using given delimiter of keys.
     *
     * @param delimiter The delimiter of keys.
     */
    public YamlParser(String delimiter) {
        this(2, delimiter);
    }

    /**
     * Creates the instance using given number of white space for one indent and the delimiter of keys.
     *
     * @param indentWhiteSpaceNumber Number of white space for one indent.
     * @param delimiter              The delimiter of keys.
     */
    public YamlParser(int indentWhiteSpaceNumber, String delimiter) {

        if (indentWhiteSpaceNumber < 2) {
            throw new IllegalArgumentException("`indentWhiteSpaceNumber` must be greater than or equal to 2");
        }

        if (delimiter == null) {
            throw new IllegalArgumentException("`delimiter` must not be null");
        }

        if (delimiter.length() <= 0) {
            throw new IllegalArgumentException("`delimiter` must not be empty string");
        }

        StringBuilder space = new StringBuilder();

        for (int i = 0; i < indentWhiteSpaceNumber; i++) {
            space.append(" ");
        }

        mIndent = space.toString();
        mKeyPathFactory = new KeyPathFactory(delimiter);
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param filePath A file.
     * @return Decoded YAML data.
     * @throws IOException Raises exception if I/O error is occurred.
     */
    public Map<String, Object> decode(String filePath) throws IOException {
        return decode(new FileInputStream(filePath));
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param is An InputStream.
     * @return Decoded YAML data.
     * @throws IOException Raises exception if I/O error is occurred.
     */
    public Map<String, Object> decode(InputStream is) throws IOException {
        return decode(is, DEFAULT_CHARSET_NAME);
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param filePath    A file.
     * @param charsetName The name of a supported charset.
     * @return Decoded YAML data.
     * @throws IOException Raises exception if I/O error is occurred.
     */
    public Map<String, Object> decode(String filePath, String charsetName) throws IOException {
        return decode(new FileInputStream(filePath), charsetName);
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param is          An InputStream.
     * @param charsetName The name of a supported charset.
     * @return Decoded YAML data.
     * @throws IOException Raises exception if I/O error is occurred.
     */
    public Map<String, Object> decode(InputStream is, String charsetName) throws IOException {

        if (is==null) {
            return null;
        }

        Map<String, Object> hash = new HashMap<String, Object>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charsetName));
        ArrayList<Object> list = null;
        String keyPath = "";

        String line;
        while ((line = reader.readLine()) != null) {

            // Ignore comment line and empty line.
            if (line.trim().startsWith("#") || "".equals(line.trim())) {
                continue;
            }

            String[] str = line.split(":");
            String key = str[0];
            int index = (int) (Math.floor(key.lastIndexOf(mIndent) * 0.5) + 1);

            if (key.trim().startsWith("-")) {
                // Parse list.
                if (list == null) {
                    list = new ArrayList<Object>();
                }

                String[] substr = line.split("-");
                list.add(substr.length == 2 ? substr[1].trim() : "");
            } else {
                if (Preconditions.isNotBlank(list)) {
                    // Add list to current keyPath.
                    hash.put(keyPath, list.clone());
                    list.clear();
                    list = null;
                }

                // New keyPath from current line.
                keyPath = mKeyPathFactory.createKeyPath(index, key);

//                // Add key-value if not only key.
                if (str.length > 1) {
                    hash.put(keyPath, line.substring(key.length()+1).replace("\"","").trim());
                }
            }
        }

        // Add last list if needed.
        if (Preconditions.isNotBlank(list)) {
            hash.put(keyPath, list.clone());
        }

        mKeyPathFactory.clear();

        IOUtils.closeQuietly(is,reader);

        return hash;
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param filePath A file.
     * @return Decoded YAML data. If I/O error is occurred, returns empty map.
     */
    public Map<String, Object> decodeOrEmpty(String filePath) {
        return decodeOrEmpty(filePath, DEFAULT_CHARSET_NAME);
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param is An InputStream.
     * @return Decoded YAML data. If I/O error is occurred, returns empty map.
     */
    public Map<String, Object> decodeOrEmpty(InputStream is) {
        return decodeOrEmpty(is, DEFAULT_CHARSET_NAME);
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param filePath    A file.
     * @param charsetName The name of a supported charset.
     * @return Decoded YAML data. If I/O error is occurred, returns empty map.
     */
    public Map<String, Object> decodeOrEmpty(String filePath, String charsetName) {
        try {
            return decode(new FileInputStream(filePath), charsetName);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Decodes YAML data and converts it to Map object.
     *
     * @param is          An InputStream.
     * @param charsetName The name of a supported charset.
     * @return Decoded YAML data. If I/O error is occurred, returns empty map.
     */
    public Map<String, Object> decodeOrEmpty(InputStream is, String charsetName) {
        try {
            return decode(is, charsetName);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Dump given YAML file.
     *
     * @param filePath A file.
     */
    public static void dump(String filePath) {
        dump(filePath, DEFAULT_CHARSET_NAME);
    }

    /**
     * Dump given YAML file.
     *
     * @param is An InputStream.
     */
    public static void dump(InputStream is) {
        dump(is, DEFAULT_CHARSET_NAME);
    }

    /**
     * Dump given YAML file.
     *
     * @param filePath    A file.
     * @param charsetName he name of a supported charset.
     */
    public static void dump(String filePath, String charsetName) {
        try {
            dump(new FileInputStream(filePath), charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dump given YAML file.
     *
     * @param is          An InputStream.
     * @param charsetName The name of a supported charset.
     */
    public static void dump(InputStream is, String charsetName) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, charsetName));
            StringBuilder builder = new StringBuilder();
            String lineSeparator = System.getProperty("line.separator");
            boolean isEmpty = true;

            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comment line and empty line.
                if (line.trim().startsWith("#") || "".equals(line.trim())) {
                    continue;
                }

                if (!isEmpty) {
                    builder.append(lineSeparator);
                }

                builder.append(line);
                isEmpty = false;
            }

            System.out.println(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is,reader);
        }
    }

    private class KeyPathFactory {

        private final List<String> mKeys = new ArrayList<String>();
        private final String mDelimiter;

        public KeyPathFactory(String delimiter) {
            mDelimiter = delimiter;
        }

        public String createKeyPath(int index, String key) {
            // Remove indents.
            key = key.trim();

            try {
                // Replace the key at index.
                mKeys.set(index, key);

                int len = mKeys.size();

                // Remove the keys after specified index.
                if (index < len) {
                    for (int i = index + 1; i < len; i++) {
                        mKeys.remove(i);
                    }
                }
            } catch (Exception e) {
                // Add the key at last of key path.
                mKeys.add(key);
            }

            // Create key path.
            StringBuilder builder = new StringBuilder(mKeys.get(0));

            for (int i = 1, len = mKeys.size(); i < len; i++) {
                builder.append(mDelimiter).append(mKeys.get(i));
            }

            return builder.toString();
        }

        public void clear() {
            mKeys.clear();
        }
    }

}
