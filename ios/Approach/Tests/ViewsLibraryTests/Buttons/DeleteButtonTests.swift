import SnapshotTesting
import SwiftUI
import Testing
@testable import ViewsLibrary

@Suite(.snapshots(record: .missing))
struct DeleteButtonTests {
	@Test("Delete button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotDeleteButton() {
		let deleteButton = DeleteButton { }
		assertSnapshot(of: deleteButton, as: .image)
	}
}
