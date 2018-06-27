#!/bin/bash
curl -XPUT "http://elasticsearch:9200/_template/logstash" -H 'Content-Type: application/json' -d'
{
    "order": 0,
    "index_patterns": [
      "*"
    ],
    "settings": {
      "index": {
        "number_of_shards": "1",
        "refresh_interval": "5s"
      }
    },
    "mappings": {
      "_default_": {
        "dynamic_templates": [
          {
            "message_field": {
              "path_match": "message",
              "match_mapping_type": "string",
              "mapping": {
                "type": "text",
                "norms": false
              }
            }
          },
          {
            "string_fields": {
              "match": "*",
              "match_mapping_type": "string",
              "mapping": {
                "type": "text",
                "norms": false,
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              }
            }
          }
        ],
        "properties": {
          "@timestamp": {
            "type": "date"
          },
          "@version": {
            "type": "keyword"
          },
          "geoip": {
            "dynamic": true,
            "properties": {
              "ip": {
                "type": "ip"
              },
              "location": {
                "type": "geo_point"
              },
              "latitude": {
                "type": "half_float"
              },
              "longitude": {
                "type": "half_float"
              }
            }
          }
        }
      }
    },
    "aliases": {}
}'
