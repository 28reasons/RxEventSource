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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;

public class EventSource {
    private static final int DEFAULT_BUFFER_SIZE = 20 * 1024;

    private int bufferSize;

    public EventSource() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public EventSource(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Observable<Event> connect(String uri) {
        return Observable.defer(() -> {
            try {
                URL url = new URL(uri);
                return connect(url);
            } catch (MalformedURLException e) {
                return Observable.error(e);
            }
        });
    }

    public Observable<Event> connect(URL uri) {
        return Observable.defer(() -> {
            final EventParser parser = new EventParser(bufferSize);
            final AtomicBoolean running = new AtomicBoolean(true);

            Observable<Event> streamObservable = Observable.create(subscriber -> {
                InputStream stream = null;

                try {
                    stream = uri.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                    String line;

                    while(running.get() && (line = reader.readLine()) != null) {
                        Event event = parser.readLine(line);
                        if (event != null && running.get()) {
                            subscriber.onNext(event);
                        }
                    }

                    subscriber.onCompleted();
                } catch (InterruptedIOException e) {
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                } finally {
                    if(stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            // Ignore
                        }
                    }
                }
            });

            streamObservable = streamObservable.doOnUnsubscribe(() -> {
                running.set(false);
            });

            return streamObservable;
        });
    }
}