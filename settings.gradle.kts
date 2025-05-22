plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "BreakpointTracker"

include(":frontend-server")
//include(":plugin")
include(":shared")