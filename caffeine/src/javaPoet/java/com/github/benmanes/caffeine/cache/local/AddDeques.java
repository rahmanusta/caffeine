/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.caffeine.cache.local;

import static com.github.benmanes.caffeine.cache.Specifications.ACCESS_ORDER_DEQUE;
import static com.github.benmanes.caffeine.cache.Specifications.WRITE_ORDER_DEQUE;

import com.github.benmanes.caffeine.cache.Feature;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class AddDeques extends LocalCacheRule {

  @Override
  protected boolean applies() {
    return true;
  }

  @Override
  protected void execute() {
    addAccessOrderEdenDeque();
    addAccessOrderMainDeque();
    addWriteOrderDeque();
  }

  private void addAccessOrderEdenDeque() {
    if (Feature.usesAccessOrderEdenDeque(context.parentFeatures)
        || !Feature.usesAccessOrderEdenDeque(context.generateFeatures)) {
      return;
    }
    addDeque(ACCESS_ORDER_DEQUE, "accessOrderEdenDeque");
  }

  private void addAccessOrderMainDeque() {
    if (Feature.usesAccessOrderMainDeque(context.parentFeatures)
        || !Feature.usesAccessOrderMainDeque(context.generateFeatures)) {
      return;
    }
    addDeque(ACCESS_ORDER_DEQUE, "accessOrderMainDeque");
  }

  private void addWriteOrderDeque() {
    if (Feature.usesWriteOrderDeque(context.parentFeatures)
        || !Feature.usesWriteOrderDeque(context.generateFeatures)) {
      return;
    }
    addDeque(WRITE_ORDER_DEQUE, "writeOrderDeque");
  }

  private void addDeque(TypeName type, String name) {
    context.constructor.addStatement("this.$L = new $T()", name, type);
    context.cache.addField(FieldSpec.builder(type, name, privateFinalModifiers).build());
    context.cache.addMethod(MethodSpec.methodBuilder(name)
        .addModifiers(protectedFinalModifiers)
        .addStatement("return " + name)
        .returns(type)
        .build());
  }
}
