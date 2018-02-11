// Copyright 2017 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.cmdline;

import com.google.devtools.build.lib.skyframe.serialization.DeserializationContext;
import com.google.devtools.build.lib.skyframe.serialization.ObjectCodec;
import com.google.devtools.build.lib.skyframe.serialization.SerializationContext;
import com.google.devtools.build.lib.skyframe.serialization.SerializationException;
import com.google.devtools.build.lib.skyframe.serialization.strings.StringCodecs;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;

/** Custom serialization logic for {@link Label}s. */
public class LabelCodec implements ObjectCodec<Label> {
  public static final LabelCodec INSTANCE = new LabelCodec();

  // TODO(michajlo): Share single instance of package id codec among all the codecs.
  private final PackageIdentifierCodec packageIdCodec = new PackageIdentifierCodec();
  private final ObjectCodec<String> stringCodec = StringCodecs.asciiOptimized();

  @Override
  public Class<Label> getEncodedClass() {
    return Label.class;
  }

  @Override
  public void serialize(SerializationContext context, Label label, CodedOutputStream codedOut)
      throws IOException, SerializationException {
    packageIdCodec.serialize(context, label.getPackageIdentifier(), codedOut);
    stringCodec.serialize(context, label.getName(), codedOut);
  }

  @Override
  public Label deserialize(DeserializationContext context, CodedInputStream codedIn)
      throws SerializationException, IOException {
    PackageIdentifier packageId = packageIdCodec.deserialize(context, codedIn);
    String name = stringCodec.deserialize(context, codedIn);
    return Label.createUnvalidated(packageId, name);
  }
}
