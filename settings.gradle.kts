plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "BreakpointTracker"

include(":frontend-server")
//include(":plugin")
include(":shared")