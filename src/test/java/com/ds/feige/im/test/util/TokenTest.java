package com.ds.feige.im.test.util;

/**
 * @author DC
 */
public class TokenTest {
    public static void main(String[] args) {
        String token =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhcHBJZCI6Mzk2NzE5MjkwNjcxNDI3NTg0LCJleHAiOjE2MDU3MTA1OTd9.-8Cvqwf-3LnzFKMnz2JT-Zf0QJkZPke3j8MeZ0f2f5I";
        String[] array = token.split("\\.");
        System.out.println(array.toString());
    }
}
