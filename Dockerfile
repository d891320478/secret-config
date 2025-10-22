FROM registry.cn-hangzhou.aliyuncs.com/htdong/ubuntu:24.04-with-jdk17.0.16

USER root

ENV MEMORY=512
ENV LOG_PATH=/data/logs/secret-config

RUN mkdir -p /root/secret-config

WORKDIR /root/secret-config

ADD secret-config-web/target/secret-config.jar /root/secret-config/secret-config.jar
ADD run.sh /root/secret-config/run.sh

CMD ["sh", "run.sh"]
