import Dependencies
import DependenciesMacros
import Foundation

@DependencyClient
public struct ZIPService {
	public var zipContents: @Sendable (_ ofUrls: [URL], _ to: String) throws -> URL
}

extension ZIPService: TestDependencyKey {
	public static var testValue = Self()
}
