package com.csvdbupload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * ResourceLoader Class
 *    extends CsvToDatabaseLoader
 *    implements ResourceLoaderInterface
 */
public class ResourceLoader extends CsvToDatabaseLoader implements ResourceLoaderInterface {
    @Override
    public PropertiesModel propsLoader(String propertiesFileName, String dbName) {
        log("propsLoader: Properties file to load :: " + propertiesFileName);
        PropertiesModel propertiesModel = new PropertiesModel();
        try (InputStream input = loadInputResource(propertiesFileName)) {
            // load a properties file
            Properties propOutput = new Properties();
            propOutput.load(input);

            // set the property values to local object
            propertiesModel.setSourceUrl(propOutput.getProperty(dbName+".url"));
            propertiesModel.setSourceUser(propOutput.getProperty(dbName+".user"));
            propertiesModel.setSourcePassword(propOutput.getProperty(dbName+".password"));
            propertiesModel.setDbDriver(propOutput.getProperty(dbName+".driver"));
            propertiesModel.setSchemaName(propOutput.getProperty(dbName+".schema"));

            log("propsLoader: Properties file load completed");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
         return propertiesModel;
    }

    @Override
    public InputStream loadInputResource(String resourceName) {
        log("loadInputResource: Resource" + resourceName);
        InputStream input = super.getClass().getClassLoader().getResourceAsStream(resourceName);
        log("loadInputResource: Resource Loaded to InputStream" + input.toString());
        return input;
    }

    @Override
    public BufferedReader loadBufferedResource(InputStream input) {
        log("loadBufferedResource: Resource" + input.toString());
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(input));
        log("loadBufferedResource: Resource Loaded to BufferedReader" + bufReader.toString());
        return bufReader;
    }
}
