import XCTest
@testable import FileManagerService
@testable import FileManagerServiceInterface

final class FileManagerServiceTests: XCTestCase {

	func getTempFolder() throws -> URL {
		try FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
			.appendingPathComponent("approach-tmp")
	}

//	override func tearDownWithError() throws {
//		try? FileManager.default.removeItem(at: try getTempFolder())
//		try super.tearDownWithError()
//	}

	func testGetUserDirectory() throws {
		let fileManager: FileManagerService = .liveValue
		XCTAssertEqual(
			try getTempFolder(),
			try fileManager.getUserDirectory()
				.appendingPathComponent("approach-tmp")
		)
	}

	// FIXME: broken test
	func skip_testExists() throws {
		let fileManager: FileManagerService = .liveValue
		let tempFolder = try getTempFolder()
		print(tempFolder)

		XCTAssertFalse(fileManager.exists(tempFolder))
		XCTAssertNoThrow(try fileManager.createDirectory(tempFolder))
		XCTAssertTrue(fileManager.exists(tempFolder))
	}

	// FIXME: broken test
	func skip_testCreateDirectory() throws {
		let fileManager: FileManagerService = .liveValue
		let tempFolder = try getTempFolder()

		XCTAssertNoThrow(try fileManager.createDirectory(tempFolder))
		XCTAssertTrue(fileManager.exists(tempFolder))
	}

	// FIXME: broken test
	func skip_testRemove() throws {
		let fileManager: FileManagerService = .liveValue
		let tempFolder = try getTempFolder()

		XCTAssertNoThrow(try fileManager.createDirectory(tempFolder))
		XCTAssertTrue(fileManager.exists(tempFolder))
		XCTAssertNoThrow(try fileManager.remove(tempFolder))
		XCTAssertFalse(fileManager.exists(tempFolder))
	}
}
