package kr.co.shortenurlservice.presentation;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 로직 필요 시 작성
        log.info("LoggingFilter 초기화");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.info("요청 필터 진입");

        /* if(request instanceof HttpServletRequest httpServletRequest){
        * 🔸 의미 1: 타입 확인
            request 객체가 HttpServletRequest 타입인지 확인해.

            🔸 의미 2: 조건이 true일 경우, 변수 선언과 자동 캐스팅
            만약 request가 HttpServletRequest의 인스턴스라면
            → httpRequest라는 새로운 지역 변수를 만들고,
            → HttpServletRequest 타입으로 자동 캐스팅된 request를 담아줘.
            *
            *  if(request instanceof HttpServletRequest httpServletRequest){
            // getRequestURI()는 ServletRequest에는 없고, HttpServletRequest 인터페이스에만 존재하는 메서드야.
            String url = httpServletRequest.getRequestURI();
            String method = httpServletRequest.getMethod();
            String body = getRequestBody(httpServletRequest);

            //트레이스 레벨에는 요청에 대한 어떤 요청이 들어왔고
            // 어떤 경로, 어떤 메서드, 그리고 어떤 요청 body로 들어왔는지 같은 굉장히 디테일한 정보에 대해서 남기도록 할거임.
            log.trace("Incoming Request: URL={}, Method={}, Body={}", url, method, body);

        }
        * */
        if(request instanceof HttpServletRequest httpServletRequest) {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            String url = wrappedRequest.getRequestURI();
            String method = wrappedRequest.getMethod();
            String body = wrappedRequest.getReader().lines().reduce("",String::concat);//캐시된 스크림 읽어오기

            log.trace("Incoming Request: URL={}, Method={}, Body={}", url, method, body);
        }

        // 다음 필터 or 서블릿 실행
        filterChain.doFilter(request, response);

        log.info("응답 필터 종료");
    }

//    private  String getRequestBody(HttpServletRequest request){
//        try(BufferedReader reader = request.getReader()){
//            return  reader.lines().collect(Collectors.joining(System.lineSeparator()));
//        }catch (IOException e){
//            log.error("Failed to read request body", e);
//            return  "Unable to read request body";
//        }
//    }


    @Override
    public void destroy() {
        log.info("LoggingFilter 소멸");
    }
}
