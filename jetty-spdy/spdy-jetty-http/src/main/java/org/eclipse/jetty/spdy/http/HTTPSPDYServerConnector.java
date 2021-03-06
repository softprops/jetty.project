/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.jetty.spdy.http;

import org.eclipse.jetty.spdy.api.SPDY;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HTTPSPDYServerConnector extends AbstractHTTPSPDYServerConnector
{
    public HTTPSPDYServerConnector()
    {
        this(null, new PushStrategy.None());
    }

    public HTTPSPDYServerConnector(PushStrategy pushStrategy)
    {
        this(null, pushStrategy);
    }

    public HTTPSPDYServerConnector(SslContextFactory sslContextFactory)
    {
        this(sslContextFactory, new PushStrategy.None());
    }

    public HTTPSPDYServerConnector(SslContextFactory sslContextFactory, PushStrategy pushStrategy)
    {
        // We pass a null ServerSessionFrameListener because for
        // HTTP over SPDY we need one that references the endPoint
        super(null, sslContextFactory);
        clearAsyncConnectionFactories();
        // The "spdy/3" protocol handles HTTP over SPDY
        putAsyncConnectionFactory("spdy/3", new ServerHTTPSPDYAsyncConnectionFactory(SPDY.V3, getByteBufferPool(), getExecutor(), getScheduler(), this, pushStrategy));
        // The "spdy/2" protocol handles HTTP over SPDY
        putAsyncConnectionFactory("spdy/2", new ServerHTTPSPDYAsyncConnectionFactory(SPDY.V2, getByteBufferPool(), getExecutor(), getScheduler(), this, pushStrategy));
        // The "http/1.1" protocol handles browsers that support NPN but not SPDY
        putAsyncConnectionFactory("http/1.1", new ServerHTTPAsyncConnectionFactory(this));
        // The default connection factory handles plain HTTP on non-SSL or non-NPN connections
        setDefaultAsyncConnectionFactory(getAsyncConnectionFactory("http/1.1"));
    }
}
