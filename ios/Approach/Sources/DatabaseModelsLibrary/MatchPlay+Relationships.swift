import GRDB
import ModelsLibrary

extension MatchPlay.Database {
	public static let opponentKey = ForeignKey(["opponentId"])
	public static let opponent = belongsTo(Bowler.Database.self, using: opponentKey)
}
