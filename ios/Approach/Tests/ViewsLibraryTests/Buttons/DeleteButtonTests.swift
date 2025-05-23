import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("DeleteButton", .tags(.library), .snapshots(record: .failed))
struct DeleteButtonTests {

	@Test("Delete button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotDeleteButton() {
		let deleteButton = DeleteButton { }
		assertSnapshot(of: deleteButton, as: .image)
	}
}
