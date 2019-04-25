# Android 静态代码检查
Android静态代码检查主要分为四个部分：checkStyle、findbugs、pmd、lint。下面简单介绍，最后对可行性、适用性做讨论。
## FindBugs
a program which uses static analysis to look for bugs in Java code. 
The current version of FindBugs is 3.0.1.
FindBugs requires JRE (or JDK) 1.7.0 or later to run.  However, it can analyze programs compiled for any version of Java, from 1.0 to 1.8.([链接](http://findbugs.sourceforge.net/))

#### 使用步骤：
##### gradle 配置

```
apply plugin: 'findbugs'

findbugs {
    //工具版本
    toolVersion = "3.0.1"
    //忽略失败，如果检测到bug，task会执行失败，这里设置true会让task继续执行
    ignoreFailures = false
    //分析等级：min  default   max
    effort = "max"
    //检测bug的等级：low   medium  high，等级越高检测越严格
    reportLevel = "high"
    //exclude Filter路径
    excludeFilter file('../findbugs-android-exclude.xml')
}

task findbugs(type: FindBugs,dependsOn:'assembleDebug') {

    ignoreFailures= true
    effort= "default"
    reportLevel= "high"
    println( "$project.buildDir")
    classes = files("$project.buildDir/intermediates/classes")
    source= fileTree("src/main/java/")
    classpath= files()
    reports{
        xml.enabled=false
        html.enabled=true
        xml {
            destination "$project.buildDir/findbugs.xml"
        }
        html{
            destination "$project.buildDir/findbugs.html"
        }
    }
}
```
##### 执行命令：（以mac为例，Windows下使用gradlew，[参考链接](https://developer.android.com/studio/build/building-cmdline?hl=zh-CN)）

```
./gradlew findbugs
```

#### 结果示例
[以好运为例](http://localhost:63342/6a95tteps56l59rrjbk9bamf539fk4eq4ne9j/FYApp/findbugs.html#SE_BAD_FIELD)

## PMD
An extensible multilanguage static code analyzer.([github](https://github.com/pmd/pmd))
#### 使用步骤
##### gradle 配置
```
apply plugin: 'pmd'
def configDir = "${project.rootDir}"

task pmd(type: Pmd) {
    //忽略失败，如果设置为true，检测出bug会停止task
    ignoreFailures = false
    //filter路径
    ruleSetFiles = files("$configDir/pmd-ruleset.xml")
    ruleSets = []
    //检测资源路径
    source 'src/main/java'
    //排除项
    exclude '**/gen/**'

    //判断是否是git pre-commit hook触发的pmd
    if (project.hasProperty('checkCommit') && project.property("checkCommit")) {
        def ft = filterCommitter(getChangeFiles())
        def includeList = new ArrayList<String>()
        for (int i = 0; i < ft.size(); i++) {
            String spliter = ft.getAt(i)
            String[] spliterlist = spliter.split("/")
            String fileName = spliterlist[spliterlist.length - 1]
            includeList.add("**/" + fileName)
        }
        if (includeList.size() == 0) {
            exclude '**/*.java'
        } else {
            include includeList
        }
    } else {
        include '**/*.java'
    }

    reports {
        xml.enabled = false
        html.enabled = true
        xml {
            destination "$configDir/pmd.xml"
        }
        html {
            destination "$configDir/pmd.html"
        }
    }
}
```
##### 执行命令
```
./gradlew pmd
```
#### 结果示例
[以好运为例](http://localhost:63342/6a95tteps56l59rrjbk9bamf539fk4eq4ne9j/FYApp/pmd.html)
## Lint
#### 简介
Android Studio 提供一个名为 Lint 的代码扫描工具，可帮助您发现并纠正代码结构质量的问题，而无需实际执行该应用，也不必编写测试用例。该工具会报告其检测到的每个问题并提供该问题的描述消息和严重级别，以便您可以快速确定需要优先进行哪些关键改进。此外，您可以调低问题的严重级别，忽略与项目无关的问题，也可以调高严重级别，以突出特定问题。

Lint 工具可检查您的 Android 项目源文件是否包含潜在错误，以及在正确性、安全性、性能、易用性、便利性和国际化方面是否需要优化改进。在使用 Android Studio 时，配置的 Lint 和 IDE 检查会在您每次构建应用时运行。不过，您可以手动运行检查或从命令行运行 Lint。（[摘自Developers网站](https://developer.android.com/studio/write/lint.html)）
#### gradle 配置
```
lintOptions {
    // Turns off checks for the issue IDs you specify.
    disable 'TypographyFractions','TypographyQuotes'
    // Turns on checks for the issue IDs you specify. These checks are in
    // addition to the default lint checks.
    enable 'RtlHardcoded','RtlCompat', 'RtlEnabled'
    // To enable checks for only a subset of issue IDs and ignore all others,
    // list the issue IDs with the 'check' property instead. This property overrides
    // any issue IDs you enable or disable using the properties above.
    check 'NewApi', 'InlinedApi'
    // If set to true, turns off analysis progress reporting by lint.
    quiet true
    // if set to true (default), stops the build if errors are found.
    abortOnError false
    // if true, only report errors.
    ignoreWarnings true
  }
```
[更多配置](http://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.LintOptions.html)
#### 执行命令
```
./gradlew lint
```


## 自定义lint
### 主要API
自定义Lint开发需要调用Lint提供的API，最主要的几个API如下。

Issue：表示一个Lint规则。例如调用Toast.makeText()方法后，没有调用Toast.show()方法将其显示。

IssueRegistry：用于注册要检查的Issue列表。自定义Lint需要生成一个jar文件，其Manifest指向IssueRegistry类。

Detector：用于检测并报告代码中的Issue。每个Issue包含一个Detector。

Scope：声明Detector要扫描的代码范围，例如Java源文件、XML资源文件、Gradle文件等。每个Issue可包含多个Scope。

### Scanner
Scanner：用于扫描并发现代码中的Issue。每个Detector可以实现一到多个Scanner。自定义Lint开发过程中最主要的工作就是实现Scanner。
Scanner
Lint中包括多种类型的Scanner如下，其中最常用的是扫描Java源文件和XML文件的Scanner。

JavaScanner / JavaPsiScanner / UastScanner：扫描Java源文件
XmlScanner：扫描XML文件
ClassScanner：扫描class文件
BinaryResourceScanner：扫描二进制资源文件
ResourceFolderScanner：扫描资源文件夹
GradleScanner：扫描Gradle脚本
OtherFileScanner：扫描其他类型文件

扫描Java原文件的Scanner，JavaScanner、JavaPsiScanner、UastScanner（支持java，kotlin）

### 代码示例：

### 改善
* gradle plugin封装检查配置
* lint规则封装maven


