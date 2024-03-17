import Dependencies
import Foundation
import RecentlyUsedServiceInterface

extension JSONEncoderService: DependencyKey {
	public static var liveValue: Self = {
		let encoder = JSONEncoder()
		return .init(encoder)
	}()
}
