import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("UnarchiveButton", .tags(.library), .snapshots(record: .failed))
struct UnarchiveButtonTests {

	@Test("Unarchive button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotUnarchiveButton() {
		let unarchiveButton = UnarchiveButton { }
			.frame(width: 100)
		assertSnapshot(of: unarchiveButton, as: .image)
	}
}
