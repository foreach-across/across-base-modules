FROM docker.elastic.co/logstash/logstash-oss:6.3.0

RUN rm -f /usr/share/logstash/pipeline/logstash.conf
RUN rm -f /usr/share/logstash/config/logstash.yml

ADD wait-for-it.sh ./wait-for-it.sh
ADD insert-logstash-template.sh ./insert-logstash-template.sh

USER root
RUN chown logstash ./wait-for-it.sh
RUN chown logstash ./insert-logstash-template.sh
RUN chmod +x ./wait-for-it.sh
RUN chmod +x ./insert-logstash-template.sh
USER logstash

ADD logback.conf /usr/share/logstash/pipeline/logback.conf
ADD logstash.yml /usr/share/logstash/config/logstash.yml

CMD ["-f", "/usr/share/logstash/pipeline/", "--http.host", "0.0.0.0", "--http.port", "9600"]
