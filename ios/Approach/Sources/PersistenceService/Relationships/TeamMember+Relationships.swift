import GRDB
import SharedModelsLibrary

extension TeamMember {
	static let team = belongsTo(Team.self)
	static let bowler = belongsTo(Bowler.self)
}
