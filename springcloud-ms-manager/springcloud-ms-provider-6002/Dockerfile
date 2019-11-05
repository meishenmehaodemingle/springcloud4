FROM store/oracle/serverjre:8
MAINTAINER hello@163.com
RUN mkdir -p /datas
WORKDIR /datas
COPY springcloud-ms-provider-6002-1.0-SNAPSHOT.jar ./
COPY wait-for-it.sh ./
EXPOSE 6002