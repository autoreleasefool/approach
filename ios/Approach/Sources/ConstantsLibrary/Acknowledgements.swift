import Dependencies
import Foundation

public struct Acknowledgement: Equatable, Sendable {
	public let name: String
	public let license: LicenseFile

	public var licenseText: String? {
		guard let path = Bundle.module.path(forResource: license.fileName, ofType: license.type),
					let contents = try? String(contentsOfFile: path, encoding: .utf8) else {
			return nil
		}

		return contents
	}
}

extension Acknowledgement {
	public struct LicenseFile: Equatable, Sendable {
		public let fileName: String
		public let type: String
	}
}

extension Acknowledgement {
	public static let all: [Acknowledgement] = [
		.init(name: "CocoaLumberjack", license: .init(fileName: "CocoaLumberjack", type: "txt")),
		.init(name: "GRDB.swift", license: .init(fileName: "GRDB", type: "txt")),
		.init(name: "SFSafeSymbols", license: .init(fileName: "SFSafeSymbols", type: "txt")),
		.init(name: "SnapshotTesting", license: .init(fileName: "SnapshotTesting", type: "txt")),
		.init(name: "Swift Algorithms", license: .init(fileName: "SwiftAlgorithms", type: "txt")),
		.init(name: "Swift Async Algorithms", license: .init(fileName: "SwiftAsyncAlgorithms", type: "txt")),
		.init(name: "Swift Dependencies", license: .init(fileName: "SwiftDependencies", type: "txt")),
		.init(name: "Swift Identified Collections", license: .init(fileName: "SwiftIdentifiedCollections", type: "txt")),
		.init(name: "The Composable Architecture", license: .init(fileName: "ComposableArchitecture", type: "txt")),
		.init(name: "WiggleAnimationModifier", license: .init(fileName: "WiggleAnimationModifier", type: "txt")),
		.init(name: "XCTestDynamicOverlay", license: .init(fileName: "XCTestDynamicOverlay", type: "txt")),
		.init(name: "ZIPFoundation", license: .init(fileName: "ZIPFoundation", type: "txt")),
	]
}
