/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.server.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.logging.Logger;

/**
 * This filter reads the REST path information from the web.xml file and makes
 * it available for Angular code.
 */
public final class RESTPathFilter implements Filter 
{
    private static final Logger LOGGER = Logger.getLogger(RESTPathFilter.class.getName());

    @Override
    public void init(final FilterConfig filterConfig)
    {
        // no-op
    }

    @Override
    public void destroy()
    {
        // do nothing
    }

    @Override
    public final void doFilter(final ServletRequest request,
        final ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        PrintWriter out = new PrintWriter(response.getOutputStream());
        GenericResponseWrapper wrapper = new GenericResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapper);

        /*
         * We set this cookie so the client can know the time on the server.  We could
         * set this into the page like everything else, but we don't want to risk that 
         * this value gets cached since it prevents the user from accessing the application.
         */
        Cookie cookie = new Cookie("gromit-time", "");
        cookie.setValue("" + new Date().getTime());
        ((HttpServletResponse) response).addCookie(cookie);

        response.setContentType(wrapper.getContentType());

        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\t\t<script type=\"text/javascript\">\n");
        sb.append("iac.bestLocale = '" + request.getLocale() + "';\n");
        sb.append("</script>\n</head>");

        String transformedContent = wrapper.toString().replace("</head>", sb.toString());
        response.setContentLength(transformedContent.length());
        out.write(transformedContent);
        out.flush();
        out.close();
    }
}

