# Helidon Petclinic REST Service

Helidon MP version of the Spring Petclinic REST service (see [spring-petclinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest)). **There is no UI**.
The [spring-petclinic-angular project](https://github.com/spring-petclinic/spring-petclinic-angular) is an Angular front-end application which consumes the REST API.

## How to Build and Run

### Locally

With JDK21

```bash
mvn package
java -jar target/petclinic.jar
```

### With Docker

```
docker build -t petclinic .
docker run --rm -p 8080:8080 petclinic:latest
```

### Run Integration Tests

```
mvn integration-test
```


## Exercise the application

### Basics

OpenAPI REST API documentation: http://localhost:9966/openapi

To use UI, clone the [spring-petclinic-angular](https://github.com/spring-petclinic/spring-petclinic-angular) project and run it following instructions in its README.md.

### Try metrics

Prometheus Format:

```
curl -s -X GET http://localhost:9966/metrics
# TYPE base:gc_g1_young_generation_count gauge
```

JSON Format:

```
curl -H 'Accept: application/json' -X GET http://localhost:9966/metrics
```

### Try health

```
curl -s -X GET http://localhost:9966/health
```

## Building a Custom Runtime Image

Build the custom runtime image using the jlink image profile:

```
mvn package -Pjlink-image
```

This uses the helidon-maven-plugin to perform the custom image generation.
After the build completes it will report some statistics about the build including the reduction in image size.

The target/petclinic-jri directory is a self contained custom image of your application. It contains your application,
its runtime dependencies and the JDK modules it depends on. You can start your application using the provide start script:

```
./target/petclinic-jri/bin/start
```

Class Data Sharing (CDS) Archive
Also included in the custom image is a Class Data Sharing (CDS) archive that improves your application’s startup
performance and in-memory footprint. You can learn more about Class Data Sharing in the JDK documentation.

The CDS archive increases your image size to get these performance optimizations. It can be of significant size (tens of MB).
The size of the CDS archive is reported at the end of the build output.

If you’d rather have a smaller image size (with a slightly increased startup time) you can skip the creation of the CDS
archive by executing your build like this:

```
mvn package -Pjlink-image -Djlink.image.addClassDataSharingArchive=false
```

For more information on available configuration options see the helidon-maven-plugin documentation.

## Help

* See the [Helidon FAQ](https://github.com/oracle/helidon/wiki/FAQ)
* Ask questions on Stack Overflow using the [helidon tag](https://stackoverflow.com/tags/helidon)
* Join us on Slack: [#helidon-users](http://slack.helidon.io)

## Contributing

This project welcomes contributions from the community. Before submitting a pull request, please [review our contribution guide](./CONTRIBUTING.md)

## Security

Please consult the [security guide](./SECURITY.md) for our responsible security vulnerability disclosure process

## License

Copyright (c) 2017, 2024 Oracle and/or its affiliates.

Released under [Apache License 2.0](./LICENSE.txt).

