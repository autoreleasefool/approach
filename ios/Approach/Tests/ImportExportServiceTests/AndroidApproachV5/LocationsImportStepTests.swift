import DatabaseModelsLibrary
import Foundation
import GRDB
import GRDBDatabasePackageServiceInterface
import ModelsLibrary
import Testing

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("LocationsImportStep Tests", .tags(.android, .imports))
struct LocationsImportStepTests {
	let step = AndroidApproachV5SQLiteImporter.LocationsImportStep()
	let dbProvider: ImportTestDatabaseProvider

	var androidDb: DatabaseWriter { dbProvider.sourceDb.writer }
	var iOSDb: DatabaseWriter { dbProvider.destDb.writer }

	// MARK: Initialization

	init() throws {
		self.dbProvider = try ImportTestDatabaseProvider(source: .androidApproach(.v5), dest: .iOSApproach)
	}

	// MARK: Arguments

	struct FromLocation {
		let title: String
		let subtitle: String
		let latitude: Double
		let longitude: Double
	}

	struct ToLocation {
		let title: String
		let subtitle: String
		let latitude: Double
		let longitude: Double
	}

	// MARK: Test Properties

	@Test("Imports properties", arguments: zip(
		[
			FromLocation(title: "Grandview Lanes", subtitle: "Vancouver", latitude: 49.265201, longitude: -123.069897),
			FromLocation(title: "Skyview Lanes", subtitle: "Bolton", latitude: 43.879517, longitude: -79.738271),
		],
		[
			ToLocation(title: "Grandview Lanes", subtitle: "Vancouver", latitude: 49.265201, longitude: -123.069897),
			ToLocation(title: "Skyview Lanes", subtitle: "Bolton", latitude: 43.879517, longitude: -79.738271),
		]
	))
	func importsProperties(_ from: FromLocation, to: ToLocation) throws {
		let locationId = Location.ID()

		try androidDb.write {
			try $0.execute(
				sql: """
				INSERT INTO
					locations(id, title, subtitle, latitude, longitude)
				VALUES
					('\(locationId)', '\(from.title)', '\(from.subtitle)', \(from.latitude), \(from.longitude));
				"""
			)
		}

		try androidDb.read { androidDb in
			try iOSDb.write { iOSDb in
				try step.performImport(from: androidDb, to: iOSDb)
			}
		}

		let location = try iOSDb.read {
			try Location.Database.fetchOneGuaranteed($0, id: locationId)
		}

		#expect(location.title == to.title)
		#expect(location.subtitle == to.subtitle)
		#expect(location.latitude == to.latitude)
		#expect(location.longitude == to.longitude)
	}
}
