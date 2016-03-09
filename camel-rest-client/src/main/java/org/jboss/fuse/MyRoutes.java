package org.jboss.fuse;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.cdi.Uri;

/**
 * Configures all our Camel routes, components, endpoints and beans
 */
@ContextName("myCdiCamelContext")
public class MyRoutes extends RouteBuilder {

    //protected static final String DOCKER_CONTAINER_IP = System.getenv("DOCKER_CONTAINER_IP");
    //protected static final String NETTY_URI = "netty4-http:http://{{env:DOCKER_CONTAINER_IP}}:8080?keepalive=false&disconnect=true";

    @Inject
    @Uri("timer:foo?period=5000")
    private Endpoint inputEndpoint;

    @Inject
    @Uri("netty4-http:http://{{env:DOCKER_CONTAINER_IP}}:8080?keepalive=false&disconnect=true")
    private Endpoint httpEndpoint;

    @Inject
    private SomeBean someBean;

    @Override
    public void configure() throws Exception {

        from(inputEndpoint)
            .setHeader("user").method(someBean,"getRandomUser")
            .setHeader("CamelHttpPath").simple("/camel/users/${header.user}/hello")
            .to(httpEndpoint)
            .log("Response : ${body}");
    }

}
