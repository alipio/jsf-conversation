/*
 * Copyright 2013 JAXIO http://www.jaxio.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsfconversation;

import static org.jsfconversation.ConversationHolder.getCurrentConversation;
import static org.jsfconversation.ConversationUtil.getConversationContextId;
import static org.jsfconversation.ConversationUtil.getConversationId;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter responsible for creating/resuming {@link Conversation}.
 * By convention, the conversation id and the context id are carried by the _cid parameter.
 * To create a new conversation, you must request the initial conversation view and pass the _ncid_=value parameter.
 */
@Named
public class ConversationFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ConversationFilter.class);

    @Inject
    private ConversationManager conversationManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String cid = getConversationId(request);

        if (cid != null) {
            String ccid = getConversationContextId(request);

            // -----------------------------
            // RESUME existing conversation
            // -----------------------------
            try {
                conversationManager.resumeConversation(cid, ccid, request);
                log.debug("Conv. {} resumed. Nb ctx: {}", cid, getCurrentConversation().getConversationContextesCount());
            } catch (UnexpectedConversationException uue) {
                log.error(uue.getMessage());
                response.sendRedirect(request.getContextPath() + uue.getRedirectUrl());
                return;
            }

            try {
                filterChain.doFilter(request, response);
            } finally {
                conversationManager.pauseCurrentConversation(request);
            }
        } else if (!request.getRequestURI().contains("/javax.faces.resource/") && "true".equals(request.getParameter("_ncid_"))) {
            throw new IllegalArgumentException("This version does not support ncid parameter");
        } else {
            // -----------------------------
            // Not related to conversations
            // -----------------------------
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}