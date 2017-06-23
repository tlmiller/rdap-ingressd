# Overview
The following document contains information for building *rdap-ingressd* from
both source and as a Docker container.

# Requirements
The following requirements need to be available in order to build the project.

- [Git](https://git-scm.com/)

From Source:

- Java 8 or higher. Supports both Oracle JDK and OpenJDK
- [Maven](https://maven.apache.org/) 3.5 or higher

Docker Container:

- [Docker CE](https://www.docker.com/community-edition) 17 or higher

# Obtaining The Source Code

The first step in building *rdap-ingressd* is to obtain the source code with
git.

```
git clone https://github.com/APNIC-net/rdap-ingressd
```

# Building & Running From Source

## Building
*rdap-ingressd* is built using maven. To create a new build of the project
please run the following maven command.

```
mvn package
```

The projects jars have now been created and can be executed.

## Running
The project can be executed in one of two ways. The first is through maven using
spring-boot or by executing the create jar directly with java.

Executing with maven:

```
mvn spring-boot:run
```

Executing with java:

```
java -jar target/rdap-ingressd-<version>.jar
```
Where <version> is the version of the project that has been checkout with git.

*rdap-ingressd* is now listening and available on port 8080.

# Building & Running With Docker

## Building
Use the following command to build a docker image of *rdap-ingressd*

```
docker build . -t apnic/rdap-ingressd
```

## Running
The created docker image can now be executed with the following:

```
docker run -p 8080:8080 apnic/rdap-ingressd
```

*rdap-ingressd* is now listening and available on port 8080.

See the [deploy](deploy.md) documentation for more detailed instructions on
deploying the Docker image.