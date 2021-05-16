package com.csvdbupload;

import com.ibatis.common.jdbc.ScriptRunner;
import com.mysql.cj.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * CsvToDatabaseLoader Class
 */
public class CsvToDatabaseLoader {
    static Connection dbconnection = null;
    static PreparedStatement statement = null;

    public static void main(String[] args) {
        String datasource ="";
        if(StringUtils.isNullOrEmpty(args[0])) {
            // Assumption: Default datasource is mysql
            datasource = "mysql";
        } else {
            // Assumption: Properties has valid connection parameters in teh dbresource.properties file
            datasource = args[0].toString().toLowerCase();
        }
        // Load the database connection params from the properties based on the db server
        PropertiesModel dbPropertiesModel = new ResourceLoader().propsLoader("dbresource.properties", datasource);

        // Make a JDBC connection
        makeJDBCConnection(dbPropertiesModel);

        // Create a new Database if not exists
        createDatabase(dbPropertiesModel);
        // Execute the DDL
        executeSql(dbPropertiesModel);

        int batchSize = 10;

        try {
            log("Start: Loading of CSV data");
            dbconnection.setAutoCommit(false);

            String sql = "INSERT INTO STORE_ORDER (ID, ORDER_ID, ORDER_DATE, SHIP_DATE, SHIP_MODE, QUANTITY, DISCOUNT, PROFIT, PRODUCT_ID, CUSTOMER_NAME, CATEGORY, CUSTOMER_ID, PRODUCT_NAME) VALUES ( ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = dbconnection.prepareStatement(sql);

            log("Process Check: Opening LineReader");

            ResourceLoader resourceLoader = new ResourceLoader();
            BufferedReader lineReader = resourceLoader.loadBufferedResource(resourceLoader.loadInputResource("sales.csv"));
            //BufferedReader lineReader = new ResourceLoader().csvLoader("sales.csv");

            int count = 0;

            // Skip Header Line
            log("Process Check: Skipping Header Line \n" + lineReader.readLine());

            // Get Store Model records
            Collection<StoreModel> storeList = getStoreModels(lineReader);

            // Get Distinct records
            List<StoreModel> distinctElements = getStoreModels(storeList);

            // Log distinct elements
            log( "distinctElements" + distinctElements );

            // Set records to statement
            storeStatement(batchSize, count, distinctElements);

            // commit to DB
            dbconnection.commit();
            log("Process Check: Commit executed");

            // close connection
            dbconnection.close();
            log("Process Check: Connection closed");

        } catch (IOException ex) {
            System.err.println(ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                // Rollback
                dbconnection.rollback();
                log("Process Check: Rollback executed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * getStoreModels function : Get distinct records on OrderId, CustomerID and Product ID
     */
    private static List<StoreModel> getStoreModels(Collection<StoreModel> storeList) {
        // Set Distinct object by Key
        List<StoreModel> distinctElements = storeList.stream()
                .filter( distinctByKey(s -> s.orderId) )
                .collect( Collectors.toList() );

        distinctElements = distinctElements.stream()
                .filter( distinctByKey(s -> s.customerId) )
                .collect( Collectors.toList() );

        distinctElements = distinctElements.stream()
                .filter( distinctByKey(s -> s.productId) )
                .collect( Collectors.toList() );
        return distinctElements;
    }

    /**
     * storeStatement function
     */
    private static void storeStatement(int batchSize, int count, List<StoreModel> distinctElements) throws SQLException, ParseException {
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat inputFormat = new SimpleDateFormat("dd.MM.yy");
        for (StoreModel storeEntry : distinctElements) {
            statement.setString(1, String.valueOf(count++));
            statement.setString(2, storeEntry.orderId);
            java.util.Date oDate = inputFormat.parse(storeEntry.orderDate);
            String stringODate = outputFormat.format(oDate);
            statement.setDate(3, Date.valueOf(stringODate));
            java.util.Date sDate = inputFormat.parse(storeEntry.shipDate);
            String stringSDate = outputFormat.format(sDate);
            statement.setDate(4, Date.valueOf(stringSDate));
            statement.setString(5, storeEntry.shipMode);
            statement.setInt(6, Integer.parseInt(storeEntry.quantity));
            statement.setFloat(7, Float.parseFloat(storeEntry.discount));
            statement.setFloat(8, Float.parseFloat(storeEntry.profit));
            statement.setString(9, storeEntry.productId);
            statement.setString(10, storeEntry.customerName);
            statement.setString(11, storeEntry.category);
            statement.setString(12, storeEntry.customerId);
            statement.setString(13, storeEntry.productName);

            log("Process Check: Adding to Batch Execution");
            statement.addBatch();

/*            if (count % batchSize == 0) {
                log("Process Check: Execution");
                statement.executeBatch();
            }*/
        }
        // execute the remaining queries
        statement.executeBatch();
    }

    /**
     * getStoreModels function
     */
    private static Collection<StoreModel> getStoreModels(BufferedReader lineReader) throws IOException {
        String lineText = null;
        Collection<StoreModel> storeList = new ArrayList<>();
        while ((lineText = lineReader.readLine()) != null) {
            log("Process Check: Reading Lines \n" + lineText);
            lineText = lineText.replaceAll("[\\n\\t]", " ").replaceAll(", ", " ");
            String[] data = lineText.split(",");
            StoreModel storeModel = new StoreModel(data);
            storeList.add(storeModel);
        }
        return storeList;
    }

    /**
     * distinctByKey function
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * makeJDBCConnection function
     */
    private static void makeJDBCConnection(PropertiesModel dbPropertiesModel) {
        try {
            Class.forName(dbPropertiesModel.getDbDriver());
            log("MySQL JDBC Driver registered!");
        } catch (ClassNotFoundException e) {
            log("Sorry, couldn't found JDBC driver. Make sure you have added JDBC Maven Dependency Correctly");
            e.printStackTrace();
            return;
        }
    }

    /**
     * createDatabase function
     */
    private static void createDatabase(PropertiesModel dbPropertiesModel) {
        try {
            // Opening a new connection to Create Database and close once db creation is success
            dbconnection = DriverManager.getConnection(dbPropertiesModel.getSourceUrl() + "?user=" + dbPropertiesModel.getSourceUser() + "&password=" + dbPropertiesModel.getSourcePassword());
            if (dbconnection != null) {
                log("Connection Successful! Next Step - Create a Database ");
            } else {
                log("Failed to make connection!");
            }
            Statement stmt = dbconnection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbPropertiesModel.getSchemaName());
            dbconnection.close();
            log("Database creation - Success: " + dbPropertiesModel.getSchemaName());
        } catch (Exception e) {
            System.err.println("Failed to Create Database : " + dbPropertiesModel.getSchemaName()
                    + " The error is " + e.getMessage());
        }
    }

    /**
     * executeSql function
     */
    private static void executeSql(PropertiesModel dbPropertiesModel) {
        try {
            // Opening a new connection
            dbconnection = DriverManager.getConnection(dbPropertiesModel.getSourceUrl() + dbPropertiesModel.getSchemaName(), dbPropertiesModel.getSourceUser(), dbPropertiesModel.getSourcePassword());
            if (dbconnection != null) {
                log("Connection Successful! Next Step - Load the DDL - Create Table ");
            } else {
                log("Failed to make connection!");
            }
            // Initialize object for ScripRunner
            ScriptRunner sr = new ScriptRunner(dbconnection, false, false);

            // Give the input file to Reader
            ResourceLoader resourceLoader = new ResourceLoader();
            Reader reader = resourceLoader.loadBufferedResource(resourceLoader.loadInputResource("schema.sql"));

            // Execute script
            sr.runScript(reader);
            log("Table creation - Success");
        } catch (Exception e) {
            System.err.println("Failed to Execute : schema.sql"
                    + " The error is " + e.getMessage());
        }
    }

    /**
     * log function
     */
    // Simple log utility
    public static void log(String string) {
        System.out.println(string);

    }
}
