/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.commons.utils.wildcards;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * WildcardRules
 * @author xenoblade
 * @since 1.0.0
 */
public class WildcardRules {
    private Set<WildcardRule> rules;

    /**
     * JWildcardRules represents a set of rules to use while converting
     * wildcard to regex string
     */
    public WildcardRules() {
        rules = new HashSet<>();
    }

    /**
     * JWildcardRules represents a set of rules to use while converting
     * wildcard to regex string
     *
     * @param rules a collection of JWildcardRule
     */
    public WildcardRules(final Set<WildcardRule> rules) {
        this.rules = (rules != null) ? new HashSet<>(rules) : new HashSet<>();
    }

    /**
     * Add a rule to the existing rules
     *
     * @param rule JWildcardRule
     * @return <tt>true</tt> if the rules set did not already contain the specified element
     */
    public boolean addRule(WildcardRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule can't be null");
        }
        return rules.add(rule);
    }

    /**
     * Add a set of rules to the existing rules
     *
     * @param rules a collection of JWildcardRule
     * @return <tt>true</tt> if the rules set did not already contain the specified elements
     */
    public boolean addRules(Collection<WildcardRule> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("Rules list can't be null");
        }

        return this.rules.addAll(rules);
    }

    /**
     * Removes the specified element from this set if it is present
     *
     * @param rule JWildcardRule
     * @return <tt>true</tt> if this set contained the specified element
     */
    public boolean removeRule(WildcardRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule to remove can't be null");
        }

        return rules.remove(rule);
    }

    public Set<WildcardRule> getRules() {
        return rules;
    }
}
