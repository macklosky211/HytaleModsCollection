package com.katsi.lib.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hypixel.hytale.logger.HytaleLogger;

public class ConfigManager {
    public static ConfigManager instance;
    private HashMap<String, ConfigMap<?>> config_map;
    private Gson GSON;

    private final String CONFIG_PATH = "config/KatsiLib/";

    public ConfigManager() {
        instance = this;
        instance.GSON = new Gson();
        this.config_map = new HashMap<String, ConfigMap<?>>();
        setup();
    }

    @SuppressWarnings("unchecked")
    public static <T> void saveConfig(String file_name, T data, Class<T> type) {
        ConfigMap<T> config_map;
        if (instance.config_map.containsKey(file_name)){
            config_map = (ConfigMap<T>) instance.config_map.get(file_name);
            if (config_map == null) {
                System.err.println(String.format("[ERROR|KatsiLib] Failed to save file. 'ConfigMap was null'"));
                return;
            }
        } else {
            config_map = new ConfigMap<T>(instance.makeFile(file_name), data, type);
            instance.config_map.put(file_name, config_map);
        }
        
        String json_string = instance.GSON.toJson(data);
        
        if (json_string.isBlank()) {
            if (config_map.logger != null) {
                config_map.logger.atWarning().log("[WARNING|KatsiLib] Attempting to save empty config file");
            } else {
                System.out.println("[WARNING|KatsiLib] Attempting to save empty config file");
            }
        }

        try (FileWriter file_writer = new FileWriter(config_map.file)) {
            file_writer.write(json_string);
            if (config_map.logger != null) {
                config_map.logger.atInfo().log(String.format("[KatsiLib] Sucessfully saved config to '%s'", config_map.file.getAbsolutePath()));
            } else {
                System.out.println(String.format("[KatsiLib] Sucessfully saved config to '%s'", config_map.file.getAbsolutePath()));
            }
        } catch (IOException e) {
            if (config_map.logger != null) {
                config_map.logger.atSevere().log(String.format("[ERROR|KatsiLib] Failed to save file. '%s'", e.toString()));
            } else {
                System.err.println(String.format("[ERROR|KatsiLib] Failed to save file. '%s'", e.toString()));
            }
            return;
        }
    }


    /**
     * Loads a config and returns the object that the file may contain.
     * The config is stored in the ConfigManager and can be referenced externally.
     * Note: This method uses Gson to parse json files; this means that all 'Map' obj's (HashMaps, etc) will be turned into 'LinkedTreeMap's & All numeric values will be Double's
     * IMPORTANT: assert that the config file actually exists before calling this function -> configExists(file_name);
     * @param <T>
     * @param file_name
     * @param type
     * @param logger
     * @return
     */
    public static <T> T loadConfig(String file_name, Class<T> type) {
        if (!instance.config_map.containsKey(file_name)) {
            // System.err.println("[ERROR|KatsiLib] Attempted to load config file that doesnt exist");
            // return null;
            ConfigMap<T> new_config = new ConfigMap<T>(instance.makeFile(file_name), type);
            instance.config_map.put(file_name, new_config);
        }

        @SuppressWarnings("unchecked")
        ConfigMap<T> config_map = (ConfigMap<T>) instance.config_map.get(file_name);
        if (config_map == null) {
            System.err.println("[ERROR|KatsiLib] Failed to parse config_map object into useable object");
            return null;
        }

        String file_content = "";
        try (Scanner scanner = new Scanner(config_map.file)) {
            scanner.useDelimiter("\\Z");

            if (scanner.hasNext()) {
                file_content = scanner.next();
            } else {
                config_map.logger.atWarning().log("Attempted to load empty file...");
            }

        } catch (FileNotFoundException e) {
            return null;
        }

        try {
            config_map.object = instance.GSON.fromJson(file_content, type);
        } catch (JsonSyntaxException e) {
            System.err.println(String.format("[KatsiLib] Encountered error when decoding config '%s', error: '%s'", file_name, e));
            return null;
        }

        return config_map.object;
    }

