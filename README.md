# Java Implementation of Shorten Decoder
This is a fork of [Java Implementation of Shorten Decoder](https://github.com/soiaf/Java-Shorten-decoder), with JavaSound SPI support.

This library is a Java implementation of Shorten audio decoder. The code is based on v3.6.1 of the Shorten C code. This implementation accepts Shorten files that have been generated from either WAV zor AIFF files.

The code is based around decoding PCM data, but it can also successfully decode files using:  
- TYPE_AU1 - original lossless ulaw (8-bit)
- TYPE_AU2 - new ulaw with zero mapping (8-bit)
- TYPE_AU3 - lossless alaw (8-bit)

## Add the library to your project (gradle)
1. Add the Maven Central repository (if not exist) to your build file:
```groovy
repositories {
    ...
    mavenCentral()
}
```

2. Add the dependency:
```groovy
dependencies {
    ...
    implementation 'com.tianscar.javasound:javasound-shorten:3.6.2'
}
```

## Usage
[Tests and Examples](/src/test/java/com/beatofthedrum/shortendecoder/test)  
[Command-line interfaces](/src/test/com/beatofthedrum/shortendecoder/cli)

Note you need to download test audios [here](https://github.com/Tianscar/fbodemo1) and put them to /src/test/java/resources to run the test code properly!

## License
[BSD 3-Clause](/LICENSE)
