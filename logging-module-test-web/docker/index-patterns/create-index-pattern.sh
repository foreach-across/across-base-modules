#!/usr/bin/env bash

# Creates an index pattern
# Usage create_index_pattern url application_env application_name  appender default(true/false)
create_index_pattern(){
pattern="${2}-logs-${3}-${4}*"
echo '>  Creating index pattern: ' ${pattern} ' as default: ' ${5} ' <'
echo '> ' ${1} ' <'

curl -f -XPOST -H "Content-Type: application/json" -H "kbn-xsrf: anything" "${1}/api/saved_objects/index-pattern/${pattern}" -d'
{
  "attributes": {
    "title": "'${pattern}'",
    "timeFieldName": "@timestamp"
  }
}'

if [ "${5}" = true ]; then
# Create the default index
curl -XPOST -H "Content-Type: application/json" -H "kbn-xsrf: anything" "${1}/api/kibana/settings/defaultIndex" -d'
{
    "value":"'${pattern}'"
}'
fi
}

