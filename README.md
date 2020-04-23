# Spring Boot Community

## Overview
This full stack web application is designed to provide developers with a technical exchange platform. Normal users will be able to 
send new posts, comment on other users' posts and comments, like other people's posts and comments, send private messages to others, follow 
others, and search for posts. Administrator and moderators have a few additional authorities, such as change the status of a 
post, delete a post, and monitor the number of daily active users and unique visitors.


## Local Installation
Please make sure you have `Java 12`, `git`, `Redis`, `Kafka`, `Elasticsearch`, `MySQL`, and `Maven` installed before running the steps below. <br><br>
<b>Clone the repository to your local machine</b> <br>
```git clone https://github.com/makwingchi/spring-boot-community``` <br><br>
<b>Run Redis via command line</b> <br>
```>redis-cli``` <br><br>
<b>Start a Zookeeper and Kafka server</b> <br>
```>cd kafka_2.x-2.y.z``` <br>
```>bin\windows\zookeeper-server-start.bat config\zookeeper.properties``` <br>
```>bin\windows\kafka-server-start.bat config\server.properties``` <br><br>
<b>Create topics</b> <br>
```>cd \bin\windows``` <br>
```>kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic comment``` <br>
```>kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic follow``` <br>
```>kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic like``` <br>
```>kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic publish``` <br>
```>kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic delete``` <br><br>
<b>Start a Elasticsearch server</b><br>
```>cd elasticsearch-x.y.z\bin``` <br>
```>elasticsearch.bat``` <br><br>
<b>Run the SQL scripts in the sql folder using MySQL in the following order:</b> init_schema -> init_data -> tables_mysql_innodb<br><br>
<b>Start the application</b> <br>
`>cd community` <br>
`>mvn spring-boot:run`<br>

## Tech Stack
- `Spring Boot`
- `Spring MVC`
- `Spring Security`
- `Quartz`
- `Redis`
- `Caffeine`
- `Kafka`
- `Elasticsearch`
- `MyBatis`
- `MySQL`
- `Tomcat`
- `Maven`
- `Thymeleaf`
- `HTML/CSS`
- `Javascript`

## TODO
- [ ] Transform DAO layer implementation from MyBatis into Spring Data JPA or Hibernate
- [ ] Change Thymeleaf template engine to a React or Angular-based frontend
- [ ] Add load balancer to improve the distribution of workloads under high concurrency
- [ ] ...