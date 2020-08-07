# 安卓项目工程模板

![Release APK](https://github.com/gzu-liyujiang/CJKCharsetDetector/workflows/Release%20APK/badge.svg)
![Gradle Package](https://github.com/gzu-liyujiang/CJKCharsetDetector/workflows/Gradle%20Package/badge.svg)
[![jitpack](https://jitpack.io/v/gzu-liyujiang/CJKCharsetDetector.svg)](https://jitpack.io/#gzu-liyujiang/CJKCharsetDetector)

中日韩文本文件字符集自动检测。Auto-detection of charset in Chinese, Japanese and Korean text files.

基于 Mozilla 的 [jchardet-1.1](http://jchardet.sourceforge.net) 进行易用性封装， 并打包发布到 [![jitpack](https://jitpack.io/v/gzu-liyujiang/CJKCharsetDetector.svg)](https://jitpack.io/#gzu-liyujiang/CJKCharsetDetector)

```groovy
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	    implementation 'com.github.gzu-liyujiang:CJKCharsetDetector:latest.version'
	}
```
```groovy
    Charset charset = CJKCharsetDetector.detect(new FileInputStream(file));
    String str = new String(bytes, charset.name());
    if (CJKCharsetDetector.inWrongEncoding(str)) {
        System.err.println("File was loaded using wrong encoding: " + charset.name());
    }
```
## License

```text
Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
**NOTE**: This software include `jchardet` is licensed under the [MPL 1.1](http://www.mozilla.org/MPL/)
