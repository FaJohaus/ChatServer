FROM openjdk:11

WORKDIR /the/workdir/path

ADD ./ ./

RUN javac -cp ./lib/:./src/ -encoding utf8 ./src/com/muc/ServerMain.java

ENV PORT=8080

ENTRYPOINT [ "java","-classpath","./src/", "com.muc.ServerMain" ]