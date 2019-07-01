package com.github.hollykunge.security.auth.client.exception;

/**
 *
 * @author hollykunge
 * @date 2017/9/15
 */
public class JwtSignatureException extends Exception {
    public JwtSignatureException(String s) {
        super(s);
    }
}
