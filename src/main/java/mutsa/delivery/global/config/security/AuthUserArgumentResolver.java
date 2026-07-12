package mutsa.delivery.global.config.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    // 1. 어떤 파라미터에 이 리졸버를 적용할지 결정합니다.
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 파라미터에 @AuthUser 어노테이션이 붙어있고, 타입이 Long(userId)인 경우에만 작동합니다.
        return parameter.hasParameterAnnotation(AuthUser.class)
                && parameter.getParameterType().equals(Long.class);
    }

    // 2. supportsParameter가 true를 반환하면 실제로 유저 데이터를 뽑아서 주입하는 로직입니다.
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // 시큐리티 저장소에서 필터가 저장해둔 인증 객체를 꺼냅니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        // JwtAuthenticationFilter에서 principal 자리에 넣어둔 userId(Long)를 그대로 리턴합니다.
        return authentication.getPrincipal();
    }
}
