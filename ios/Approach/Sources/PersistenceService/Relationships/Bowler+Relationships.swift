import GRDB
import SharedModelsLibrary

extension Bowler {
	static let leagues = hasMany(League.self)
	static let gear = hasMany(Gear.self)

	var leagues: QueryInterfaceRequest<League> {
		request(for: Bowler.leagues)
	}

	var gear: QueryInterfaceRequest<Gear> {
		request(for: Bowler.gear)
	}
}
