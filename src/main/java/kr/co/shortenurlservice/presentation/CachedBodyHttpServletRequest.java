package kr.co.shortenurlservice.presentation;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

//HttpServletRequestWrapper 기능 찾아보기
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {//중요, 찾아보기

    private  final  byte[] cachedBody;//stream은 한번 가져온 값은 읽고 지우기 때문에 캐시에 담아놓고 사용하기에
    public CachedBodyHttpServletRequest(HttpServletRequest request)  throws IOException {
        super(request);
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

}
