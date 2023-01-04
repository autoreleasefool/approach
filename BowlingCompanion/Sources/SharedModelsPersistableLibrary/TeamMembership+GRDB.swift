import GRDB
import SharedModelsLibrary

extension TeamMembership: PersistableSQL {
	public func update(_ db: Database) throws {
		try db.execute(sql: "DELETE FROM teamBowlers WHERE team = ?", arguments: [team])

		var statements = ""
		var arguments: [(any DatabaseValueConvertible)?] = []
		members.forEach {
			statements.append("INSERT INTO teamBowlers (team, bowler) VALUES (?, ?); ")
			arguments.append(contentsOf: [team, $0.id])
		}

		try db.execute(sql: statements, arguments: StatementArguments(arguments))
	}
}
