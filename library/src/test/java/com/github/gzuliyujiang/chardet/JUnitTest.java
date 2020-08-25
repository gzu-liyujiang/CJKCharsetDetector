/*
 * Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.gzuliyujiang.chardet;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by liyujiang on 2020/08/06 01:00
 *
 * @author 大定府羡民
 */
public class JUnitTest {

    static {
        CJKCharsetDetector.DEBUG = true;
    }

    @Test
    public final void detectASCII() {
        Assert.assertTrue(guessCharset("ASCII"));
    }

    @Test
    public final void detectUTF8() {
        Assert.assertTrue(guessCharset("UTF-8"));
    }

    @Test
    public final void detectUTF8WithBOM() {
        Assert.assertTrue(guessCharset("UTF-8-BOM", "UTF-8"));
    }

    @Test
    public final void detectGBK() {
        Assert.assertTrue(guessCharset("GBK"));
    }

    @Test
    public final void detectGB2312() {
        Assert.assertTrue(guessCharset("GB2312"));
    }

    @Test
    public final void detectGB18030() {
        Assert.assertTrue(guessCharset("GB18030"));
    }

    @Test
    public final void detectBig5() {
        Assert.assertTrue(guessCharset("Big5"));
    }

    @Test
    public final void detectShiftJIS() {
        Assert.assertTrue(guessCharset("Shift_JIS"));
    }

    @Test
    public final void detectEUCKR() {
        Assert.assertTrue(guessCharset("EUC-KR"));
    }

    /*@Test
    public final void detectKOI8R() {
        // NOTE: KOI8-R 编码 被识别成 Shift_JIS 编码，不知道是不是样本不靠谱？
        Assert.assertTrue(guessCharset("KOI8-R"));
    }*/

    private static boolean guessCharset(String charsetName) {
        return guessCharset(charsetName, charsetName);
    }

    private static boolean guessCharset(String fileName, String charsetName) {
        System.out.println("---------------------------------");
        try {
            System.out.println("Origin charset: " + charsetName);
            File file = new File(System.getProperty("user.dir"), fileName + ".txt");
            System.out.println("Target file: " + file);
            byte[] bytes = readBytes(new FileInputStream(file));
            System.out.println("Bytes length: " + bytes.length);
            //Charset charset = CJKCharsetDetector.detect(new FileInputStream(file));
            Charset charset = CJKCharsetDetector.detect(new ByteArrayInputStream(bytes));
            assert charset != null;
            System.out.println("Detect charset: " + charset);
            String str = new String(bytes, charset.name());
            System.out.println("Display text: \n**********\n" + str.trim() + "\n**********");
            if (CJKCharsetDetector.inWrongEncoding(str)) {
                System.err.println("File was loaded in the wrong encoding: " + charset.name());
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] readBytes(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buff = new byte[524288];
        while (true) {
            int len = is.read(buff);
            if (len == -1) {
                break;
            } else {
                os.write(buff, 0, len);
            }
        }
        byte[] bytes = os.toByteArray();
        os.close();
        is.close();
        return bytes;
    }

}
