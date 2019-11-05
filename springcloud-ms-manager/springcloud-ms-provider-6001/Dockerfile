FROM store/oracle/serverjre:8
MAINTAINER hello@163.com
RUN mkdir -p /datas
WORKDIR /datas
COPY springcloud-ms-provider-6001-1.0-SNAPSHOT.jar ./
# 将脚本拷贝进去，等依赖的服务启动后才启动
COPY wait-for-it.sh ./
EXPOSE 6001