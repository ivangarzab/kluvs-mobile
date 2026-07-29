# kotlinx.serialization ships as a plain multiplatform jar (no AAR, so AGP never
# picks up embedded consumer rules). Without these, R8 strips the generated
# $serializer companions and JSON decoding of our DTOs breaks silently.
# https://github.com/Kotlin/kotlinx.serialization/blob/master/rules/common.pro
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Our own generated API DTOs (core/api/models) — every @Serializable class lives here.
-keep,includedescriptorclasses class com.ivangarzab.kluvs.api.models.**$$serializer { *; }
-keepclassmembers class com.ivangarzab.kluvs.api.models.** {
    *** Companion;
}
-keepclasseswithmembers class com.ivangarzab.kluvs.api.models.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# auth-kt persists its own UserSession/JWT model via kotlinx.serialization and
# ships no consumer rules. Scoped to auth (not all of supabase-kt) since that's
# the only module known to serialize its own internal state.
-keep,includedescriptorclasses class io.github.jan.supabase.auth.**$$serializer { *; }
-keepclassmembers class io.github.jan.supabase.auth.** {
    *** Companion;
}
-keepclasseswithmembers class io.github.jan.supabase.auth.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# androidx.security-crypto pulls in Google Tink, which references annotation-only
# classes (error-prone, JSR305) that aren't present at runtime. Safe to ignore.
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.concurrent.GuardedBy
