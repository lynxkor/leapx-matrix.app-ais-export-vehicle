FROM lpdocker.leapmotor.com/dz-network/secbase-images/openjdk:17.0.10-debian-12-7
LABEL maintainer="xiao_yan3@leapmotor.com"

ADD  --chown=lproot:lproot *.jar /app/app.jar

COPY --chown=lproot:lproot ./entrypoint.sh /app/
RUN chmod 777 /app/entrypoint.sh
ENV VERSION=
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["bash"]

