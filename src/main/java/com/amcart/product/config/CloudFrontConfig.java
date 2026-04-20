package com.amcart.product.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Holds CloudFront distribution domain and S3 bucket name.
 * Used by MediaStorageService to build public CDN URLs.
 *
 * CDN URL pattern: https://{cloudFrontDomain}/products/{productId}/{variant}
 */
@Getter
@Configuration
public class CloudFrontConfig {

    /** CloudFront distribution domain, e.g. d1abc23xyz.cloudfront.net */
    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    /** S3 bucket name, e.g. amcart-product-images-prod */
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String buildCdnUrl(String s3Key) {
        return "https://" + cloudFrontDomain + "/" + s3Key;
    }
}
