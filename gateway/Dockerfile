FROM bellsoft/liberica-openjdk-alpine-musl:11
ENV TZ=Europe/Moscow
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8787
EXPOSE 8787
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]