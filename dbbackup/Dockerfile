# Docker image of mysql-client
# VERSION 1.0.0
# Author: gosun

FROM alpine:3.11

ADD cron.daily/db-backup /usr/local/bin/
ADD entrypoint.sh /

RUN  sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
     apk add --update mysql-client tzdata bash && \
     rm -rf /var/cache/apk/* && \
     sed -i 's/[\r]*$//g' /usr/local/bin/db-backup && \
     chmod 777  /usr/local/bin/db-backup  && \
     ln -s /usr/local/bin/db-backup /etc/periodic/daily/db-backup && \
     sed -i 's/[\r]*$//g' /entrypoint.sh && \
     chmod 777 /entrypoint.sh && \
     ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

ENTRYPOINT ["/entrypoint.sh"]

