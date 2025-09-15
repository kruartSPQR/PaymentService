ARG GITHUB_ACTOR
ARG GIT_TOKEN

FROM gradle:jdk17-alpine AS builder

WORKDIR /opt/app

COPY . .

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /opt/app/build/libs/*.jar paymentService.jar

EXPOSE 8079

ENTRYPOINT ["java", "-jar", "paymentService.jar"]

RUN GITHUB_ACTOR=${GITHUB_ACTOR} GIT_TOKEN=${GIT_TOKEN} gradle clean bootJar --no-daemon