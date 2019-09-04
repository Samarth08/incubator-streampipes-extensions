/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.enricher.jvm.processor.sizemeasure;

import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SizeMeasure implements EventProcessor<SizeMeasureParameters> {

  private SizeMeasureParameters sizeMeasureParameters;

  @Override
  public void onInvocation(SizeMeasureParameters sizeMeasureParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    this.sizeMeasureParameters = sizeMeasureParameters;
  }

  @Override
  public void onDetach() {
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) {
    try {
      double size = getSizeInBytes(event.getRaw());
      if (sizeMeasureParameters.getSizeUnit().equals(SizeMeasureController.KILOBYTE_SIZE)) {
        size /= 1024;
      } else if (sizeMeasureParameters.getSizeUnit().equals(SizeMeasureController.MEGABYTE_SIZE)) {
        size /= 1048576;
      }
      event.addField(SizeMeasureController.EVENT_SIZE, size);
      spOutputCollector.collect(event);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private int getSizeInBytes(Object map) throws IOException {
    // Measuring the size by serializing it and then measuring the bytes
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteStream);

    out.writeObject(map);
    out.close();

    return byteStream.toByteArray().length;
  }
}
