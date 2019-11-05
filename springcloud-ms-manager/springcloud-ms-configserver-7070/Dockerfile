FROM store/oracle/serverjre:8
MAINTAINER hello@163.com
RUN mkdir -p /datas
WORKDIR /datas

#将jar包拷贝到 /datas目录
COPY springcloud-ms-configserver-7070-1.0-SNAPSHOT.jar ./

COPY wait-for-it.sh ./
EXPOSE 7070