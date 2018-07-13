FROM debian:latest

RUN apt-get update
RUN apt-get -y install curl

ADD create-index-pattern.sh ./create-index-pattern.sh
ADD create-default-patterns.sh ./create-default-patterns.sh
ADD wait-for-it.sh ./wait-for-it.sh

RUN chmod +x ./create-index-pattern.sh
RUN chmod +x ./create-default-patterns.sh
RUN chmod +x ./wait-for-it.sh

CMD ["/usr/local/bin/kibana-docker"]
