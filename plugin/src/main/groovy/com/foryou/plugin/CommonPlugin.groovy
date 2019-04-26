package com.foryou.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def reportsDir = "$project.rootDir/reports"
        def configDir = "$project.rootDir"
        def suffix = project.name

        if (project.hasProperty("android")) {
            project.android {
                lintOptions {
                    abortOnError false
                    xmlReport true
                    htmlReport true
                    htmlOutput project.file("$reportsDir/lint-result-${suffix}.html")
                    xmlOutput project.file("$reportsDir/lint-result-${suffix}.xml")
                }
            }
        }

//        project.apply plugin: 'findbugs'
//
//        project.findbugs {
//            toolVersion = "3.0.1"
//            ignoreFailures = true
//            effort = "max"
//            reportLevel = "high"
//            excludeFilter file("$configDir/findbugs-android-exclude.xml")
//        }
//
//        project.tasks.create("findbugs", FindBugs, dependsOn: 'assembleDebug') {
//            classes = files("${project.projectDir}/build/intermediates/javac")
//            source = fileTree("src/main/java/")
//            classpath = files()
//            reports {
//                xml.enabled = true
//                html.enabled = false
//                xml {
//                    destination "$reportsDir/findbugs-${suffix}.xml"
//                }
//                html {
//                    destination "$reportsDir/findbugs-${suffix}.html"
//                }
//            }
//        }
//
//        project.apply plugin: 'pmd'
//
//        project.tasks.create("pmd", Pmd) {
//            ignoreFailures = true
//            ruleSetFiles = files("$configDir/pmd-ruleset.xml")
//            ruleSets = []
//            source 'src/main/java'
//            exclude '**/gen/**'
//            include '**/*.java'
//
//            reports {
//                xml.enabled = true
//                html.enabled = false
//                xml {
//                    destination "$reportsDir/pmd-${suffix}.xml"
//                }
//                html {
//                    destination "$reportsDir/pmd-${suffix}.html"
//                }
//            }
//        }

    }
}