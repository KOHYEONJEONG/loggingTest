package kr.co.shortenurlservice.presentation;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

//HttpServletRequestWrapper 기능 찾아보기
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private  final  byte[] cachedBody;//stream은 한번 가져온 값은 읽고 지우기 때문에 캐시에 담아놓고 재사용하자.
    public CachedBodyHttpServletRequest(HttpServletRequest request)  throws IOException {
        super(request);

        //HTTP body(requestBody) 내용을 전부 바이트 배열로 읽어오는 것(캐싱하는 역할)
        this.cachedBody = request.getInputStream().readAllBytes();

    }

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
