# Multi-stage build for the review and report service (M0-T4). The build context is the
# repo root (docker build -f services/review/Dockerfile .) so the Gradle build can see libs/
# and the other modules.

# --- build stage: full JDK + the repo, produce the boot jar ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
# Invoke the wrapper through sh so the build never depends on gradlew's executable bit,
# which is not preserved when the build context is created on Windows.
RUN sh ./gradlew --no-daemon :services:review:bootJar

# --- runtime stage: JRE only + the boot jar, non-root ---
FROM eclipse-temurin:21-jre AS runtime
# Non-root runtime user; the image carries only the boot jar.
RUN useradd --system --uid 10001 --create-home --home-dir /app --shell /usr/sbin/nologin app
WORKDIR /app
COPY --from=build /workspace/services/review/build/libs/review.jar app.jar
USER app
EXPOSE 8083
# Healthcheck hits /health over the loopback using bash's /dev/tcp (no extra packages).
HEALTHCHECK --interval=10s --timeout=5s --start-period=40s --retries=6 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8083; printf "GET /health HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n" >&3; grep -q "\"status\":\"UP\"" <&3'
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
