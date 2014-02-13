fixio - FIX Protocol Support for Netty [![Build Status](https://travis-ci.org/kpavlov/fixio.png?branch=master)](https://travis-ci.org/kpavlov/fixio)
=====

# Overview #

## Why One More FIX Protocol API ##

This API is intended to be a replacement for well known [QuickFIX/J][quickfix] library to be used for high-frequency trading cases.

### Design goals ###

1. Implement [FIX Protocol][fixprotocol] Java API with as low memory footprint as possible in order to eliminate unnecessary GC overhead,
thus improving overall application performance under high load.
2. Provide [FIX Protocol][fixprotocol] Codecs for [Netty][netty], making it possible to get rid of Apache [Mina][mina] which is used by [QuickFIX/J][quickfix] as a transport layer.

This API has a number of [limitations](#Limitations), so it may be not suitable for any FIX application.

# Getting Started #

1. [Download ZIP archive](archive/master.zip) or clone/fork the repository.
2. Build and install project artifacts to your local maven repository:
`mvn clean install`
3. Add the dependency to your project

~~~~~~~~~
<dependency>
    <groupId>kpavlov.fixio</groupId>
    <artifactId>core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
~~~~~~~~~

You'll also need a slf4j API implementation at runtime, so please add appropriate dependency, e.g.:

~~~~~~~~~
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.5</version>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
~~~~~~~~~

# Examples #

You may find working example of [client][client-example]
and [server][server-example] applications in module ["examples"][examples-module].

I recommend running server with Concurrent Mark Sweep Collector enabled: `-XX:+UseConcMarkSweepGC`.

# Limitations #

1. Logon message encryption is not supported. EncryptMethod(98)=0
2. XmlData is not supported
3. Message encodings other than US-ASCII are not supported.
4. ...

[client-example]: tree/master/examples/src/main/java/fixio/examples/priceclient
[server-example]: tree/master/examples/src/main/java/fixio/examples/priceserver
[examples-module]: tree/master/examples
[quickfix]: http://www.quickfixj.org/ "Java Open Source FIX Engine"
[mina]: http://directory.apache.org/subprojects/mina/ "Apache Mina"
[netty]: http://netty.io/ "Netty"
[fixprotocol]: http://www.fixprotocol.org/ "FIX Protocol"
