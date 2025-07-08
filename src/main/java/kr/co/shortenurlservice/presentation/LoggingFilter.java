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
public class LoggingFilter implements Filter {//톰캣이 전달한 실제 요청 객체 (HttpServletRequest) 를 가장 먼저 접할 수 있는 곳

//    필터가 톰캣과 가까운 이유
//    💥필터는 서블릿(=스프링 진입)보다 먼저 실행됨
//    💥톰캣이 요청을 받을 때 가장 먼저 만나는 컴포넌트
//    스프링 MVC를 쓰든 안 쓰든 무조건 실행됨

    //순서 : 클라이언트 요청 -> 필터(서블릿 컨테이너(tomcat)단계) -> 서블릿(디스패쳐 서블릿, 스프링 mvc 진입) -> 인터셉터 -> 컨트롤러

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 로직 필요 시 작성
        log.info("LoggingFilter 초기화");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.info("요청 필터 진입");

        /* if(request instanceof HttpServletRequest httpServletRequest){
            // getRequestURI()는 ServletRequest에는 없고, HttpServletRequest 인터페이스에만 존재하는 메서드야.
            String url = httpServletRequest.getRequestURI();
            String method = httpServletRequest.getMethod();
            String body = getRequestBody(httpServletRequest);

            //트레이스 레벨에는 요청에 대한 어떤 요청이 들어왔고
            // 어떤 경로, 어떤 메서드, 그리고 어떤 요청 body로 들어왔는지 같은 굉장히 디테일한 정보에 대해서 남기도록 할거임.
            log.trace("Incoming Request: URL={}, Method={}, Body={}", url, method, body);

        }
        * */

        //ServletRequest 부모 > HttpServletRequest 하위 타입
        //조건 :  서블릿 컨테이너에서 전달된 요청이 HTTP 요청인지 확인하고, 스프링 진입 전 필터 단계에서 안전하게 HttpServletRequest로 꺼내 쓰기 위한 조건문
        if(request instanceof HttpServletRequest httpServletRequest) {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            String url = wrappedRequest.getRequestURI();
            String method = wrappedRequest.getMethod();
            String body = wrappedRequest.getReader().lines().reduce("",String::concat);//캐시된 스크림 읽어오기

            log.trace("Incoming Request: URL={}, Method={}, Body={}", url, method, body);

            // 래핑된 요청 객체를 다음 필터 체인으로 전달
            filterChain.doFilter(wrappedRequest, response);// 다음 필터 or 서블릿 실행
        }else{
            // HttpServletRequest가 아닌 경우 그대로 전달
            filterChain.doFilter(request, response);
        }



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
