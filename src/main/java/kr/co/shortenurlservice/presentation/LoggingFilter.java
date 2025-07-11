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


//  필터 : 컨트롤러로 들어오고 나가는 요청과 응답에 대해서 우리가 추가적인 동작을 끼워 넣을 수 있는 곳
// 서블릿 스펙 기반(스프링과 별개)

//    가장 먼저 요청을 가로챌 수 있는 위치이고
//
//    🚩🚩getInputStream()은 단 한 번만 읽을 수 있기 때문에,
//    🚩🚩DispatcherServlet보다 먼저 캐싱하지 않으면 다시 못 읽어
//
//    즉, Wrapper로 감싸려면 필터에서 미리 감싸야 안전

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


            log.trace("Incoming Request: URL={}, Method={}, Body={}", url, method, body);

        }
        * */

        //ServletRequest 부모 > HttpServletRequest 하위 타입
        //조건 :  서블릿 컨테이너에서 전달된 요청이 HTTP 요청인지 확인하고, 스프링 진입 전 필터 단계에서 안전하게 HttpServletRequest로 꺼내 쓰기 위한 조건문
        if(request instanceof HttpServletRequest httpServletRequest) {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            String url = wrappedRequest.getRequestURI(); //@PathVariable 값은 여기 
            String method = wrappedRequest.getMethod();
            String body = wrappedRequest.getReader().lines().reduce("",String::concat);//캐시된 스크림 읽어오기
            String queryString = wrappedRequest.getQueryString();// 쿼리스트링

            // INFO : 비즈니스 로직~
            // TRACE : 레벨에는 어떤 요청이 들어왔고 (여기서는 TRACE 선택)
            // 어떤 경로, 어떤 메서드, 그리고 어떤 요청 body로 들어왔는지 같은 굉장히 디테일한 정보에 대해서 남기도록 할거임.
            log.trace("Incoming Request: URL={}, Method={}, QueryString={}, Body={}", url, method, queryString, body);

            // 래핑된 요청 객체를 다음 필터 체인으로 전달
            filterChain.doFilter(wrappedRequest, response);// 다음 필터 or 서블릿 실행

            log.trace("Incoming End");
        }else{
            // HttpServletRequest가 아닌 경우 그대로 전달
            filterChain.doFilter(request, response);
        }



        log.info("응답 필터 종료");
    }

   /*
   private  String getRequestBody(HttpServletRequest request){
    //지나간 스트림은 읽을 수 없다. 한번만 읽을 수 있기 때문에 getReader() hs already called for this request 에러가 출력된다.
    CachedBodyHttpServletRequest.java를 통해 body값을 캐시에 저장해서 사용했다.
        try(BufferedReader reader = request.getReader()){
            return  reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }catch (IOException e){
            log.error("Failed to read request body", e);
            return  "Unable to read request body";
        }
    }
    *./
    */


    @Override
    public void destroy() {
        log.info("LoggingFilter 소멸");
    }
}
