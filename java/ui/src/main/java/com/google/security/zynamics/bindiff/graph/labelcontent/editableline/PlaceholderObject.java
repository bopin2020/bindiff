// Copyright 2011-2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.security.zynamics.bindiff.graph.labelcontent.editableline;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.security.zynamics.bindiff.enums.EPlaceholderType;
import com.google.security.zynamics.zylib.gui.zygraph.realizers.ECommentPlacement;

public class PlaceholderObject extends AbstractEditableLineObject {
  private final EPlaceholderType placeholderType;

  public PlaceholderObject(final EPlaceholderType placeholderType) {
    super(0, 0);
    this.placeholderType = checkNotNull(placeholderType);
  }

  @Override
  public Object getPersistentModel() {
    return null;
  }

  public EPlaceholderType getPlaceholderType() {
    return placeholderType;
  }

  @Override
  public boolean isPlaceholder() {
    return true;
  }

  @Override
  public boolean update(final String newContent) {
    return false;
  }

  @Override
  public boolean updateComment(final String newContent, final ECommentPlacement placement) {
    return false;
  }
}
