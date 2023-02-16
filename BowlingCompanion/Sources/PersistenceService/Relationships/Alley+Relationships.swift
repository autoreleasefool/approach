import GRDB
import SharedModelsLibrary

extension Alley {
	static let lanes = hasMany(Lane.self)

	var lanes: QueryInterfaceRequest<Lane> {
		request(for: Alley.lanes)
	}
}
