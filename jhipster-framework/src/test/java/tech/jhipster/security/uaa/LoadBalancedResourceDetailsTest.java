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

import tech.jhipster.test.LogbackRecorder;
import tech.jhipster.test.LogbackRecorder.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LoadBalancedResourceDetailsTest {

    private static final String ACCESS_TOKEN_URI = "http://access.token.uri/";
    private static final String TOKEN_SERVICE_ID = "tokkie";

    private LoadBalancerClient client;
    private LogbackRecorder recorder;

    @BeforeEach
    public void setup() {
        client = spy(LoadBalancerClient.class);
        doReturn(null).when(client).choose(TOKEN_SERVICE_ID);
        doAnswer(new Answer<URI>() {

            @Override
            public URI answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }

        }).when(client).reconstructURI(any(), any());

        recorder = LogbackRecorder.forClass(LoadBalancedResourceDetails.class).reset().capture("ALL");
    }

    @AfterEach
    public void teardown() {
        recorder.release();
    }

    @Test
    public void testWithoutClient() {
        LoadBalancedResourceDetails details = spy(new LoadBalancedResourceDetails(null));
        details.setAccessTokenUri(ACCESS_TOKEN_URI);
        assertThat(details.getAccessTokenUri()).isEqualTo(ACCESS_TOKEN_URI);
        verify(client, never()).reconstructURI(any(), any());

        List<Event> events = recorder.play();
        assertThat(events).isEmpty();
    }

    @Test
    public void testWithoutClientWithEmptyTokenService() {
        LoadBalancedResourceDetails details = spy(new LoadBalancedResourceDetails(null));
        details.setAccessTokenUri(ACCESS_TOKEN_URI);
        details.setTokenServiceId("");
        assertThat(details.getAccessTokenUri()).isEqualTo(ACCESS_TOKEN_URI);
        assertThat(details.getTokenServiceId()).isEmpty();
        verify(client, never()).reconstructURI(any(), any());

        List<Event> events = recorder.play();
        assertThat(events).isEmpty();
    }

    @Test
    public void testWithoutClientWithTokenService() {
        LoadBalancedResourceDetails details = spy(new LoadBalancedResourceDetails(null));
        details.setAccessTokenUri(ACCESS_TOKEN_URI);
        details.setTokenServiceId(TOKEN_SERVICE_ID);
        assertThat(details.getAccessTokenUri()).isEqualTo(ACCESS_TOKEN_URI);
        assertThat(details.getTokenServiceId()).isEqualTo(TOKEN_SERVICE_ID);
        verify(client, never()).reconstructURI(any(), any());

        List<Event> events = recorder.play();
        assertThat(events).isEmpty();
    }

    @Test
    public void testWithClient() {
        LoadBalancedResourceDetails details = spy(new LoadBalancedResourceDetails(client));
        details.setAccessTokenUri(ACCESS_TOKEN_URI);
        assertThat(details.getAccessTokenUri()).isEqualTo(ACCESS_TOKEN_URI);
        verify(client, never()).reconstructURI(any(), any());

        List<Event> events = recorder.play();
        assertThat(events).isEmpty();
    }

    @Test
    public void testWithClientAndEmptyTokenService() {
        LoadBalancedResourceDetails details = spy(new LoadBalancedResourceDetails(client));
        details.setAccessTokenUri(ACCESS_TOKEN_URI);
        details.setTokenServiceId("");
        assertThat(details.getAccessTokenUri()).isEqualTo(ACCESS_TOKEN_URI);
        assertThat(details.getTokenServiceId()).isEmpty();
        verify(client, never()).reconstructURI(any(), any());

        List<Event> events = recorder.play();
        assertThat(events).isEmpty();
    }

    @Test
    public void testWithClientAndTokenService() {
        LoadBalancedResourceDetails details = spy(new LoadBalancedResourceDetails(client));
        details.setAccessTokenUri(ACCESS_TOKEN_URI);
        details.setTokenServiceId(TOKEN_SERVICE_ID);
        assertThat(details.getAccessTokenUri()).isEqualTo(ACCESS_TOKEN_URI);
        assertThat(details.getTokenServiceId()).isEqualTo(TOKEN_SERVICE_ID);

        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(client).reconstructURI(any(), captor.capture());
        assertThat(captor.getValue().toString()).isEqualTo(ACCESS_TOKEN_URI);

        List<Event> events = recorder.play();
        assertThat(events).isEmpty();
    }

    @Test
    public void testInvalidAccessTokenURI() {
        String invalidUri = "%";
        Throwable exception = catchThrowable(() -> new URI(invalidUri));
        LoadBalancedResourceDetails details = spy(new LoadBalancedResourceDetails(client));
        details.setAccessTokenUri(invalidUri);
        details.setTokenServiceId(TOKEN_SERVICE_ID);
        assertThat(details.getAccessTokenUri()).isEqualTo(invalidUri);

        List<Event> events = recorder.play();
        assertThat(events).hasSize(1);
        Event event = events.get(0);
        assertThat(event.getLevel()).isEqualTo("ERROR");
        assertThat(event.getMessage()).isEqualTo(LoadBalancedResourceDetails.EXCEPTION_MESSAGE);
        assertThat(event.getThrown()).isNull();
    }
}
