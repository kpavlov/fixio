fixio - FIX Protocol Support for Netty [![Build Status](https://travis-ci.org/kpavlov/fixio.png?branch=master)](https://travis-ci.org/kpavlov/fixio)
=====

# Overview #

## Why One More FIX Protocol API

This API is intended to replace well known [QuickFIX/J][quickfix] in high-frequency trading scenarios.

## Design goals

1. Implement [FIX Protocol][fixprotocol] Java API with as low memory footprint as possible in order to eliminate unnecessary GC overhead,
thus improving overall application performance under high load.
2. Provide [FIX Protocol][fixprotocol] Codecs for [Netty][netty], to make it possible to get rid of Apache [Mina][mina] which is used by [QuickFIX/J][quickfix] as a transport layer.
3. Avoid using expensive operations:
     - Avoid synchronization.
     - Replace BigDecimals with custom [Fixed Point Number][FixedPointNumber] implementation for financial data.
     - Reuse java.util.Calendar and java.util.TimeZone instances.

The API has a number of [limitations](#Limitations), so it may be not suitable for any FIX application.

## Limitations

1. Logon message encryption is not supported. EncryptMethod(98)=0
2. XmlData is not supported
3. Message encodings other than US-ASCII are not supported.
4. ...

## Performance

Currently fixio can beat QuickFix performance in simple scenario. See [performance comparison](https://github.com/kpavlov/fixio/wiki/Performance-Comparison).

# Getting Started

1. [Download ZIP archive](archive/master.zip) or clone/fork the repository.
2. Build and install project artifacts to your local maven repository:
`mvn clean install`
3. Add the dependency to your project

~~~~~~~~~xml
<dependency>
    <groupId>kpavlov.fixio</groupId>
    <artifactId>core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
~~~~~~~~~

You'll also need a slf4j API implementation at runtime, so please add appropriate dependency, e.g.:

~~~~~~~~~xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.5</version>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
~~~~~~~~~

## Examples

You may find working example of [client][client-example]
and [server][server-example] applications in module ["examples"][examples-module].

I recommend running server with Concurrent Mark Sweep Collector enabled: `-XX:+UseConcMarkSweepGC`
and increased Survivor spaces (`-XX:SurvivorRatio=4`).

## Writing Simple FIX Client

To create a simple FIX client you need to:

1. Implement [FixApplication][FixApplication].
   You may extend [FixApplicationAdapter][FixApplicationAdapter] as a quick start.

2. Create an instance of [FixClient][FixClient] and initialize if with [FixApplication][FixApplication] you've just created and classpath reference to FIX session settings property file.

3. Invoke `FixClient.connect(host, port)` to initiate connection.
   Method `connect(...)` returns a [ChannelFeature][ChannelFeature] which which will be notified when a channel is closed,
    so you may invoke the method `sync()` on it if you wish to wait for connection to be closed.

~~~~~~~~~java
FixApplication app = new FixApplicationAdapter();
client = new FixClient(app);

// set settings file location related to classpath
client.setSettingsResource("/client.properties");

// connect to specified host and port
ChannelFeature closeFeature = client.connect("localhost", 10201);

// wait until FIX Session is closed
closeFeature.sync();

// Shutdown FIX client
client.disconnect();
~~~~~~~~~

You may find more information in [User Guide](https://github.com/kpavlov/fixio/wiki/User-Guide) and
[Wiki pages](https://github.com/kpavlov/fixio/wiki).

[FixedPointNumber]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/fixprotocol/fields/FixedPointNumber.java
[FixApplication]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/handlers/FixApplication.java
[FixApplicationAdapter]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/handlers/FixApplicationAdapter.java
[FixClient]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/FixClient.java
[FixServer]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/FixServer.java
[FixAuthenticator]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/netty/pipeline/server/FixAuthenticator.java
[AcceptAllAuthenticator]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/netty/pipeline/server/AcceptAllAuthenticator.java
[ChannelFeature]: http://netty.io/5.0/api/io/netty/channel/ChannelFuture.html

[FixMessage]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/fixprotocol/FixMessage.java
[FixMessageBuilder]: https://github.com/kpavlov/fixio/treemaster/core/src/main/java/fixio/fixprotocol/FixMessageBuilder.java

[client-example]: https://github.com/kpavlov/fixio/treemaster/examples/src/main/java/fixio/examples/priceclient
[server-example]: https://github.com/kpavlov/fixio/treemaster/examples/src/main/java/fixio/examples/priceserver
[examples-module]: https://github.com/kpavlov/fixio/treemaster/examples
[quickfix]: http://www.quickfixj.org/ "Java Open Source FIX Engine"
[mina]: http://directory.apache.org/subprojects/mina/ "Apache Mina"
[netty]: http://netty.io/ "Netty"
[fixprotocol]: http://www.fixprotocol.org/ "FIX Protocol"