    /**
     * Checks for a config file, this is meant to be used before calling loadConfig() to determine that there is a config to load.
     * @param <T>
     * @param file_name
     * @param type
     * @return
     */
    public static <T> boolean configExists(String file_name) {
        if (instance.config_map.containsKey(file_name)) return true;
        File file = instance.getFile(file_name);
        if (file == null) return false;

        
        
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\Z");
            if (scanner.hasNext()) {
                return true;
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            System.err.println("[ERROR|KatsiLib] Failed to use file scanner while loading config.");
            return false;
        }
    }

    public static ConfigMap<?> getData(String file_name) {
        if (instance.config_map.containsKey(file_name)) {
            return instance.config_map.get(file_name);
        } else {
            return null;
        }
    }

    public static void setLogger(String file_name, HytaleLogger logger) {
        if (!instance.config_map.containsKey(file_name)) {
            if (logger != null) {
                logger.atSevere().log(String.format("[ERROR|KatsiLib] Failed to set logger object for '%s'", file_name));
            } else {
                System.err.println(String.format("[ERROR|KatsiLib] Failed to set logger object for '%s'", file_name));
            }
            return;
        }

        instance.config_map.get(file_name).setLogger(logger);
    }

    /**
     * Retrieves a valid file, otherwise creates new file with specified file_name.
     * @param file_name
     * @return
     */
    private File getFile(String file_name) {
        File new_file = new File(CONFIG_PATH.concat(file_name));
        if (new_file.exists()) return new_file;
        else return null;
    }

    private File makeFile(String file_name) {
        File new_file = new File(CONFIG_PATH.concat(file_name));
        if (new_file.exists()) return new_file; // File Already exist's.
        try {
            new_file.createNewFile();
            return new_file;
        } catch (IOException e) {
            config_map.get(file_name).logger.atSevere().log(String.format("[ERROR|KatsiLib] Failed to create file '%s'", file_name));
            return null;
        }
    }

    /**
     * Creates a new ConfigMap and puts it in the global config_map.
     * @param <T> ObjectType that the ConfigMap is intending to store
     * @param file_name The file_name the json file will be created at.
     * @param config_type The class of the object you are intending to store.
     * @return the new ConfigMap that is created.
    */
    public <T> ConfigMap<T> createMapConfig(String file_name, Class<T> type) {
       if (instance.config_map.containsKey(file_name)) {
            @SuppressWarnings("unchecked")
            ConfigMap<T> config = (ConfigMap<T>) instance.config_map.get(file_name);
            return config;
        } else {
            ConfigMap<T> new_config = new ConfigMap<T>(getFile(file_name), type);
            instance.config_map.put(file_name, new_config);
            return new_config;
        }
    }

    /**
     * Creates nessisary file structure for this mod. {Server_Root}/config/KatsiLib
     */
    private void setup() {
        File config_dir = new File(CONFIG_PATH);
        if (!config_dir.exists()) config_dir.mkdirs();
    }

    @SuppressWarnings("unused")
    private static class ConfigMap<T> {
        public final File file; // file pointer to where its stored.
        public final Class<T> type; // The type of object that is being stored.
        public HytaleLogger logger; // active logger for its assosiated mod, this is useful for error handling on per-mod basis.
        public T object; // The actual object that your intending to store in a json file.

        public ConfigMap(File file, Class<T> type) {
            this.file = file;
            this.type = type;
        }

        public ConfigMap(File file, T object, Class<T> type, HytaleLogger logger) {
            this.file = file;
            this.object = object;
            this.type = type;
            this.logger = logger;
        }

        public ConfigMap(File file, T object, Class<T> type) {
            this.file = file;
            this.object = object;
            this.type = type;
        }

        public void setLogger(HytaleLogger logger) {
            this.logger = logger;
        }

        public void setConfigObject(T obj) {
            this.object = obj;
        }
    }
}
