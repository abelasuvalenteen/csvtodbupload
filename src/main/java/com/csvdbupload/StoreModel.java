package com.csvdbupload;

import com.mysql.cj.util.StringUtils;

/**
 * StoreModel Class
 */
public class StoreModel {
    public StoreModel(String[] storeDetails) {
        super();
        if(!(StringUtils.isNullOrEmpty(storeDetails[1])
                && StringUtils.isNullOrEmpty(storeDetails[2])
                && StringUtils.isNullOrEmpty(storeDetails[3])
                && StringUtils.isNullOrEmpty(storeDetails[18])
                && StringUtils.isNullOrEmpty(storeDetails[20])
                && StringUtils.isNullOrEmpty(storeDetails[13])
                && StringUtils.isNullOrEmpty(storeDetails[6])
                && StringUtils.isNullOrEmpty(storeDetails[14])
                && StringUtils.isNullOrEmpty(storeDetails[5]))) {
            this.orderId = storeDetails[1];
            this.orderDate = storeDetails[2];
            this.shipDate = storeDetails[3];
            this.shipMode = storeDetails[4];
            this.quantity = storeDetails[18];
            this.profit = storeDetails[20];
            this.discount = storeDetails[19];
            this.productId = storeDetails[13];
            this.customerName = storeDetails[6];
            this.category = storeDetails[14];
            this.customerId = storeDetails[5];
            this.productName = storeDetails[16];
        }
    }

    String orderId;
    String orderDate;
    String shipDate;
    String shipMode;
    String quantity;
    String discount;
    String profit;
    String productId;
    String customerName;
    String category;
    String customerId;
    String productName;

    @Override
    public String toString() {
        return "[orderId=" + orderId + ", orderDate=" + orderDate +
                ", shipDate=" + shipDate + ", shipMode=" + shipMode +
                ", quantity=" + quantity + ", discount=" + discount +
                ", profit=" + profit + ", productId=" + productId +
                ", customerName=" + customerName + ", category=" + category +
                ", customerId=" + customerId + ", productName=" + productName +"]";
    }
}
