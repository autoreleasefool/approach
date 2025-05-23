import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("AddButton", .tags(.library), .snapshots(record: .failed))
struct AddButtonTests {

	@Test("Add button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotAddButton() {
		let addButton = AddButton { }
		assertSnapshot(of: addButton, as: .image)
	}
}
