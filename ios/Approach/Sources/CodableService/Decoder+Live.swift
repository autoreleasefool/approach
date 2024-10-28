import CodableServiceInterface
import Dependencies
import Foundation

extension DecoderService: DependencyKey {
	public static var liveValue: DecoderService {
		Self(JSONDecoder())
	}
}
