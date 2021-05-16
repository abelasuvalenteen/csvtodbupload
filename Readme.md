*Pre-requisites Considered for the Assignment*
        
        > Programming : Java
        > Dependancy Management : Maven
        > Database : MySql 

*Main Class:*

    CsvToDatabaseLoader

    Accepts Parameter: 1 

    Datasource Parameter:
    mysql
    oracle

    e.g. java CsvToDatabaseLoader mysql
                - Assumption: default Datasource is mysql
         java CsvToDatabaseLoader oracle 
                - Assumption:
                     1. Datasource will be oracle
                     2. dbresource.properties should be updated with valid connection parameters

*Validations done before persisting records to database*
     
     > Regex applied
      Records from CSV file has been applied with 2 Regex patters

     > Unique and Not Null
     * Order Id
     * Customer Id
     * Product Id

     > Not Null applied on
     * ORDER_DATE
     * SHIP_DATE
     * QUANTITY
     * PROFIT
     * CUSTOMER_NAME
     * CATEGORY
     * CUSTOMER_ID

    > Date conversion
     * Converted date format from dd.MM.yy to yyyy.MM.dd standard format

*Final Database Output after above validations and date format conversion:*

    SELECT * FROM zhulkhecsvdb.store_order 	5 row(s) returned	0.000 sec / 0.000 sec
    ID, ORDER_ID, ORDER_DATE, SHIP_DATE, SHIP_MODE, QUANTITY, DISCOUNT, PROFIT, PRODUCT_ID, CUSTOMER_NAME, CATEGORY, CUSTOMER_ID, PRODUCT_NAME
    '1', 'CA-2016-138688', '2016-06-12', '2016-06-16', 'Second Class', '2', '0.00', '6.87', 'OFF-LA-10000240', 'Darrin Van Huff', 'Office Supplies', 'DV-13045', 'Self-Adhesive Address Labels for Typewriters by Universal'
    '2', 'US-2015-108966', '2015-10-11', '2015-10-18', 'Standard Class', '5', '0.45', '-383.03', 'FUR-TA-10000577', 'Sean O\'Donnell', 'Furniture', 'SO-20335', 'Bretford CR4500 Series Slim Rectangular Table'
    '3', 'CA-2014-115812', '2014-06-09', '2014-06-14', 'Standard Class', '7', '0.00', '14.17', 'FUR-FU-10001487', 'Brosina Hoffman', 'Furniture', 'BH-11710', '\"Eldon Expressions Wood and Plastic Desk Accessories Cherry Wood\"'
    '4', 'CA-2015-117415', '2015-12-27', '2015-12-31', 'Standard Class', '9', '0.20', '35.42', 'OFF-EN-10002986', 'Steve Nguyen', 'Office Supplies', 'SN-20710', '\"#10-4 1/8\"\" x 9 1/2\"\" Premium Diagonal Seam Envelopes\"'
    '11', 'CA-2016-152156', '2016-11-08', '2016-11-11', 'Second Class', '2', '0.00', '41.91', 'FUR-BO-10001798', 'Claire Gute', 'Furniture', 'CG-12520', 'Bush Somerset Collection Bookcase'


          




