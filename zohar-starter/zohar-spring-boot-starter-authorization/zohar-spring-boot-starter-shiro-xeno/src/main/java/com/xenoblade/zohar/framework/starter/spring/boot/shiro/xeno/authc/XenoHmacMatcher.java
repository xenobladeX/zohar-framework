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
package com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.authc;

import java.util.Date;

import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.cache.CacheDelegator;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.config.MessageConfig;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.config.ShiroProperties;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.model.StatelessLogined;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.service.ShiroCryptoService;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.service.ShiroStatelessAccountProvider;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.token.HmacToken;
import com.xenoblade.zohar.framework.starter.spring.boot.shiro.xeno.util.Commons;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

import com.google.common.base.Strings;

/**
 * HMAC签名匹配器
 * @author xenoblade
 * @since 1.0.0
 */
public class XenoHmacMatcher implements CredentialsMatcher {

    private ShiroProperties properties;
    private MessageConfig messages;
    private ShiroCryptoService cryptoService;
    private ShiroStatelessAccountProvider accountProvider;
    private CacheDelegator cacheDelegator;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        HmacToken hmacToken = (HmacToken)token;
        String appId = hmacToken.getAppId();
        String digest = (String) info.getCredentials();
        String serverDigest = null;
        if(this.properties.isHmacBurnEnabled()
                &&this.cacheDelegator.cutBurnedToken(digest)){
            throw new AuthenticationException(MessageConfig.MSG_BURNED_TOKEN);
        }
        if(Commons.hasLen(this.properties.getHmacSecretKey())){
            serverDigest = this.cryptoService.hmacDigest(hmacToken.getBaseString());
        } else {
            String appKey = accountProvider.loadAppKey(appId);
            if(Strings.isNullOrEmpty(appKey))
                throw new AuthenticationException(MessageConfig.MSG_NO_SECRET_KEY);
            serverDigest = this.cryptoService.hmacDigest(hmacToken.getBaseString(),appKey);
        }

        if(Strings.isNullOrEmpty(serverDigest)){
            throw new AuthenticationException(this.messages.getMsgHmacError());
        }
        if(!serverDigest.equals(digest)){
            throw new AuthenticationException(this.messages.getMsgHmacError());
        }
        Long currentTimeMillis = System.currentTimeMillis();
        Long tokenTimestamp = Long.valueOf(hmacToken.getTimestamp());
        // 数字签名超时失效
        if ((currentTimeMillis-tokenTimestamp) > this.properties.getHmacPeriod()) {
            throw new AuthenticationException(this.messages.getMsgHmacTimeout());
        }
        // 检查账号
        boolean checkAccount = this.accountProvider.checkAccount(appId);
        if(!checkAccount){
            throw new AuthenticationException(this.messages.getMsgAccountException());
        }
        StatelessLogined statelessAccount = new StatelessLogined();
        statelessAccount.setTokenId(hmacToken.getDigest());
        statelessAccount.setAppId(hmacToken.getAppId());
        statelessAccount.setHost(hmacToken.getHost());
        statelessAccount.setIssuedAt(new Date(tokenTimestamp));
        StatelessLocals.setAccount(statelessAccount);
        return true;
    }

    public void setProperties(ShiroProperties properties) {
        this.properties = properties;
    }
    public void setCryptoService(ShiroCryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }
    public void setAccountProvider(ShiroStatelessAccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }
    public void setMessages(MessageConfig messages) {
        this.messages = messages;
    }
    public void setCacheDelegator(CacheDelegator cacheDelegator) {
        this.cacheDelegator = cacheDelegator;
    }
}
