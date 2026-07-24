// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "KluvsDesignSystem",
    platforms: [
        .iOS(.v17)
    ],
    products: [
        .library(
            name: "KluvsDesignSystem",
            targets: ["KluvsDesignSystem"]
        )
    ],
    targets: [
        .target(
            name: "KluvsDesignSystem",
            path: "Sources/KluvsDesignSystem"
        )
    ]
)
