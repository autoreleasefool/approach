import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("BowlersImportStep tests", .tags(.android, .imports))
struct BowlersImportStepTests {
	let step = AndroidApproachV5SQLiteImporter.BowlersImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)
	}

	// MARK: Test Properties

	@Test("Imports properties")
	func importsProperties() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					bowlers(id, name, kind, archived_on)
				VALUES
					('\(bowlerId)', 'Joseph', 'PLAYABLE', 123000);
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let bowler = try iOSDb.read {
			try Bowler.Database.fetchOneGuaranteed($0, id: bowlerId)
		}

		#expect(bowler.id == bowlerId)
		#expect(bowler.name == "Joseph")
		#expect(bowler.kind == .playable)
		#expect(bowler.archivedOn == Date(timeIntervalSince1970: 123))
	}

	@Test("Imports kind", arguments: zip(["PLAYABLE", "OPPONENT"], Bowler.Kind.allCases))
	func importsKind(from: String, to: Bowler.Kind) throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					bowlers(id, name, kind)
				VALUES
					('\(bowlerId)', 'Joseph', '\(from)');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let bowler = try iOSDb.read {
			try Bowler.Database.fetchOneGuaranteed($0, id: bowlerId)
		}

		#expect(bowler.id == bowlerId)
		#expect(bowler.kind == to)
	}
}
