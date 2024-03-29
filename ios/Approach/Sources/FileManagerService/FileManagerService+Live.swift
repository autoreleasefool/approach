import Dependencies
import FileManagerServiceInterface
import Foundation
import ZIPFoundation

extension FileManagerService: DependencyKey {
	public static var liveValue: Self = {
		@Sendable func getTemporaryDirectory() -> URL {
			FileManager.default.temporaryDirectory
		}

		return Self(
			getFileContents: { url in
				try Data(contentsOf: url)
			},
			getUserDirectory: {
				try FileManager.default
					.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
			},
			getTemporaryDirectory: getTemporaryDirectory,
			createDirectory: { url in
				try FileManager.default
					.createDirectory(at: url, withIntermediateDirectories: true)
			},
			remove: { url in
				try FileManager.default
					.removeItem(at: url)
			},
			exists: { url in
				FileManager.default
					.fileExists(atPath: url.absoluteString)
			},
			getZip: { urls, fileName in
				let archivePath = getTemporaryDirectory().appending(path: fileName)
				let archive = try Archive(url: archivePath, accessMode: .create, pathEncoding: nil)

				for url in urls {
					try archive.addEntry(with: url.lastPathComponent, relativeTo: url.deletingLastPathComponent())
				}

				return archivePath
			}
		)
	}()
}
