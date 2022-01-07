
FROM adoptopenjdk/openjdk13:jdk-13.0.2_8-alpine-slim

COPY . /admios-flix

ARG SBT_VERSION=1.4.1
ARG DOCKER_VERSION="20.10.3"
ARG WGET_VERSION="1.20.3"
ARG BASH_VERSION="5.0.17"


ARG CA_CERTIFICATES_VERSION="20191127"

RUN apk --no-cache add wget~=${WGET_VERSION} ca-certificates~=${CA_CERTIFICATES_VERSION} bash~=${BASH_VERSION} docker-cli~=${DOCKER_VERSION} && update-ca-certificates && rm -rf /var/cache/apk/*

SHELL ["/bin/bash", "-o", "pipefail", "-c"]
ARG WORK_DIR=/opt
ARG SBT_CACHE_DIR=${WORK_DIR}/sbt_cache


# Install SBT
RUN wget -qcO- https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz | tar -xz -C /opt \
    && ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt \
    && chmod +x /usr/local/bin/sbt \
    && mkdir -p ${WORK_DIR}
    
ENV SBT_OPTS="-Dsbt.global.base=${SBT_CACHE_DIR}/.sbt -Dsbt.boot.directory=${SBT_CACHE_DIR}/.sbt/boot -Dsbt.ivy.home=${SBT_CACHE_DIR}/ivy -Xmx2048M -Xss2M " \
    COURSIER_CACHE=${SBT_CACHE_DIR}/coursier  \
    JAVA_OPTS="-XX:+UseContainerSupport -Dfile.encoding=UTF-8"
WORKDIR /admios-flix


CMD [ "sbt", "docker:publishLocal" ]