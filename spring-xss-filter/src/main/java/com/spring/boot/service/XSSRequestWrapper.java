package com.spring.boot.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static com.spring.boot.service.XSSUtils.stripXSS;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

	public XSSRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {

		 //Non json type, return directly
        if (!super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            return super.getInputStream();
        }

        //Null, return directly
		List<String> strings = IOUtils.readLines(super.getInputStream(), "utf-8");
		String json = StringUtils.join(strings,"");
        if (StringUtils.isEmpty(json)) {
            return super.getInputStream();
        }

        json = XSSUtils.stripXSS(json).trim();
        final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setReadListener(ReadListener readListener) {
            }
            @Override
            public int read() {
                return bis.read();
            }
        };
    }

	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = stripXSS(values[i]);
		}
		return encodedValues;
	}

	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		return stripXSS(value);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		List<String> result = new ArrayList<>();
		Enumeration<?> headers = super.getHeaders(name);
		while (headers.hasMoreElements()) {
			String header = (String) headers.nextElement();
			String[] tokens = header.split(",");
			for( String token : tokens) {
				result.add(XSSUtils.stripXSS(token));
			}
		}
		return Collections.enumeration(result);
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		return stripXSS(value);
	}
}