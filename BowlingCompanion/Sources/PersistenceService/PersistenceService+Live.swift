import Dependencies
import Foundation
import PersistenceServiceInterface

extension PersistenceService: DependencyKey {
	public static let liveValue: Self = {
		return Self(
			read: { block in
				block()
			},
			write: { block, onComplete in
				block()
				onComplete?(nil)
			}
		)
	}()
}
