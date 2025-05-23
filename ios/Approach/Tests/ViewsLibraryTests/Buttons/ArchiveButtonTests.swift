import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("ArchiveButton", .tags(.library), .snapshots(record: .failed))
struct ArchiveButtonTests {

	@Test("Archive button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotArchiveButton() {
		let archiveButton = ArchiveButton { }
			.frame(width: 100)
		assertSnapshot(of: archiveButton, as: .image)
	}
}
