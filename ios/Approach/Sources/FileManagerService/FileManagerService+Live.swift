import Dependencies
import FileManagerServiceInterface
import Foundation
import ZIPFoundation

public enum FileManagerServiceError: Error {
	case failedToCreateArchive
}

extension FileManagerService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			getFileContents: { url in
				try Data(contentsOf: url)
			},
			getUserDirectory: {
				try FileManager.default
					.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
			},
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
			getZip: { urls in
				let archivePath = FileManager.default.temporaryDirectory.appending(path: "approach_logs.zip")
				guard let archive = Archive(url: archivePath, accessMode: .create) else {
					throw FileManagerServiceError.failedToCreateArchive
				}

				for url in urls {
					try archive.addEntry(with: url.lastPathComponent, relativeTo: url.deletingLastPathComponent())
				}

				return archivePath
			}
		)
	}()
}
