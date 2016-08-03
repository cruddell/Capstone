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
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-05-27 16:00:31 UTC)
 * on 2016-08-03 at 01:05:48 UTC 
 * Modify at your own risk.
 */

package com.ruddell.ticketingbackend.ticketApi.model;

/**
 * Model definition for PurchaseObject.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the ticketApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class PurchaseObject extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String cardNumber;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String cvc;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String email;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String expiration;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String firstName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String lastName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfTickets;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberTickets;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String phone;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCardNumber() {
    return cardNumber;
  }

  /**
   * @param cardNumber cardNumber or {@code null} for none
   */
  public PurchaseObject setCardNumber(java.lang.String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCvc() {
    return cvc;
  }

  /**
   * @param cvc cvc or {@code null} for none
   */
  public PurchaseObject setCvc(java.lang.String cvc) {
    this.cvc = cvc;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmail() {
    return email;
  }

  /**
   * @param email email or {@code null} for none
   */
  public PurchaseObject setEmail(java.lang.String email) {
    this.email = email;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getExpiration() {
    return expiration;
  }

  /**
   * @param expiration expiration or {@code null} for none
   */
  public PurchaseObject setExpiration(java.lang.String expiration) {
    this.expiration = expiration;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName firstName or {@code null} for none
   */
  public PurchaseObject setFirstName(java.lang.String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLastName() {
    return lastName;
  }

  /**
   * @param lastName lastName or {@code null} for none
   */
  public PurchaseObject setLastName(java.lang.String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfTickets() {
    return numberOfTickets;
  }

  /**
   * @param numberOfTickets numberOfTickets or {@code null} for none
   */
  public PurchaseObject setNumberOfTickets(java.lang.Integer numberOfTickets) {
    this.numberOfTickets = numberOfTickets;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberTickets() {
    return numberTickets;
  }

  /**
   * @param numberTickets numberTickets or {@code null} for none
   */
  public PurchaseObject setNumberTickets(java.lang.Integer numberTickets) {
    this.numberTickets = numberTickets;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhone() {
    return phone;
  }

  /**
   * @param phone phone or {@code null} for none
   */
  public PurchaseObject setPhone(java.lang.String phone) {
    this.phone = phone;
    return this;
  }

  @Override
  public PurchaseObject set(String fieldName, Object value) {
    return (PurchaseObject) super.set(fieldName, value);
  }

  @Override
  public PurchaseObject clone() {
    return (PurchaseObject) super.clone();
  }

}