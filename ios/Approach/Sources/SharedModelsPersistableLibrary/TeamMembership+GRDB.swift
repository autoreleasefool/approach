import GRDB
import SharedModelsLibrary

extension TeamMembership: PersistableSQL {
	public func save(_ db: Database) throws {
		try db.execute(sql: "DELETE FROM teamMember WHERE team = ?", arguments: [team])

		var statements = ""
		var arguments: [(any DatabaseValueConvertible)?] = []
		for member in members {
			statements.append("INSERT INTO teamMember (team, bowler) VALUES (?, ?); ")
			arguments.append(contentsOf: [team, member.id])
		}

		try db.execute(sql: statements, arguments: StatementArguments(arguments))
	}
}
