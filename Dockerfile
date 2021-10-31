FROM openjdk:11

WORKDIR /the/workdir/path

ADD ./ ./

RUN javac -cp ./lib/postgresql-42.3.1.jar:./lib/mysql-connector-java-8.0.26.jar:./src/ -encoding utf8 ./src/com/muc/ServerMain.java

ENV PORT=8080

ENTRYPOINT [ "java","-cp","./src/:./lib/mysql-connector-java-8.0.26.jar", "com.muc.ServerMain" ]