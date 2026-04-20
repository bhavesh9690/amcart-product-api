#!/bin/bash
# LocalStack init script — creates S3 bucket on container startup
awslocal s3 mb s3://amcart-product-images-dev
echo "S3 bucket amcart-product-images-dev created in LocalStack"
