# You can also pull these images from DockerHub amazon/aws-lambda-java:25
FROM public.ecr.aws/lambda/java:25 AS base

FROM maven:4.0.0-rc-5-amazoncorretto-25 AS build
WORKDIR /build
COPY pom.xml .
COPY src src
COPY test test

ARG TESTS_ENABLE=1
RUN if [ "${TESTS_ENABLE}" = "1" ]; then \
      mvn -B verify; \
    else \
      mvn -B package -DskipTests; \
    fi

FROM base AS final
WORKDIR ${LAMBDA_TASK_ROOT}
COPY --from=build /build/src/redshirt-example-sqs-lambda/target/redshirt-example-sqs-lambda-1.0.0-SNAPSHOT.jar ${LAMBDA_TASK_ROOT}/lib/

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "com.redshirt.example.sqslambda.Function::handleRequest" ]
