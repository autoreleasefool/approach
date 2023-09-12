import GRDB
import ModelsLibrary

extension Gear.Database {
	public static let bowler = belongsTo(Bowler.Database.self)

	public static let bowlerPreferredGear = hasMany(BowlerPreferredGear.Database.self)
	public static let preferredBy = hasMany(
		Bowler.Database.self,
		through: bowlerPreferredGear,
		using: BowlerPreferredGear.Database.bowler
	)
}
