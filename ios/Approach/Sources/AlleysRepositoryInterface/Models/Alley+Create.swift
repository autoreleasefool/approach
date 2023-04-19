import Foundation
import ModelsLibrary

extension Alley {
	public typealias Create = Edit
}

extension Alley.Create {
	public static func `default`(withId: UUID) -> Self {
		.init(id: withId, name: "")
	}
}
