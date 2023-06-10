FROM openjdk:17-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -XshowSettings:vm -XX:MaxRAMFraction=1 -XX:MaxRAMPercentage=80.0 -XX:+PrintFlagsFinal -cp "app:app/lib/*" ru.vzotov.accounting.Application

ARG ALFABANK_REPORTS=/alfabank_reports
RUN mkdir ${ALFABANK_REPORTS}
VOLUME ${ALFABANK_REPORTS}

ARG TINKOFF_REPORTS=/tinkoff_reports
RUN mkdir ${TINKOFF_REPORTS}
VOLUME ${TINKOFF_REPORTS}

ARG GPB_REPORTS=/gpb_reports
RUN mkdir ${GPB_REPORTS}
VOLUME ${GPB_REPORTS}
