@file:Suppress("UNUSED_VARIABLE")

import com.github.lamba92.firebasemultiplatform.build.firebase
import com.github.lamba92.firebasemultiplatform.build.firebaseKt
import com.github.lamba92.firebasemultiplatform.build.kotlinx

plugins {
    id("firebase-multiplatform-gradle-plugin")
}

kotlin.sourceSets {

    val coroutinesVersion: String by project
    val firebaseCommonAndroidVersion: String by project
    val firebaseJsVersion: String by project
    val firebaseKtVersion: String by project

    val commonMain by getting {
        dependencies {
            api(kotlin("stdlib-common"))
            api(kotlinx("coroutines-core-common", coroutinesVersion))
        }
    }

    val androidMain by getting {
        dependencies {

            api(kotlin("stdlib-jdk8"))

            api(kotlinx("coroutines-android", coroutinesVersion))
            api(firebase("common", firebaseCommonAndroidVersion))

        }
    }

    val iosArm64Main by getting {
        dependencies {
            api(kotlinx("coroutines-core-iosarm64", coroutinesVersion))
            api(firebaseKt("core-iosarm64", firebaseKtVersion))
        }
    }

    val iosX64Main by getting {
        dependencies {
            api(kotlinx("coroutines-core-iosx64", coroutinesVersion))
            api(firebaseKt("core-iosx64", firebaseKtVersion))
        }
    }

}
