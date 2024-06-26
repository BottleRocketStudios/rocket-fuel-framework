package com.bottlerocket.config;

import org.openqa.selenium.InvalidArgumentException;

/**
 * Created by ford.arnett on 9/22/22
 * This class holds extra configuration values that are not already defined in the framework as recognized values.
 *
 * There might be an opportunity to combine some of these pairs rather than having each object only a few values of the class.
 * For example something like you pass in the file key to the constructor and then remove all the pairs,
 * just leaving a single more generic key/value pair. Just an idea
 */
public class UndefinedConfig {
    public static final String PROPERTY_FILE_EXTRA_CAPABILITY_KEY = "extra_capability";
    public static final String PROPERTY_FILE_EXTRA_SYSTEM_KEY = "extra_system";
    public static final String PROPERTY_FILE_SAUCE_CAPABILITY_KEY = "sauce_capability";
    public static final String PROPERTY_FILE_SAUCE_OPTION_KEY = "sauce_option";

    //For selenium/appium capabilities
    public String capabilityName;
    public String capabilityValue;

    //System values
    public String systemKey;
    public String systemValue;

    //Sauce options
    public String sauceCapabilityKey;
    public String sauceCapabilityValue;
    public String sauceOptionKey;
    public String sauceOptionValue;

    private static String removePrefix(String fullString, String prefix) {
        return removePrefix(fullString, prefix, true);
    }

    private static String removePrefix(String fullString, String prefix, boolean isCaseSensitive) {
        if(!isCaseSensitive) {
            fullString = fullString.toLowerCase();
        }

        //Remove all the underscore "_" characters from the variable name, then remove the prefix
        return fullString.replace(prefix,"").replaceAll("_", "");
    }

    public static UndefinedConfig undefinedCapability(String capabilityName, String capabilityValue) {
        UndefinedConfig config = new UndefinedConfig();

        //Strip off the key that let us know what this value was
        config.capabilityName = removePrefix(capabilityName, PROPERTY_FILE_EXTRA_CAPABILITY_KEY, false);
        config.capabilityValue = capabilityValue;

        return config;
    }

    public static UndefinedConfig undefinedSystemParameter(String systemKey, String systemValue) {
        //key doesn't necessarily need to be stripped off here since this is for internal use only.
        UndefinedConfig config = new UndefinedConfig();
        config.systemKey = systemKey;
        config.systemValue = systemValue;
        return config;
    }


    public static UndefinedConfig sauceOption(String sauceOptionKey, String sauceOptionValue) {
        UndefinedConfig config = new UndefinedConfig();

        //Strip off the key that let us know what this value was
        config.sauceOptionKey = removePrefix(sauceOptionKey, PROPERTY_FILE_SAUCE_OPTION_KEY, true);
        config.sauceOptionValue = sauceOptionValue;

        return config;
    }

    public static UndefinedConfig sauceCapability(String sauceCapabilityKey, String sauceCapabilityValue) {
        UndefinedConfig config = new UndefinedConfig();

        config.sauceCapabilityKey = removePrefix(sauceCapabilityKey,PROPERTY_FILE_SAUCE_CAPABILITY_KEY, true);
        config.sauceCapabilityValue = sauceCapabilityValue;


        return config;
    }

    /**
     * This method returns a Sauce Labs capability from a .properties file value
     * For example, a .properties file key-value SAUCELABS_CAPABILITY_DEVICE_NAME = "appium:deviceName", "^Samsung.*"
     * would return the Sauce Labs capability "appium:deviceName" -> "^Samsung.*"
     * Note: capability keys and values cannot contain a double quotation mark (") since they are all removed
     * @param keyValuePair
     * @param delimiter
     * @return an {@link UndefinedConfig} variable containing the Sauce Labs option key and value
     */
    public static UndefinedConfig getSauceCapability(String keyValuePair, String delimiter) {
        UndefinedConfig config = new UndefinedConfig();
        config.sauceCapabilityKey = getKey(keyValuePair, delimiter);
        config.sauceCapabilityValue = getValue(keyValuePair, delimiter);
        return config;
    }

    /**
     * This method returns a Sauce Labs option from a .properties file value
     * For example, a .properties file key-value SAUCE_OPTION_NAME = "name", "Android tests"
     * would return the Sauce Labs option "name" -> "Android tests"
     * Note: capability option keys and values cannot contain a double quotation mark (") since they are all removed
     * @param keyValuePair
     * @param delimiter
     * @return an {@link UndefinedConfig} variable containing the Sauce Labs option key and value
     */
    public static UndefinedConfig getSauceOption(String keyValuePair, String delimiter) {
        UndefinedConfig config = new UndefinedConfig();
        config.sauceOptionKey =  getKey(keyValuePair, delimiter);
        config.sauceOptionValue = getValue(keyValuePair, delimiter);
        return config;
    }

    /**
     * This method returns the key from a key-value pair read in through a .properties file
     * For example, a string containing the key-value pair "appium:deviceName", "^Samsung.*" with a comma (,) delimiter
     * would return the key "appium:deviceName"
     * Note: a key cannot contain a double quotation mark ("), even if escaped, since they are all removed
     * @param keyValuePair
     * @param delimiter
     * @return a string containing the key from the key-value pair
     */
    private static String getKey(String keyValuePair, String delimiter) {
        if (!keyValuePair.contains(delimiter)) {
            throw new InvalidArgumentException(
              "The delimiter was not found in this key-value pair. Check to see if your key value pair is setup correctly in the config files. \n" + 
              "Key/Value pair: " + keyValuePair + "\n" +
              "Delimiter: " + delimiter
           );
        }
        return keyValuePair.substring(0, keyValuePair.indexOf(delimiter)).replaceAll("\"", "");
    }

    /**
     * This method returns the value from a key-value pair read in through a .properties file
     * For example, a string containing the key-value pair "appium:deviceName", "^Samsung.*" with a comma (",") delimiter
     * would return the value "^Samsung.*"
     * Note: a key cannot contain a double quotation mark ("), even if escaped, since they are all removed
     * @param keyValuePair
     * @param delimiter
     * @return a string containing the value from the key-value pair
     */
    private static String getValue(String keyValuePair, String delimiter) {
        if(!keyValuePair.contains(delimiter)) {
            throw new InvalidArgumentException(
              "The delimiter was not found in this key-value pair. Check to see if your key value pair is setup correctly in the config files. \n" + 
              "Key/Value pair: " + keyValuePair + "\n" +
              "Delimiter: " + delimiter
           );
        }
        return keyValuePair.substring(keyValuePair.indexOf(delimiter) + 1).replaceAll("\"", "").trim();
    }
}
