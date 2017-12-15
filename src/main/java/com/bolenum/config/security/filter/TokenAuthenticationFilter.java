/**
 * 
 */
package com.bolenum.config.security.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.bolenum.config.security.ApplicationUserDetail;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.repo.common.AuthenticationTokenRepo;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public class TokenAuthenticationFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		// extract token from header
		String token = httpRequest.getHeader("Authorization");
		if (token != null && !token.isEmpty()) {
			AuthenticationTokenRepo authenticationTokenRepository = WebApplicationContextUtils
					.getRequiredWebApplicationContext(httpRequest.getServletContext())
					.getBean(AuthenticationTokenRepo.class);
			// check whether token is valid
			AuthenticationToken authToken = authenticationTokenRepository.findByToken(token);
			if (authToken != null) {
				// Add user to SecurityContextHolder
				final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						authToken.getUser(), null, new ApplicationUserDetail(authToken.getUser()).getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		HtmlResponseWrapper capturingResponseWrapper = new HtmlResponseWrapper(
                (HttpServletResponse) response);

		chain.doFilter(request, capturingResponseWrapper);
		SecurityContextHolder.clearContext();
            String content = capturingResponseWrapper.getCaptureAsString();
            response.getWriter().write(content);
            // replace stuff here
            System.out.println("respose after dofilter for :"+httpRequest.getRequestURI().substring(httpRequest.getContextPath().length())+" is ........"+content);

}







class HtmlResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream capture;
    private ServletOutputStream output;
    private PrintWriter writer;

    public HtmlResponseWrapper(HttpServletResponse response) {
        super(response);
        capture = new ByteArrayOutputStream(response.getBufferSize());
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (writer != null) {
            throw new IllegalStateException(
                    "getWriter() has already been called on this response.");
        }

        if (output == null) {
            output = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    capture.write(b);
                }

                @Override
                public void flush() throws IOException {
                    capture.flush();
                }

                @Override
                public void close() throws IOException {
                    capture.close();
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener arg0) {
                }
            };
        }

        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (output != null) {
            throw new IllegalStateException(
                    "getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(capture,
                    getCharacterEncoding()));
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();

        if (writer != null) {
            writer.flush();
        } else if (output != null) {
            output.flush();
        }
    }

    public byte[] getCaptureAsBytes() throws IOException {
        if (writer != null) {
            writer.close();
        } else if (output != null) {
            output.close();
        }

        return capture.toByteArray();
    }

    public String getCaptureAsString() throws IOException {
        return new String(getCaptureAsBytes(), getCharacterEncoding());
    }
}
}
