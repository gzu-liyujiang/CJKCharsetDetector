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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Auto-detect charset of Chinese/Japanese/Korean.
 * Based on jchardet (http://jchardet.sourceforge.net)
 * <p>
 * Created by liyujiang on 2020/08/05 21:17
 */
@SuppressWarnings("unused")
public final class CJKCharsetDetector implements nsICharsetDetectionObserver {
    public static boolean DEBUG = false;
    private static volatile CJKCharsetDetector instance;
    private boolean alreadyFound;
    private String probableCharset;

    private CJKCharsetDetector() {
        super();
    }

    private static CJKCharsetDetector getInstance() {
        if (instance == null) {
            synchronized (CJKCharsetDetector.class) {
                if (instance == null) {
                    instance = new CJKCharsetDetector();
                }
            }
        }
        return instance;
    }

    public static boolean inWrongEncoding(String str) {
        return str.contains("\ufffd");
    }

    @Nullable
    public static Charset detect(@NonNull byte[] data) {
        return detect(new ByteArrayInputStream(data));
    }

    @Nullable
    public static Charset detect(@NonNull InputStream inputStream) {
        return detectCharsetForCJK(inputStream, nsDetector.ALL);
    }

    @Nullable
    public static Charset detectCharsetOnlyChinese(@NonNull InputStream inputStream) {
        return detectCharsetForCJK(inputStream, nsDetector.CHINESE);
    }

    @Nullable
    public static Charset detectCharsetOnlyJapanese(@NonNull InputStream inputStream) {
        return detectCharsetForCJK(inputStream, nsDetector.JAPANESE);
    }

    @Nullable
    public static Charset detectCharsetOnlyKorean(@NonNull InputStream inputStream) {
        return detectCharsetForCJK(inputStream, nsDetector.KOREAN);
    }

    @Nullable
    public static Charset detectCharsetForCJK(@NonNull InputStream inputStream, int language) {
        try {
            CJKCharsetDetector instance = getInstance();
            instance.guessCharset(inputStream, language);
            Charset charset = null;
            if (instance.probableCharset != null) {
                charset = Charset.forName(instance.probableCharset);
            }
            return charset;
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void Notify(String charset) {
        if (DEBUG) {
            System.out.println("Notify charset: " + charset);
        }
        alreadyFound = true;
        probableCharset = charset;
    }

    private void guessCharset(@NonNull InputStream inputStream, int language) throws Exception {
        nsDetector detector = new nsDetector(language);
        detector.Init(this);
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;
        while ((len = bis.read(buf, 0, buf.length)) != -1) {
            if (isAscii) {
                isAscii = detector.isAscii(buf, len);
            }
            if (!isAscii && !done) {
                done = detector.DoIt(buf, len, false);
            }
        }
        detector.DataEnd();
        if (isAscii) {
            alreadyFound = true;
            if (DEBUG) {
                System.out.println("ASCII first: true");
            }
            probableCharset = "ASCII";
            return;
        }
        if (!alreadyFound) {
            String[] probableCharsets = detector.getProbableCharsets();
            if (DEBUG) {
                System.out.println("Probable charsets: " + Arrays.toString(probableCharsets));
            }
            // 先取第一个可能的字符集，然后再赛选其他可能的字符集
            probableCharset = probableCharsets[0];
            for (String itCharset : probableCharsets) {
                // “UTF-16LE、UTF-16BE、GB18030”这几种范围比较大，目前并不常用，优先级靠后
                // [UTF-16BE, Big5, GB18030]
                // [UTF-16LE, Big5, GB18030, UTF-16BE]
                // [GB18030, Shift_JIS, UTF-16BE]
                if (!(itCharset.startsWith("UTF-16") || itCharset.startsWith("GB18030"))) {
                    probableCharset = itCharset;
                    break;
                }
            }
        }
        if ("nomatch".equals(probableCharset)) {
            if (DEBUG) {
                System.out.println("Charset no match");
            }
            throw new Exception("no match");
        }
        alreadyFound = false;
    }

}
