import Foundation
import SharedModelsLibrary

extension Alley {
	public static func mock(
		id: UUID,
		name: String = "Skyview",
		address: String? = nil,
		material: Alley.Material = .unknown,
		pinFall: Alley.PinFall = .unknown,
		mechanism: Alley.Mechanism = .unknown,
		pinBase: Alley.PinBase = .unknown
	) -> Alley {
		.init(
			id: id,
			name: name,
			address: address,
			material: material,
			pinFall: pinFall,
			mechanism: mechanism,
			pinBase: pinBase
		)
	}
}
