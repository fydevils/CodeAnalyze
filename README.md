# Code-Analyze

### 集成步骤
项目根目录添加：
```
buildscript {
    dependencies {
        classpath 'com.foryou.gradle:code-analyze:0.1.4'
    }
}
```

模块gradle文件添加：
```
apply plugin: 'com.foryou.gradle.code-analyze'
```