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

/**
 * Holds the current {@link Conversation} in the current thread of execution.
 */
public class ConversationHolder {
    private static final ThreadLocal<Conversation> currentConversationHolder = new ThreadLocal<Conversation>();

    /**
     * Returns the {@link Conversation} that is bound to the current thread of execution.
     */
    public static Conversation getCurrentConversation() {
        return currentConversationHolder.get();
    }

    /**
     * Bind the passed {@link Conversation} to the current thread of execution.
     */
    public static void setCurrentConversation(Conversation conversation) {
        currentConversationHolder.set(conversation);
    }
}