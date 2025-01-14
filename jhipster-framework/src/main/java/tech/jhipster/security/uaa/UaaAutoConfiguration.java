/*
 * Copyright 2016-2021 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.jhipster.security.uaa;

import tech.jhipster.config.JHipsterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * <p>UaaAutoConfiguration class.</p>
 */
@Configuration
@ConditionalOnClass({ClientCredentialsResourceDetails.class, LoadBalancerClient.class})
@ConditionalOnProperty("jhipster.security.client-authorization.client-id")
public class UaaAutoConfiguration {

    private JHipsterProperties jHipsterProperties;

    /**
     * <p>Constructor for UaaAutoConfiguration.</p>
     *
     * @param jHipsterProperties a {@link JHipsterProperties} object.
     */
    public UaaAutoConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    /**
     * <p>loadBalancedResourceDetails.</p>
     *
     * @param loadBalancerClient a {@link org.springframework.cloud.client.loadbalancer.LoadBalancerClient} object.
     * @return a {@link LoadBalancedResourceDetails} object.
     */
    @Bean
    public LoadBalancedResourceDetails loadBalancedResourceDetails(LoadBalancerClient loadBalancerClient) {
        LoadBalancedResourceDetails loadBalancedResourceDetails = new LoadBalancedResourceDetails(loadBalancerClient);
        JHipsterProperties.Security.ClientAuthorization clientAuthorization = jHipsterProperties.getSecurity()
            .getClientAuthorization();
        loadBalancedResourceDetails.setAccessTokenUri(clientAuthorization.getAccessTokenUri());
        loadBalancedResourceDetails.setTokenServiceId(clientAuthorization.getTokenServiceId());
        loadBalancedResourceDetails.setClientId(clientAuthorization.getClientId());
        loadBalancedResourceDetails.setClientSecret(clientAuthorization.getClientSecret());
        return loadBalancedResourceDetails;
    }
}
