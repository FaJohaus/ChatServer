FROM openjdk:11

WORKDIR /the/workdir/path

ADD ./ ./

RUN javac -cp ./src/ -encoding utf8 ./src/com/muc/ServerMain.java

ENV PORT=8080

ENTRYPOINT [ "java","-classpath","./src/",":","./lib/*.jar", "com.muc.ServerMain" ]