FROM docker.elastic.co/logstash/logstash-oss:6.2.3
RUN rm -f /usr/share/logstash/pipeline/logstash.conf
RUN rm -f /usr/share/logstash/config/logstash.yml

RUN logstash-plugin install logstash-input-sqs

ADD import_logback.conf /usr/share/logstash/pipeline/import_logback.conf

ADD logstash.yml /usr/share/logstash/config/logstash.yml

CMD ["-f", "/usr/share/logstash/pipeline/", "--http.host", "0.0.0.0", "--http.port", "9600"]