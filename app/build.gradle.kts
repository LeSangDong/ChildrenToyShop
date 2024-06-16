plugins {
    id("com.android.application")

    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.toysshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.toysshop"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }


}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    
    //cirlce indicator3
    implementation ("me.relex:circleindicator:2.1.6")
    //navigation fragment
    implementation ("androidx.navigation:navigation-fragment:2.7.7")
    implementation ("androidx.navigation:navigation-ui:2.7.7")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //lottie
    implementation ("com.airbnb.android:lottie:6.4.1")
    //google map
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")


    //room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    //event bus
    implementation("org.greenrobot:eventbus:3.3.1")

    //refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    //loading facebook shimmer
    implementation ("com.facebook.shimmer:shimmer:0.1.0@aar")

    //circle imageview
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //touch imageview
    implementation("com.github.MikeOrtiz:TouchImageView:1.4.1")

    //gson
    implementation ("com.google.code.gson:gson:2.11.0")



}


