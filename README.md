# RxEventSource: Reactive EventSource for Java

Implementation of the [ServerSentEvents Protocol][sse] based on
[RxJava][rxjava].

## Build

To build:

```bash
$ git clone git@github.com:28reasons/RxEventSource.git
$ cd RxEventSource/
$ ./gradlew build
```

## Sample usage

To subscribe to an event stream:

```java
subscription = new EventSource().connect(uri)
    .subscribeOn(Schedulers.newThread())
    .flatMap(event -> {
        try {
            JSONObject json = new JSONObject(event.getData());
            return Observable.just(new Tweet(json));
        } catch (JSONException e) {
            return Observable.error(e);
        }
    })
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(tweet -> {
        tweets.add(tweet);
        notifyItemInserted(tweets.size() - 1);
    }, throwable -> {
        Log.e("RxEventSource", "Could not subscribe to stream", throwable);
    });
```

To unsubscribe:

```java
subscription.unsubscribe();
```

## Bugs and Feedback

For bugs, feature requests, and discussion please use [GitHub Issues][issues].

## LICENSE

    Copyright 2016 28 Reasons

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[rxjava]: https://github.com/ReactiveX/RxJava
[sse]: http://www.w3schools.com/html/html5_serversentevents.asp
[issues]: https://github.com/28Reasons/RxEventSource/issues