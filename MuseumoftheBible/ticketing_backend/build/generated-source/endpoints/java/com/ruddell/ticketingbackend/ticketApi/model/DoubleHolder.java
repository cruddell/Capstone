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
 * (build: 2016-07-08 17:28:43 UTC)
 * on 2016-08-05 at 12:16:25 UTC 
 * Modify at your own risk.
 */

package com.ruddell.ticketingbackend.ticketApi.model;

/**
 * Model definition for DoubleHolder.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the ticketApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class DoubleHolder extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double data;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getData() {
    return data;
  }

  /**
   * @param data data or {@code null} for none
   */
  public DoubleHolder setData(java.lang.Double data) {
    this.data = data;
    return this;
  }

  @Override
  public DoubleHolder set(String fieldName, Object value) {
    return (DoubleHolder) super.set(fieldName, value);
  }

  @Override
  public DoubleHolder clone() {
    return (DoubleHolder) super.clone();
  }

}
