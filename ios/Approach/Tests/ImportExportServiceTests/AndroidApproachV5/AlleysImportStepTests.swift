import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing
import TestUtilitiesLibrary

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("AlleysImportStep", .tags(.android, .imports, .grdb, .service))
struct AlleysImportStepTests {
	let preSteps = [AndroidApproachV5SQLiteImporter.LocationsImportStep()]
	let step = AndroidApproachV5SQLiteImporter.AlleysImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	let locationId = Location.ID()
	let alleyId = Alley.ID()

	// MARK: Initialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)

		// Set up every test with a location
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					locations(id, title, subtitle, latitude, longitude)
				VALUES
					('\(locationId)', 'Grandview', 'Lanes', 49.265201, -123.069897);
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

	@Test("Imports properties", .tags(.unit))
	func importsProperties() throws {
		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					alleys(id, name, location_id)
				VALUES
					('\(alleyId)', 'Grandview Lanes', '\(locationId)');
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let alley = try iOSDb.read {
			try Alley.Database.fetchOneGuaranteed($0, id: alleyId)
		}

		#expect(alley.id == alleyId)
		#expect(alley.name == "Grandview Lanes")
		#expect(alley.locationId == locationId)
	}

	@Test(
		"Imports materials",
		.tags(.unit),
		arguments: zip([nil, "SYNTHETIC", "WOOD"], [nil] + Alley.Material.allCases)
	)
	func importsMaterials(from: String?, to: Alley.Material?) throws {
		let alleyId = Alley.ID()

		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					alleys(id, name, material)
				VALUES
					('\(alleyId)', 'Grandview Lanes', \(from == nil ? "NULL" : "'\(from!)'"));
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let alley = try iOSDb.read {
			try Alley.Database.fetchOneGuaranteed($0, id: alleyId)
		}

		#expect(alley.id == alleyId)
		#expect(alley.material == to)
	}

	@Test(
		"Imports mechanism",
		.tags(.unit),
		arguments: zip([nil, "DEDICATED", "INTERCHANGEABLE"], [nil] + Alley.Mechanism.allCases)
	)
	func importsMechanism(from: String?, to: Alley.Mechanism?) throws {
		let alleyId = Alley.ID()

		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					alleys(id, name, mechanism)
				VALUES
					('\(alleyId)', 'Grandview Lanes', \(from == nil ? "NULL" : "'\(from!)'"));
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let alley = try iOSDb.read {
			try Alley.Database.fetchOneGuaranteed($0, id: alleyId)
		}

		#expect(alley.id == alleyId)
		#expect(alley.mechanism == to)
	}

	@Test(
		"Imports pinFall",
		.tags(.unit),
		arguments: zip([nil, "FREE_FALL", "STRINGS"], [nil] + Alley.PinFall.allCases)
	)
	func importsPinFall(from: String?, to: Alley.PinFall?) throws {
		let alleyId = Alley.ID()

		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					alleys(id, name, pin_fall)
				VALUES
					('\(alleyId)', 'Grandview Lanes', \(from == nil ? "NULL" : "'\(from!)'"));
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let alley = try iOSDb.read {
			try Alley.Database.fetchOneGuaranteed($0, id: alleyId)
		}

		#expect(alley.id == alleyId)
		#expect(alley.pinFall == to)
	}

	@Test(
		"Imports pinBase",
		.tags(.unit),
		arguments: zip([nil, "BLACK", "WHITE", "OTHER"], [nil] + Alley.PinBase.allCases)
	)
	func importsPinBase(from: String?, to: Alley.PinBase?) throws {
		let alleyId = Alley.ID()

		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					alleys(id, name, pin_base)
				VALUES
					('\(alleyId)', 'Grandview Lanes', \(from == nil ? "NULL" : "'\(from!)'"));
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let alley = try iOSDb.read {
			try Alley.Database.fetchOneGuaranteed($0, id: alleyId)
		}

		#expect(alley.id == alleyId)
		#expect(alley.pinBase == to)
	}
}
