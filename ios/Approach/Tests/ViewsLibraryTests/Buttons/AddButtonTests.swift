import SnapshotTesting
import SwiftUI
import Testing
@testable import ViewsLibrary

@Suite(.snapshots(record: .missing))
struct AddButtonTests {
	@Test("Add button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotAddButton() {
		let addButton = AddButton { }
		assertSnapshot(of: addButton, as: .image)
	}
}
