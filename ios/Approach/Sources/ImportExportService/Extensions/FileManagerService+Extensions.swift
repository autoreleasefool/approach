import FileManagerPackageServiceInterface
import Foundation

extension FileManagerService {
	func removeIfExists(_ url: URL) throws {
		let exists = try self.exists(url)
		if exists {
			try self.remove(url)
		}
	}
}
