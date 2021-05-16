package com.csvdbupload;

/**
 * PropertiesModel Class
 */
public class PropertiesModel {
    public String dbDriver;
    public String sourceUrl;
    public String sourceUser;
    public String sourcePassword;
    public String schemaName;

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }
    public void setSourceUser(String sourceUser) {
        this.sourceUser = sourceUser;
    }

    public String getSourceUser() {
        return sourceUser;
    }
    public void setSourcePassword(String sourcePassword) {
        this.sourcePassword = sourcePassword;
    }

    public String getSourcePassword() {
        return sourcePassword;
    }
}
