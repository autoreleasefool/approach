import Dependencies
import Foundation

public struct FileManagerService {
	public var getUserDirectory: @Sendable () throws -> URL
	public var createDirectory: @Sendable (URL) throws -> Void
	public var remove: @Sendable (URL) throws -> Void
	public var exists: @Sendable (URL) -> Bool

	public init(
		getUserDirectory: @escaping @Sendable () throws -> URL,
		createDirectory: @escaping @Sendable (URL) throws -> Void,
		remove: @escaping @Sendable (URL) throws -> Void,
		exists: @escaping @Sendable (URL) -> Bool
	) {
		self.getUserDirectory = getUserDirectory
		self.createDirectory = createDirectory
		self.remove = remove
		self.exists = exists
	}
}

extension FileManagerService: TestDependencyKey {
	public static var testValue = Self(
		getUserDirectory: { unimplemented("\(Self.self).getUserDirectory") },
		createDirectory: { _ in unimplemented("\(Self.self).createDirectory") },
		remove: { _ in unimplemented("\(Self.self).remove") },
		exists: { _ in unimplemented("\(Self.self).exists") }
	)
}

extension DependencyValues {
	public var fileManagerService: FileManagerService {
		get { self[FileManagerService.self] }
		set { self[FileManagerService.self] = newValue }
	}
}
