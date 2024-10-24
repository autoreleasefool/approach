import Foundation

extension URL {
	public var relativeSQLiteFileUrls: [URL] {
		[
			self,
			self.deletingLastPathComponent().appending(path: self.lastPathComponent + "-shm"),
			self.deletingLastPathComponent().appending(path: self.lastPathComponent + "-wal"),
		]
	}
}
