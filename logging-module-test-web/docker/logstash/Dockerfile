FROM docker.elastic.co/logstash/logstash-oss:6.3.0
RUN rm -f /usr/share/logstash/pipeline/logstash.conf
RUN rm -f /usr/share/logstash/config/logstash.yml

ADD logback.conf /usr/share/logstash/pipeline/logback.conf
ADD wait-for-it.sh /wait-for-it.sh
ADD insert-logstash-template.sh /insert-logstash-template.sh

ADD logstash.yml /usr/share/logstash/config/logstash.yml

CMD ["-f", "/usr/share/logstash/pipeline/", "--http.host", "0.0.0.0", "--http.port", "9600"]
