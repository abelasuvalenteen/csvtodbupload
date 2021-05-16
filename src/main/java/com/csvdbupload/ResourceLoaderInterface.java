package com.csvdbupload;

import java.io.BufferedReader;
import java.io.InputStream;

/**
 * ResourceLoaderInterface Class
 */
public interface ResourceLoaderInterface {
    PropertiesModel propsLoader(String propertiesFileName, String dbName);
    InputStream loadInputResource(String resourceName);
    BufferedReader loadBufferedResource(InputStream input);
}
