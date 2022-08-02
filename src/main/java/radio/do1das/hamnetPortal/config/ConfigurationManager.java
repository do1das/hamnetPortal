package radio.do1das.hamnetPortal.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import radio.do1das.hamnetPortal.util.Json;

import java.io.IOException;
import java.io.InputStream;

public class ConfigurationManager {

    private static ConfigurationManager myConfigurationManager;
    private static Configuration myCurrentConfiguration;
    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if(myConfigurationManager == null)
            myConfigurationManager = new ConfigurationManager();
        return myConfigurationManager;
    }

    /**
     * Userd to load configuration file by path
     */
    public void loadConfigurationFile(String filePath) {
        InputStream is = getClass().getResourceAsStream(filePath);
        if (is == null)
            throw new ConfigurationException("Fehlerhafter Pfad der Konfigurationsdatei: " + filePath);
        StringBuffer sb = new StringBuffer();
        int i;
        try {
            while( (i = i = is.read()) != -1)
                sb.append((char)i);

        } catch (IOException e) {
            throw new ConfigurationException(e);
        }

        JsonNode conf = null;
        try {
            conf = Json.parse(sb.toString());
        } catch (JsonProcessingException e) {
            throw new ConfigurationException("Error parsing Configuration File", e);
        }
        System.out.println(conf.toString());
        try {
            myCurrentConfiguration = Json.fromJson(conf, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new ConfigurationException("Error parsing the Configuration File internal", e);
        }
    }

    /**
     * Returns current configuration
     */
    public Configuration getCurrentConfiguration() {
        if(myCurrentConfiguration == null)
            throw new ConfigurationException("No Current Configuration set.");
        return myCurrentConfiguration;
    }
}
