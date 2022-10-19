import Dependencies
import FileManagerServiceInterface
import Foundation

extension FileManagerService: DependencyKey {
	public static let liveValue: Self = {
		return Self(
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
			}
		)
	}()
}
