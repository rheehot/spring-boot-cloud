package web.service;

import feign.Client;
import feign.Feign;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.http.HttpStatus;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class DefaultRequestInterceptorTest {

    @Test
    void apply() {
        // given
        final RequestTemplate requestTemplate = new RequestTemplate();
        final DefaultRequestInterceptor defaultRequestInterceptor = new DefaultRequestInterceptor();

        // when
        defaultRequestInterceptor.apply(requestTemplate);
        final Map<String, Collection<String>> result = requestTemplate.headers();

        // then
        assertThat(result).containsKey("default");
    }

    @Test
    void verifyDefaultRequestInterceptor() {
        // given
        final String reason = "reason";
        final int status = HttpStatus.OK.value();
        final Client client = (request, options) -> Response.builder().body(String.join("", request.headers().get("default")), Charset.defaultCharset()).request(request).status(status).headers(Map.of()).reason(reason).build();

        final TestClient testClient = Feign.builder()
                                           .client(client)
                                           .contract(new SpringMvcContract())
                                           .requestInterceptor(new DefaultRequestInterceptor())
                                           .target(TestClient.class, "http://localhost:8080");

        // when
        final String result = testClient.findById("hotire");

        // then
        assertThat(result).isEqualTo("config");
    }
}