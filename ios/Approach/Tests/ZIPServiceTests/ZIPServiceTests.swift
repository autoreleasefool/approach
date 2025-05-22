import Dependencies
import FileManagerPackageServiceInterface
import Foundation
import Testing
import TestUtilitiesLibrary
@testable import ZIPService
@testable import ZIPServiceInterface

@Suite("ZIPService", .tags(.service), .serialized)
final class ZIPServiceTests: Sendable {
	private let testFileContents: [String] = [
		"file1",
		"file2",
		"file3",
	]

	private var testFiles: [URL] { testFileContents.map { temporaryDirectory.appending(path: $0) } }

	private let temporaryDirectory = FileManager.default
		.temporaryDirectory
		.appending(path: "ZIPServiceTests")

	init() throws {
		if !FileManager.default.fileExists(atPath: temporaryDirectory.path()) {
			try FileManager.default.createDirectory(at: temporaryDirectory, withIntermediateDirectories: true)
		}

		// Create test files
		for (file, contents) in zip(testFiles, testFileContents) {
			try contents.data(using: .utf8)?.write(to: file)
		}
	}

	deinit {
		// Clean up test files
		try? FileManager.default.removeItem(at: temporaryDirectory)
	}

	@Test("Creates an archive", .tags(.unit))
	func createsAnArchive() async throws {
		@Dependency(ZIPService.self) var zip

		let zipFileName = "createsAnArchive.zip"

		try withDependencies {
			$0[ZIPService.self] = .liveValue
			// swiftlint:disable:next unowned_variable_capture
			$0.fileManager.getTemporaryDirectory = { @Sendable [unowned self] in temporaryDirectory }
		} operation: {
			try zip.zipContents(ofUrls: testFiles, to: zipFileName)
		}

		let zipExists = FileManager.default
			.fileExists(atPath: temporaryDirectory.appending(path: zipFileName).path())
		#expect(zipExists == true)
	}

	@Test("Creates an empty archive", .tags(.unit))
	func createsAnEmptyArchive() async throws {
		@Dependency(ZIPService.self) var zip

		let zipFileName = "emptyArchive.zip"

		try withDependencies {
			$0[ZIPService.self] = .liveValue
			// swiftlint:disable:next unowned_variable_capture
			$0.fileManager.getTemporaryDirectory = { @Sendable [unowned self] in temporaryDirectory }
		} operation: {
			try zip.zipContents(ofUrls: [], to: zipFileName)
		}

		let zipExists = FileManager.default
			.fileExists(atPath: temporaryDirectory.appending(path: zipFileName).path())
		#expect(zipExists == true)
	}

	@Test("Unzips an archive", .tags(.unit))
	func unzipsAnArchive() async throws {
		@Dependency(ZIPService.self) var zip

		let zipFile = temporaryDirectory.appending(path: "unzip.zip")
		try FileManager.default.zipItem(at: testFiles[0], to: zipFile)

		let unzipped = try withDependencies {
			$0.uuid = .constant(UUID(0))
			$0[ZIPService.self] = .liveValue
			// swiftlint:disable:next unowned_variable_capture
			$0.fileManager.getTemporaryDirectory = { @Sendable [unowned self] in temporaryDirectory }
			$0.fileManager.createDirectory = {
				try FileManager.default.createDirectory(at: $0, withIntermediateDirectories: true)
			}
		} operation: {
			try zip.unZipContents(of: zipFile)
		}

		let unzippedFile = try #require(FileManager.default.contentsOfDirectory(atPath: unzipped.path()).first)
		let unzippedUrl = unzipped.deletingLastPathComponent().appending(path: unzippedFile)

		let unzippedData = try Data(contentsOf: unzippedUrl)
		let unzippedContents = String(data: unzippedData, encoding: .utf8)

		#expect(unzippedContents == "file1")
	}

	@Test("Throws an error unzipping non-archive file", .tags(.unit))
	func throwsAnErrorUnzipping() async throws {
		@Dependency(ZIPService.self) var zip

		#expect(throws: Error.self) {
			try withDependencies {
				$0.uuid = .constant(UUID(0))
				$0[ZIPService.self] = .liveValue
				// swiftlint:disable:next unowned_variable_capture
				$0.fileManager.getTemporaryDirectory = { @Sendable [unowned self] in temporaryDirectory }
				$0.fileManager.createDirectory = {
					try FileManager.default.createDirectory(at: $0, withIntermediateDirectories: true)
				}
			} operation: {
				try zip.unZipContents(of: self.testFiles[0])
			}
		}
	}
}
