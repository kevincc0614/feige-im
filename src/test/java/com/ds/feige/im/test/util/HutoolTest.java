package com.ds.feige.im.test.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * @author DC
 */
public class HutoolTest {
    public static void main(String[] args) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        long id1 = snowflake.nextId();
        long id2 = snowflake.nextId();
        String content = id1 + "," + id2;
        System.out.println("ID:" + id1 + "," + id2);
        String beforeEncrypt = Base64.encode(content);
        System.out.println("加密前Base64:" + beforeEncrypt);
        // 随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        // 构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        // 加密
        byte[] encrypt = aes.encrypt(content);
        // 解密
        byte[] decrypt = aes.decrypt(encrypt);
        // 加密为16进制表示
        String encryptHex = aes.encryptHex(content);
        String encode = Base64.encode(encryptHex);
        System.out.println("加密后Base64:" + encode);
        System.out.println("Base64之前:" + encryptHex);
    }
}
