package com.dnd.runus.presentation.config;

import com.dnd.runus.auth.token.access.AccessTokenProvider;
import com.dnd.runus.presentation.filter.AuthenticationCheckFilter;
import com.dnd.runus.presentation.handler.ApiResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * 컨트롤러 테스트를 위한 Helper 클래스입니다.
 * <p>MockMvc를 초기화하고, ObjectMapper를 주입합니다.
 */
@Import({ObjectMapperConfig.class, ApiResponseHandler.class})
public abstract class ControllerTestHelper {
    @Autowired
    private ApiResponseHandler apiResponseHandler;

    protected MockMvc mvc = null;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AccessTokenProvider accessTokenProvider;

    @MockBean
    protected AuthenticationCheckFilter authenticationCheckFilter;

    /**
     * MockMvc를 초기화합니다.
     * <p>테스트를 진행하기 전에 반드시 호출해야 합니다.
     *
     * @param controller MockMvc를 초기화할 컨트롤러
     */
    protected void setUpMockMvc(@NotNull Object controller) {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(apiResponseHandler)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }
}
