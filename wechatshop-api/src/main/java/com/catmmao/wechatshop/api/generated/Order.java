package com.catmmao.wechatshop.api.generated;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.USER_ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.TOTAL_PRICE
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private Long totalPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.ADDRESS
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private String address;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.EXPRESS_COMPANY
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private String expressCompany;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.EXPRESS_ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private String expressId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.STATUS
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.CREATED_AT
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private Date createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order.UPDATED_AT
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private Date updatedAt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table order
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.ID
     *
     * @return the value of order.ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.ID
     *
     * @param id the value for order.ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.USER_ID
     *
     * @return the value of order.USER_ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.USER_ID
     *
     * @param userId the value for order.USER_ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.TOTAL_PRICE
     *
     * @return the value of order.TOTAL_PRICE
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public Long getTotalPrice() {
        return totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.TOTAL_PRICE
     *
     * @param totalPrice the value for order.TOTAL_PRICE
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.ADDRESS
     *
     * @return the value of order.ADDRESS
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public String getAddress() {
        return address;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.ADDRESS
     *
     * @param address the value for order.ADDRESS
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.EXPRESS_COMPANY
     *
     * @return the value of order.EXPRESS_COMPANY
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public String getExpressCompany() {
        return expressCompany;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.EXPRESS_COMPANY
     *
     * @param expressCompany the value for order.EXPRESS_COMPANY
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.EXPRESS_ID
     *
     * @return the value of order.EXPRESS_ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public String getExpressId() {
        return expressId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.EXPRESS_ID
     *
     * @param expressId the value for order.EXPRESS_ID
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setExpressId(String expressId) {
        this.expressId = expressId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.STATUS
     *
     * @return the value of order.STATUS
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.STATUS
     *
     * @param status the value for order.STATUS
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.CREATED_AT
     *
     * @return the value of order.CREATED_AT
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.CREATED_AT
     *
     * @param createdAt the value for order.CREATED_AT
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order.UPDATED_AT
     *
     * @return the value of order.UPDATED_AT
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order.UPDATED_AT
     *
     * @param updatedAt the value for order.UPDATED_AT
     *
     * @mbg.generated Sat Jul 10 17:26:46 CST 2021
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}