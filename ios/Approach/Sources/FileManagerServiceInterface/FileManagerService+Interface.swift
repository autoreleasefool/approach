import Dependencies
import Foundation

public struct FileManagerService {
	public var getFileContents: @Sendable (URL) throws -> Data
	public var getUserDirectory: @Sendable () throws -> URL
	public var getTemporaryDirectory: @Sendable () -> URL
	public var createDirectory: @Sendable (URL) throws -> Void
	public var remove: @Sendable (URL) throws -> Void
	public var exists: @Sendable (URL) -> Bool
	public var getZip: @Sendable ([URL], String) throws -> URL

	public init(
		getFileContents: @escaping @Sendable (URL) throws -> Data,
		getUserDirectory: @escaping @Sendable () throws -> URL,
		getTemporaryDirectory: @escaping @Sendable () -> URL,
		createDirectory: @escaping @Sendable (URL) throws -> Void,
		remove: @escaping @Sendable (URL) throws -> Void,
		exists: @escaping @Sendable (URL) -> Bool,
		getZip: @escaping @Sendable ([URL], String) throws -> URL
	) {
		self.getFileContents = getFileContents
		self.getUserDirectory = getUserDirectory
		self.getTemporaryDirectory = getTemporaryDirectory
		self.createDirectory = createDirectory
		self.remove = remove
		self.exists = exists
		self.getZip = getZip
	}

	public func zipContents(ofUrls: [URL], to: String) throws -> URL {
		try self.getZip(ofUrls, to)
	}
}

extension FileManagerService: TestDependencyKey {
	public static var testValue = Self(
		getFileContents: { _ in unimplemented("\(Self.self).getFileContents") },
		getUserDirectory: { unimplemented("\(Self.self).getUserDirectory") },
		getTemporaryDirectory: { unimplemented("\(Self.self).getTemporaryDirectory") },
		createDirectory: { _ in unimplemented("\(Self.self).createDirectory") },
		remove: { _ in unimplemented("\(Self.self).remove") },
		exists: { _ in unimplemented("\(Self.self).exists") },
		getZip: { _, _ in unimplemented("\(Self.self).getZip") }
	)
}
