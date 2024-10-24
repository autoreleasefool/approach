import DatabaseModelsLibrary
import Dependencies
import DependenciesTestSupport
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("GearImportStep tests", .tags(.android, .imports))
struct GearImportStepTests {
	let preSteps = [AndroidApproachV5SQLiteImporter.BowlersImportStep()]
	let step = AndroidApproachV5SQLiteImporter.GearImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let bowlerId = Bowler.ID()
	let gearId = Gear.ID()

	// MARK: Intialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with a bowler
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					bowlers(id, name, kind)
				VALUES
					('\(bowlerId)', 'Joseph', 'PLAYABLE');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSdb in
				for preStep in preSteps {
					try preStep.performImport(from: androidDb, to: iOSdb)
				}
			}
		}
	}

	// MARK: Test Properties

	@Test("Imports properties")
	func importsProperties() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					gear(id, name, kind, avatar, owner_id)
				VALUES
					('\(gearId)', 'Yellow', 'BOWLING_BALL', 'Yellow;248,198,164;255,253,53', '\(bowlerId)');
				"""
			)
		}

		try withDependencies {
			$0.uuid = .incrementing
		} operation: {
			try androidDb.read { androidDb in
				try iOSDb.write { iOSDb in
					try step.performImport(from: androidDb, to: iOSDb)
				}
			}
		}

		let avatar = try iOSDb.read {
			try Avatar.Database.fetchOneGuaranteed($0, id: UUID(0))
		}

		let gear = try iOSDb.read {
			try Gear.Database.fetchOneGuaranteed($0, id: gearId)
		}

		#expect(gear.id == gearId)
		#expect(gear.name == "Yellow")
		#expect(gear.kind == .bowlingBall)
		#expect(gear.bowlerId == bowlerId)
		#expect(gear.avatarId == UUID(0))
		#expect(avatar.id == UUID(0))
		#expect(avatar.value == .text(
			"Yellow",
			.gradient(
				.init(248.0 / 255, 198.0 / 255, 164.0 / 255),
				.init(255.0 / 255, 253.0 / 255, 53.0 / 255)
			)
		))
	}

	@Test("Imports kind", arguments: zip(["SHOES", "BOWLING_BALL", "TOWEL", "OTHER"], Gear.Kind.allCases))
	func importsKind(from: String, to: Gear.Kind) throws {
		let gearId = Gear.ID()

		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					gear(id, name, kind, avatar)
				VALUES
					('\(gearId)', 'Yellow', '\(from)', 'Yellow;248,198,164;255,253,53');
				"""
			)
		}

		try withDependencies {
			$0.uuid = .incrementing
		} operation: {
			try androidDb.read { androidDb in
				try iOSDb.write { iOSDb in
					try step.performImport(from: androidDb, to: iOSDb)
				}
			}
		}

		let gear = try iOSDb.read {
			try Gear.Database.fetchOneGuaranteed($0, id: gearId)
		}

		#expect(gear.id == gearId)
		#expect(gear.kind == to)
	}
}
