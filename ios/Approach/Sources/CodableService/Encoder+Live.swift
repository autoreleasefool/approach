import CodableServiceInterface
import Dependencies
import Foundation

extension EncoderService: DependencyKey {
	public static var liveValue: Self {
		Self(JSONEncoder())
	}
}
