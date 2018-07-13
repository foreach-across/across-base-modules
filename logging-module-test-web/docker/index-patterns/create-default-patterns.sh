#!/usr/bin/env bash
source ./create-index-pattern.sh

url="http://kibana:5601"

application_name="poc"
declare -a environments=("dev" "acceptance" "production")
declare -a appenders=("errors" "methods" "hikari" "requests")
default_index_pattern_environment="dev"
default_index_pattern_appender="requests"


for env in "${environments[@]}"
do
    for appender in "${appenders[@]}"
    do
        default=false
        if [ "${default_index_pattern_appender}" = "${appender}" ] && [ "${default_index_pattern_environment}" = "${env}" ]; then
            default=true
        fi

        create_index_pattern ${url} ${env} ${application_name} ${appender} ${default}
    done
done
