# Start with a base image
FROM eclipse-temurin:17-jdk-jammy

# Create directories in a single layer
ARG ALFABANK_REPORTS=/alfabank_reports
ARG TINKOFF_REPORTS=/tinkoff_reports
ARG GPB_REPORTS=/gpb_reports

RUN mkdir ${ALFABANK_REPORTS} \
    && mkdir ${TINKOFF_REPORTS} \
    && mkdir ${GPB_REPORTS} \
    && mkdir /cacerts

# Define volumes
VOLUME /tmp
VOLUME ${ALFABANK_REPORTS}
VOLUME ${TINKOFF_REPORTS}
VOLUME ${GPB_REPORTS}
VOLUME /cacerts

# Copy necessary files
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

# Set environment variables
ENV JAVA_OPTS=""

# Expose necessary ports
EXPOSE 8080

# Copy entrypoint script and make it executable
COPY /scripts/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Set the entrypoint
ENTRYPOINT ["/entrypoint.sh"]