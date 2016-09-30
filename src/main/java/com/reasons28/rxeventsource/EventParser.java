/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reasons28.rxeventsource;

import java.util.HashMap;

class EventParser {
    private static final String KEY_EVENT = "event";
    private static final String KEY_DATA = "data";
    private static final String KEY_ID = "id";
    private static final String DEFAULT_EVENT = "message";
    private static final String CHAR_COMMENT = ":";
    private static final String CHAR_SEPARATOR = ":";

    private HashMap<String, StringBuilder> buffers;

    EventParser(int bufferSize) {
        buffers = new HashMap<>();
        buffers.put(KEY_EVENT, new StringBuilder(1024));
        buffers.put(KEY_DATA, new StringBuilder(bufferSize));
        buffers.put(KEY_ID, new StringBuilder(1024));
    }

    Event readLine(String line) {
        String trimmed = line.trim();

        if(trimmed.equals(CHAR_COMMENT)) {
            // Ignore
            return null;
        } else if(trimmed.isEmpty()) {
            // Finished
            StringBuilder eventBuffer = buffers.get(KEY_EVENT);
            StringBuilder idBuffer = buffers.get(KEY_ID);
            StringBuilder dataBuffer = buffers.get(KEY_DATA);

            Event event = new Event(
                    eventBuffer.length() == 0 ? DEFAULT_EVENT : eventBuffer.toString(),
                    idBuffer.toString(),
                    dataBuffer.toString()
            );

            reset();

            return event;
        } else {
            String[] parts = trimmed.split(CHAR_SEPARATOR, 2);
            if(parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();

                StringBuilder buffer = this.buffers.get(key);
                if(buffer != null) {
                    buffer.append(value);
                }
            }
            return null;
        }
    }

    void reset() {
        for (StringBuilder builder : buffers.values()) {
            builder.setLength(0);
        }
    }
}