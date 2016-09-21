ispyb-logger
==================================================


How do we build it?
--------------------------------------

```bash
mvn clean install
```


How do we execute it?
--------------------------------------


```bash
java -jar target/ispyb-logger-0.0.1-SNAPSHOT.jar -i src/test/resources/ -o /tmp/json/
```

How does it do?
--------------------------------------

## It is a parser

It parses the ISPyB logs and create JSON file with the important information reducing the size of the files from hundreds of MB to few MB:

From:
```
-rw-r--r-- 1 demariaa soft  46M Sep 19 12:38 server.log
-rw-r--r-- 1 demariaa soft 110M Sep 19 12:38 server.log.2016-09-07
-rw-r--r-- 1 demariaa soft 131M Sep 19 12:38 server.log.2016-09-08
-rw-r--r-- 1 demariaa soft 118M Sep 19 12:38 server.log.2016-09-09
-rw-r--r-- 1 demariaa soft  21M Sep 19 12:38 server.log.2016-09-10
-rw-r--r-- 1 demariaa soft  51M Sep 19 12:38 server.log.2016-09-11
-rw-r--r-- 1 demariaa soft 123M Sep 19 12:38 server.log.2016-09-12
-rw-r--r-- 1 demariaa soft 133M Sep 19 12:38 server.log.2016-09-13
-rw-r--r-- 1 demariaa soft  78M Sep 19 12:38 server.log.2016-09-14
-rw-r--r-- 1 demariaa soft 131M Sep 19 12:38 server.log.2016-09-15
-rw-r--r-- 1 demariaa soft 156M Sep 19 12:38 server.log.2016-09-16
-rw-r--r-- 1 demariaa soft 105M Sep 19 12:39 server.log.2016-09-17
-rw-r--r-- 1 demariaa soft 107M Sep 19 12:39 server.log.2016-09-18
```

To:

```
-rw-r--r--  1 demariaa soft  367 Sep 21 16:02 index.json
-rw-r--r--  1 demariaa soft 9.9M Sep 21 16:02 server.log.2016-09-07.json
-rw-r--r--  1 demariaa soft 8.0M Sep 21 16:02 server.log.2016-09-08.json
-rw-r--r--  1 demariaa soft 2.7M Sep 21 16:02 server.log.2016-09-09.json
-rw-r--r--  1 demariaa soft  75K Sep 21 16:02 server.log.2016-09-10.json
-rw-r--r--  1 demariaa soft 7.9M Sep 21 16:02 server.log.2016-09-11.json
-rw-r--r--  1 demariaa soft 1.8M Sep 21 16:02 server.log.2016-09-12.json
-rw-r--r--  1 demariaa soft 4.4M Sep 21 16:02 server.log.2016-09-13.json
-rw-r--r--  1 demariaa soft 125K Sep 21 16:02 server.log.2016-09-14.json
-rw-r--r--  1 demariaa soft 5.5M Sep 21 16:02 server.log.2016-09-15.json
-rw-r--r--  1 demariaa soft  15M Sep 21 16:02 server.log.2016-09-16.json
-rw-r--r--  1 demariaa soft 4.1M Sep 21 16:02 server.log.2016-09-17.json
-rw-r--r--  1 demariaa soft 9.2M Sep 21 16:02 server.log.2016-09-18.json
-rw-r--r--  1 demariaa soft 205K Sep 21 16:02 server.log.json

```

