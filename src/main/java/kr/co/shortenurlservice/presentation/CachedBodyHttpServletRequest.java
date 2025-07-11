package kr.co.shortenurlservice.presentation;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * HttpServletRequestWrapper는
 * 기존의 HttpServletRequest 객체를 감싸서 (Wrapper)
 * 필요한 메서드만 오버라이드해서 커스터마이징할 수 있도록 제공하는 추상 클래스
 *
 *      목적	                             예시
 * request body를                    여러 번 읽게 만들기	getInputStream(), getReader() 오버라이드
 * 특정 헤더 값 가로채거나 추가	        getHeader(), getHeaderNames() 오버라이드
 * 특정 파라미터 조작	                getParameter(), getParameterMap() 오버라이드
 * 전체 요청 객체에 대한 확장	        요청 가로채기, 로깅, 보안 등
 * **/
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {//중요, 찾아보기

    private  final  byte[] cachedBody;//stream은 한번 가져온 값은 읽고 지우기 때문에 캐시에 담아놓고 재사용하자.
    public CachedBodyHttpServletRequest(HttpServletRequest request)  throws IOException {
        super(request);

        //HTTP body(requestBody) 내용을 전부 바이트 배열로 읽어오는 것(캐싱하는 역할)
        this.cachedBody = request.getInputStream().readAllBytes();

    }

    //내가 쓰지는 않지만 Spring 내부에서 호출해서 사용할 수 있으니 두기로~
    @Override
    public ServletInputStream getInputStream(){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    //getReader()도 반드시 오버라이드해야 완벽하게 재사용 가능한 요청 객체가 됨
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(cachedBody),
                getCharacterEncoding() != null ? getCharacterEncoding() : "UTF-8"
        ));
    }


}
