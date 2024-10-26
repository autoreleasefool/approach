import Dependencies
import DependenciesMacros
import Foundation

@DependencyClient
public struct ZIPService: Sendable {
	public var zipContents: @Sendable (_ ofUrls: [URL], _ to: String) throws -> URL
	public var unZipContents: @Sendable (_ of: URL) throws -> URL
}

extension ZIPService: TestDependencyKey {
	public static var testValue: Self { Self() }
}
