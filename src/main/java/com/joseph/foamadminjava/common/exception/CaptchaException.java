package com.joseph.foamadminjava.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码校验异常
 * @author Joseph.Liu
 */
public class CaptchaException extends AuthenticationException {

    public CaptchaException(String msg) {
        super(msg);
    }
}
