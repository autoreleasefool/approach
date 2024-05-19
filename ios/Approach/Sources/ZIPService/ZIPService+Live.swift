import Dependencies
import FileManagerPackageServiceInterface
import Foundation
import ZIPFoundation
import ZIPServiceInterface

extension ZIPService: DependencyKey {
	public static var liveValue: Self {
		Self(
			zipContents: { urls, fileName in
				@Dependency(\.fileManager) var fileManager
				let archivePath = try fileManager.getTemporaryDirectory().appending(path: fileName)
				let archive = try Archive(url: archivePath, accessMode: .create, pathEncoding: nil)

				for url in urls {
					try archive.addEntry(with: url.lastPathComponent, relativeTo: url.deletingLastPathComponent())
				}

				return archivePath
			}
		)
	}
}
