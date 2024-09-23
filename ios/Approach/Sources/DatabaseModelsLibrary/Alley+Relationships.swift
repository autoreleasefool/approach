@preconcurrency import GRDB
import ModelsLibrary

extension Alley.Database {
	public static let lanes = hasMany(Lane.Database.self)
	public static let location = belongsTo(Location.Database.self)
}
