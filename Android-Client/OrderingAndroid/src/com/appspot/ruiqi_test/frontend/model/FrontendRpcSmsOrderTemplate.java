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
 * Model definition for FrontendRpcSmsOrderTemplate.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the frontend. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class FrontendRpcSmsOrderTemplate extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("product_amounts")
  private java.util.List<java.lang.Double> productAmounts;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("product_ids")
  private java.util.List<java.lang.String> productIds;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("summery_cn")
  private java.lang.String summeryCn;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("summery_en")
  private java.lang.String summeryEn;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public FrontendRpcSmsOrderTemplate setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Double> getProductAmounts() {
    return productAmounts;
  }

  /**
   * @param productAmounts productAmounts or {@code null} for none
   */
  public FrontendRpcSmsOrderTemplate setProductAmounts(java.util.List<java.lang.Double> productAmounts) {
    this.productAmounts = productAmounts;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getProductIds() {
    return productIds;
  }

  /**
   * @param productIds productIds or {@code null} for none
   */
  public FrontendRpcSmsOrderTemplate setProductIds(java.util.List<java.lang.String> productIds) {
    this.productIds = productIds;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSummeryCn() {
    return summeryCn;
  }

  /**
   * @param summeryCn summeryCn or {@code null} for none
   */
  public FrontendRpcSmsOrderTemplate setSummeryCn(java.lang.String summeryCn) {
    this.summeryCn = summeryCn;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSummeryEn() {
    return summeryEn;
  }

  /**
   * @param summeryEn summeryEn or {@code null} for none
   */
  public FrontendRpcSmsOrderTemplate setSummeryEn(java.lang.String summeryEn) {
    this.summeryEn = summeryEn;
    return this;
  }

  @Override
  public FrontendRpcSmsOrderTemplate set(String fieldName, Object value) {
    return (FrontendRpcSmsOrderTemplate) super.set(fieldName, value);
  }

  @Override
  public FrontendRpcSmsOrderTemplate clone() {
    return (FrontendRpcSmsOrderTemplate) super.clone();
  }

}