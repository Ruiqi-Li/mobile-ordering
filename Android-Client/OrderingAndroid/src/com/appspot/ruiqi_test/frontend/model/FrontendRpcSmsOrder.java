/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-07-22 21:53:01 UTC)
 * on 2014-10-14 at 01:48:25 UTC 
 * Modify at your own risk.
 */

package com.appspot.ruiqi_test.frontend.model;

/**
 * Model definition for FrontendRpcSmsOrder.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the frontend. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class FrontendRpcSmsOrder extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("change_id") @com.google.api.client.json.JsonString
  private java.lang.Long changeId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("confirm_date") @com.google.api.client.json.JsonString
  private java.lang.Long confirmDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("create_date") @com.google.api.client.json.JsonString
  private java.lang.Long createDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("customer_id")
  private java.lang.String customerId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("deliver_date") @com.google.api.client.json.JsonString
  private java.lang.Long deliverDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> descriptions;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("finish_date") @com.google.api.client.json.JsonString
  private java.lang.Long finishDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("human_id")
  private java.lang.String humanId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<FrontendRpcSmsOperate> operates;

  static {
    // hack to force ProGuard to consider FrontendRpcSmsOperate used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(FrontendRpcSmsOperate.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("order_items")
  private java.util.List<FrontendRpcSmsOrderItem> orderItems;

  static {
    // hack to force ProGuard to consider FrontendRpcSmsOrderItem used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(FrontendRpcSmsOrderItem.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String state;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("target_date") @com.google.api.client.json.JsonString
  private java.lang.Long targetDate;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getChangeId() {
    return changeId;
  }

  /**
   * @param changeId changeId or {@code null} for none
   */
  public FrontendRpcSmsOrder setChangeId(java.lang.Long changeId) {
    this.changeId = changeId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getConfirmDate() {
    return confirmDate;
  }

  /**
   * @param confirmDate confirmDate or {@code null} for none
   */
  public FrontendRpcSmsOrder setConfirmDate(java.lang.Long confirmDate) {
    this.confirmDate = confirmDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreateDate() {
    return createDate;
  }

  /**
   * @param createDate createDate or {@code null} for none
   */
  public FrontendRpcSmsOrder setCreateDate(java.lang.Long createDate) {
    this.createDate = createDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCustomerId() {
    return customerId;
  }

  /**
   * @param customerId customerId or {@code null} for none
   */
  public FrontendRpcSmsOrder setCustomerId(java.lang.String customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getDeliverDate() {
    return deliverDate;
  }

  /**
   * @param deliverDate deliverDate or {@code null} for none
   */
  public FrontendRpcSmsOrder setDeliverDate(java.lang.Long deliverDate) {
    this.deliverDate = deliverDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getDescriptions() {
    return descriptions;
  }

  /**
   * @param descriptions descriptions or {@code null} for none
   */
  public FrontendRpcSmsOrder setDescriptions(java.util.List<java.lang.String> descriptions) {
    this.descriptions = descriptions;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getFinishDate() {
    return finishDate;
  }

  /**
   * @param finishDate finishDate or {@code null} for none
   */
  public FrontendRpcSmsOrder setFinishDate(java.lang.Long finishDate) {
    this.finishDate = finishDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getHumanId() {
    return humanId;
  }

  /**
   * @param humanId humanId or {@code null} for none
   */
  public FrontendRpcSmsOrder setHumanId(java.lang.String humanId) {
    this.humanId = humanId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public FrontendRpcSmsOrder setId(java.lang.String id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<FrontendRpcSmsOperate> getOperates() {
    return operates;
  }

  /**
   * @param operates operates or {@code null} for none
   */
  public FrontendRpcSmsOrder setOperates(java.util.List<FrontendRpcSmsOperate> operates) {
    this.operates = operates;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<FrontendRpcSmsOrderItem> getOrderItems() {
    return orderItems;
  }

  /**
   * @param orderItems orderItems or {@code null} for none
   */
  public FrontendRpcSmsOrder setOrderItems(java.util.List<FrontendRpcSmsOrderItem> orderItems) {
    this.orderItems = orderItems;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getState() {
    return state;
  }

  /**
   * @param state state or {@code null} for none
   */
  public FrontendRpcSmsOrder setState(java.lang.String state) {
    this.state = state;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTargetDate() {
    return targetDate;
  }

  /**
   * @param targetDate targetDate or {@code null} for none
   */
  public FrontendRpcSmsOrder setTargetDate(java.lang.Long targetDate) {
    this.targetDate = targetDate;
    return this;
  }

  @Override
  public FrontendRpcSmsOrder set(String fieldName, Object value) {
    return (FrontendRpcSmsOrder) super.set(fieldName, value);
  }

  @Override
  public FrontendRpcSmsOrder clone() {
    return (FrontendRpcSmsOrder) super.clone();
  }

}
